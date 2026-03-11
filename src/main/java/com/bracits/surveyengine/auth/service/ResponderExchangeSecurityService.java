package com.bracits.surveyengine.auth.service;

import com.bracits.surveyengine.auth.config.ResponderSecurityProperties;
import com.bracits.surveyengine.campaign.entity.Campaign;
import com.bracits.surveyengine.common.audit.AuditLogService;
import com.bracits.surveyengine.common.exception.BusinessException;
import com.bracits.surveyengine.common.exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResponderExchangeSecurityService {

    private static final String RATE_LIMIT_KEY_PREFIX = "survey-engine:responder:exchange:rl";
    private static final String AUDIT_ENTITY = "ResponderAuthExchange";

    private final ResponderSecurityProperties responderSecurityProperties;
    private final AuditLogService auditLogService;
    private final ObjectMapper objectMapper;
    private final Optional<StringRedisTemplate> stringRedisTemplate;

    private final Map<String, LocalRateLimitWindow> localRateLimitWindows = new ConcurrentHashMap<>();

    public ExchangeSecurityContext enforceAndResolveContext(HttpServletRequest request, Campaign campaign) {
        String origin = resolveNormalizedOrigin(request).orElse(null);
        enforceOriginAllowlist(origin);

        String clientIp = resolveClientIp(request);
        enforceRateLimit(campaign.getTenantId(), campaign.getId(), clientIp);

        return new ExchangeSecurityContext(origin, clientIp);
    }

    public void auditSuccess(
            Campaign campaign,
            ExchangeSecurityContext securityContext,
            String respondentId,
            String email,
            String method) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("status", "SUCCESS");
        payload.put("origin", securityContext.origin());
        payload.put("method", method);
        payload.put("campaignId", campaign.getId());
        payload.put("respondentId", respondentId);
        payload.put("email", email);

        auditLogService.record(
                campaign.getTenantId(),
                AUDIT_ENTITY,
                campaign.getId().toString(),
                "RESPONDER_AUTH_EXCHANGE_SUCCEEDED",
                actor(respondentId),
                "Private responder auth exchange succeeded",
                null,
                toJson(payload),
                securityContext.clientIp());
    }

    public void auditFailure(
            Campaign campaign,
            ExchangeSecurityContext securityContext,
            String failureReason,
            String method,
            String errorCode) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("status", "FAILED");
        payload.put("origin", securityContext.origin());
        payload.put("method", method);
        payload.put("campaignId", campaign.getId());
        payload.put("errorCode", errorCode);
        payload.put("reason", failureReason);

        auditLogService.record(
                campaign.getTenantId(),
                AUDIT_ENTITY,
                campaign.getId().toString(),
                "RESPONDER_AUTH_EXCHANGE_FAILED",
                "RESPONDER_UNKNOWN",
                "Private responder auth exchange failed",
                null,
                toJson(payload),
                securityContext.clientIp());
    }

    private void enforceOriginAllowlist(String normalizedOrigin) {
        var exchangeSecurity = responderSecurityProperties.getExchange();
        if (!exchangeSecurity.isEnforceOriginAllowlist()) {
            return;
        }

        if (normalizedOrigin == null) {
            if (exchangeSecurity.isAllowMissingOrigin()) {
                return;
            }
            throw new BusinessException(ErrorCode.ACCESS_DENIED, "Missing Origin/Referer for responder auth exchange");
        }

        if (!exchangeSecurity.getAllowedOrigins().contains(normalizedOrigin)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED, "Origin is not allowed for responder auth exchange");
        }
    }

    private Optional<String> resolveNormalizedOrigin(HttpServletRequest request) {
        return normalizeOrigin(request.getHeader("Origin"))
                .or(() -> normalizeOriginFromReferer(request.getHeader("Referer")));
    }

    private Optional<String> normalizeOriginFromReferer(String referer) {
        if (referer == null || referer.isBlank()) {
            return Optional.empty();
        }
        try {
            URI uri = URI.create(referer.trim());
            if (uri.getScheme() == null || uri.getHost() == null) {
                return Optional.empty();
            }
            int port = uri.getPort();
            String normalized = port > 0
                    ? (uri.getScheme() + "://" + uri.getHost() + ":" + port)
                    : (uri.getScheme() + "://" + uri.getHost());
            return Optional.of(normalized.toLowerCase(Locale.ROOT));
        } catch (IllegalArgumentException ignored) {
            return Optional.empty();
        }
    }

    private Optional<String> normalizeOrigin(String origin) {
        if (origin == null || origin.isBlank()) {
            return Optional.empty();
        }
        try {
            URI uri = URI.create(origin.trim());
            if (uri.getScheme() == null || uri.getHost() == null || uri.getPath() != null && !uri.getPath().isBlank()) {
                return Optional.empty();
            }
            int port = uri.getPort();
            String normalized = port > 0
                    ? (uri.getScheme() + "://" + uri.getHost() + ":" + port)
                    : (uri.getScheme() + "://" + uri.getHost());
            return Optional.of(normalized.toLowerCase(Locale.ROOT));
        } catch (IllegalArgumentException ignored) {
            return Optional.empty();
        }
    }

    private void enforceRateLimit(String tenantId, UUID campaignId, String clientIp) {
        var exchangeSecurity = responderSecurityProperties.getExchange();
        if (!exchangeSecurity.isRateLimitEnabled()) {
            return;
        }

        int limit = Math.max(exchangeSecurity.getRateLimitPerMinute(), 1);
        long epochMinute = Instant.now().getEpochSecond() / 60;
        String key = RATE_LIMIT_KEY_PREFIX + ":" + tenantId + ":" + campaignId + ":" + clientIp + ":" + epochMinute;

        try {
            if (stringRedisTemplate.isPresent()) {
                Long current = stringRedisTemplate.get().opsForValue().increment(key);
                if (current != null && current == 1) {
                    stringRedisTemplate.get().expire(key, Duration.ofSeconds(90));
                }
                if (current != null && current > limit) {
                    throw new BusinessException(ErrorCode.QUOTA_EXCEEDED,
                            "Responder auth exchange rate limit exceeded. Please retry in a minute.");
                }
                return;
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.warn("Redis rate limit check failed, falling back to local limiter: {}", e.getMessage());
        }

        LocalRateLimitWindow window = localRateLimitWindows.compute(key, (k, existing) -> {
            if (existing == null || existing.expiresAtMillis() < System.currentTimeMillis()) {
                return new LocalRateLimitWindow(1, System.currentTimeMillis() + 90_000);
            }
            return new LocalRateLimitWindow(existing.count() + 1, existing.expiresAtMillis());
        });

        if (window.count() > limit) {
            throw new BusinessException(ErrorCode.QUOTA_EXCEEDED,
                    "Responder auth exchange rate limit exceeded. Please retry in a minute.");
        }
    }

    private String resolveClientIp(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            return xff.split(",")[0].trim();
        }
        String xri = request.getHeader("X-Real-IP");
        if (xri != null && !xri.isBlank()) {
            return xri.trim();
        }
        return request.getRemoteAddr() == null ? "UNKNOWN" : request.getRemoteAddr();
    }

    private String toJson(Map<String, Object> value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception e) {
            return "{}";
        }
    }

    private String actor(String respondentId) {
        if (respondentId == null || respondentId.isBlank()) {
            return "RESPONDER_UNKNOWN";
        }
        return "RESPONDER:" + respondentId;
    }

    public record ExchangeSecurityContext(String origin, String clientIp) {
    }

    private record LocalRateLimitWindow(int count, long expiresAtMillis) {
    }
}
