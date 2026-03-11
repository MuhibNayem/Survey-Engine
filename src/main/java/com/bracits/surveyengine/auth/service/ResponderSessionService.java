package com.bracits.surveyengine.auth.service;

import com.bracits.surveyengine.auth.config.ResponderSecurityProperties;
import com.bracits.surveyengine.auth.dto.ResponderAccessIdentity;
import com.bracits.surveyengine.auth.entity.ResponderSession;
import com.bracits.surveyengine.auth.repository.ResponderSessionRepository;
import com.bracits.surveyengine.campaign.entity.CampaignSettings;
import com.bracits.surveyengine.campaign.repository.CampaignSettingsRepository;
import com.bracits.surveyengine.common.exception.BusinessException;
import com.bracits.surveyengine.common.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

@Service
@RequiredArgsConstructor
public class ResponderSessionService {

    public static final String COOKIE_NAME = "responder_session";
    private static final int DEFAULT_TIMEOUT_MINUTES = 60;

    private final ResponderSessionRepository responderSessionRepository;
    private final CampaignSettingsRepository campaignSettingsRepository;
    private final ResponderSecurityProperties responderSecurityProperties;
    private final SecureRandom secureRandom = new SecureRandom();

    @Transactional
    public List<ResponseCookie> createSessionCookies(
            HttpServletRequest request,
            String tenantId,
            UUID campaignId,
            ResponderAccessIdentity identity) {
        responderSessionRepository.deleteByExpiresAtBefore(Instant.now());

        String rawToken = generateOpaqueToken();
        Instant now = Instant.now();
        Duration timeout = sessionTimeout(campaignId);

        responderSessionRepository.save(ResponderSession.builder()
                .sessionHash(hash(rawToken))
                .tenantId(tenantId)
                .campaignId(campaignId)
                .respondentId(identity.getRespondentId())
                .email(identity.getEmail())
                .expiresAt(now.plus(timeout))
                .lastAccessedAt(now)
                .createdAt(now)
                .build());

        return List.of(
                buildSessionCookie(request, rawToken, timeout),
                buildCsrfCookie(request, rawToken, timeout));
    }

    @Transactional
    public Optional<ResponderAccessIdentity> resolveIdentity(
            HttpServletRequest request,
            String tenantId,
            UUID campaignId) {
        responderSessionRepository.deleteByExpiresAtBefore(Instant.now());

        String rawToken = readCookieValue(request);
        if (rawToken == null) {
            return Optional.empty();
        }

        Optional<ResponderSession> optionalSession = responderSessionRepository.findBySessionHash(hash(rawToken));
        if (optionalSession.isEmpty()) {
            return Optional.empty();
        }

        ResponderSession session = optionalSession.get();
        if (session.getRevokedAt() != null
                || Instant.now().isAfter(session.getExpiresAt())
                || !Objects.equals(session.getTenantId(), tenantId)
                || !Objects.equals(session.getCampaignId(), campaignId)) {
            return Optional.empty();
        }

        Instant now = Instant.now();
        session.setLastAccessedAt(now);
        session.setExpiresAt(now.plus(sessionTimeout(campaignId)));
        responderSessionRepository.save(session);

        return Optional.of(ResponderAccessIdentity.builder()
                .respondentId(session.getRespondentId())
                .email(session.getEmail())
                .build());
    }

    public List<ResponseCookie> clearSessionCookies(HttpServletRequest request) {
        return List.of(
                ResponseCookie.from(COOKIE_NAME, "")
                        .httpOnly(true)
                        .secure(resolveSecureCookie(request))
                        .sameSite(responderSecurityProperties.getCookie().getSameSite())
                        .path("/")
                        .maxAge(Duration.ZERO)
                        .build(),
                ResponseCookie.from(responderSecurityProperties.getCsrf().getCookieName(), "")
                        .httpOnly(false)
                        .secure(resolveSecureCookie(request))
                        .sameSite(responderSecurityProperties.getCookie().getSameSite())
                        .path("/")
                        .maxAge(Duration.ZERO)
                        .build());
    }

    @Transactional
    public List<ResponseCookie> revokeSessionCookies(
            HttpServletRequest request,
            String tenantId,
            UUID campaignId) {
        String rawToken = readCookieValue(request);
        if (rawToken != null && !rawToken.isBlank()) {
            responderSessionRepository.findBySessionHash(hash(rawToken))
                    .filter(session -> session.getRevokedAt() == null)
                    .filter(session -> Objects.equals(session.getTenantId(), tenantId))
                    .filter(session -> Objects.equals(session.getCampaignId(), campaignId))
                    .ifPresent(session -> {
                        session.setRevokedAt(Instant.now());
                        responderSessionRepository.save(session);
                    });
        }
        return clearSessionCookies(request);
    }

    public void enforceCsrfForSessionMutation(HttpServletRequest request) {
        if (!responderSecurityProperties.getCsrf().isEnabled()) {
            return;
        }
        String rawToken = readCookieValue(request);
        if (rawToken == null || rawToken.isBlank()) {
            return;
        }

        String headerName = responderSecurityProperties.getCsrf().getHeaderName();
        String headerToken = request.getHeader(headerName);
        String cookieToken = readCookieByName(request, responderSecurityProperties.getCsrf().getCookieName());
        String expectedToken = buildCsrfToken(rawToken);

        if (headerToken == null || headerToken.isBlank()
                || cookieToken == null || cookieToken.isBlank()
                || !constantTimeEquals(headerToken, cookieToken)
                || !constantTimeEquals(headerToken, expectedToken)) {
            throw new BusinessException(
                    ErrorCode.ACCESS_DENIED,
                    "Invalid responder CSRF token");
        }
    }

    private Duration sessionTimeout(UUID campaignId) {
        return campaignSettingsRepository.findByCampaignId(campaignId)
                .map(CampaignSettings::getSessionTimeoutMinutes)
                .filter(value -> value != null && value > 0)
                .map(Duration::ofMinutes)
                .orElse(Duration.ofMinutes(DEFAULT_TIMEOUT_MINUTES));
    }

    private String readCookieValue(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return null;
        }
        for (var cookie : request.getCookies()) {
            if (COOKIE_NAME.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    private String readCookieByName(HttpServletRequest request, String cookieName) {
        if (request.getCookies() == null) {
            return null;
        }
        for (var cookie : request.getCookies()) {
            if (cookieName.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    private ResponseCookie buildSessionCookie(HttpServletRequest request, String rawToken, Duration timeout) {
        return ResponseCookie.from(COOKIE_NAME, rawToken)
                .httpOnly(true)
                .secure(resolveSecureCookie(request))
                .sameSite(responderSecurityProperties.getCookie().getSameSite())
                .path("/")
                .maxAge(timeout)
                .build();
    }

    private ResponseCookie buildCsrfCookie(HttpServletRequest request, String rawToken, Duration timeout) {
        return ResponseCookie.from(
                        responderSecurityProperties.getCsrf().getCookieName(),
                        buildCsrfToken(rawToken))
                .httpOnly(false)
                .secure(resolveSecureCookie(request))
                .sameSite(responderSecurityProperties.getCookie().getSameSite())
                .path("/")
                .maxAge(timeout)
                .build();
    }

    private String buildCsrfToken(String rawToken) {
        String secret = responderSecurityProperties.getCsrf().getSecret();
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] hash = mac.doFinal(rawToken.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to generate responder CSRF token", e);
        }
    }

    private boolean constantTimeEquals(String a, String b) {
        byte[] first = a.getBytes(StandardCharsets.UTF_8);
        byte[] second = b.getBytes(StandardCharsets.UTF_8);
        return MessageDigest.isEqual(first, second);
    }

    private boolean resolveSecureCookie(HttpServletRequest request) {
        if (responderSecurityProperties.getCookie().isSecure()) {
            return true;
        }
        return isSecure(request);
    }

    private boolean isSecure(HttpServletRequest request) {
        String forwardedProto = request.getHeader("X-Forwarded-Proto");
        if (forwardedProto != null) {
            return "https".equalsIgnoreCase(forwardedProto);
        }
        return request.isSecure();
    }

    private String generateOpaqueToken() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hash(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(bytes.length * 2);
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to hash responder session token", e);
        }
    }
}
