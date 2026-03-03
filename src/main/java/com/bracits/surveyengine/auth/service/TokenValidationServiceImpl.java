package com.bracits.surveyengine.auth.service;

import com.bracits.surveyengine.auth.dto.TokenValidationResult;
import com.bracits.surveyengine.auth.entity.AuthProfile;
import com.bracits.surveyengine.auth.entity.AuthenticationMode;
import com.bracits.surveyengine.auth.entity.ClaimMapping;
import com.bracits.surveyengine.auth.entity.FallbackPolicy;
import com.bracits.surveyengine.auth.repository.AuthProfileRepository;
import com.bracits.surveyengine.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;

/**
 * Implementation of {@link TokenValidationService}.
 * <p>
 * SRS §4.9.2: Supports three auth modes:
 * <ul>
 * <li>PUBLIC_ANONYMOUS — always valid, no token needed</li>
 * <li>SIGNED_LAUNCH_TOKEN — HMAC-SHA256 signature validation with expiry</li>
 * <li>EXTERNAL_SSO_TRUST — delegates to external JWKS (stub for MVP)</li>
 * </ul>
 * SRS §4.9.4: Deterministic error codes for invalid tokens.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TokenValidationServiceImpl implements TokenValidationService {

    private final AuthProfileRepository authProfileRepository;

    @Override
    @Transactional(readOnly = true)
    public TokenValidationResult validateToken(String tenantId, String token) {
        Optional<AuthProfile> profileOpt = authProfileRepository.findByTenantId(tenantId);

        // No auth profile configured → default to public anonymous
        if (profileOpt.isEmpty()) {
            return publicAnonymousResult();
        }

        AuthProfile profile = profileOpt.get();

        try {
            return switch (profile.getAuthMode()) {
                case PUBLIC_ANONYMOUS -> publicAnonymousResult();
                case SIGNED_LAUNCH_TOKEN -> validateSignedToken(profile, token);
                case EXTERNAL_SSO_TRUST -> validateExternalSso(profile, token);
            };
        } catch (Exception e) {
            log.warn("Token validation failed for campaign {}: {}", campaignId, e.getMessage());
            return handleFallback(profile, e.getMessage());
        }
    }

    private TokenValidationResult publicAnonymousResult() {
        return TokenValidationResult.builder()
                .valid(true)
                .respondentId("anonymous-" + UUID.randomUUID().toString().substring(0, 8))
                .mappedClaims(Map.of())
                .build();
    }

    /**
     * Validates a signed launch token.
     * Token format: base64(payload).base64(hmac-sha256-signature)
     * Payload JSON:
     * {"sub":"userId","exp":epochSeconds,"iss":"issuer","aud":"audience","nonce":"unique"}
     */
    private TokenValidationResult validateSignedToken(AuthProfile profile, String token) {
        if (token == null || token.isBlank()) {
            throw new RuntimeException("Token is required for signed launch mode");
        }

        String[] parts = token.split("\\.");
        if (parts.length != 2) {
            throw new RuntimeException("Token must have format: payload.signature");
        }

        String payloadBase64 = parts[0];
        String signatureBase64 = parts[1];

        // Verify HMAC signature
        String expectedSignature = hmacSha256(payloadBase64, profile.getSigningSecret());
        if (!signatureBase64.equals(expectedSignature)) {
            throw new RuntimeException("Token signature verification failed");
        }

        // Decode payload
        String payloadJson = new String(Base64.getDecoder().decode(payloadBase64), StandardCharsets.UTF_8);

        // Simple JSON parsing (production would use Jackson)
        String subject = extractJsonField(payloadJson, "sub");
        String expStr = extractJsonField(payloadJson, "exp");
        String issuer = extractJsonField(payloadJson, "iss");
        String audience = extractJsonField(payloadJson, "aud");

        // Validate issuer
        if (profile.getIssuer() != null && !profile.getIssuer().equals(issuer)) {
            throw new RuntimeException(
                    "Token issuer '%s' does not match expected '%s'".formatted(issuer, profile.getIssuer()));
        }

        // Validate audience
        if (profile.getAudience() != null && !profile.getAudience().equals(audience)) {
            throw new RuntimeException(
                    "Token audience '%s' does not match expected '%s'".formatted(audience, profile.getAudience()));
        }

        // Validate expiry with clock skew
        if (expStr != null) {
            long exp = Long.parseLong(expStr);
            long now = Instant.now().getEpochSecond();
            if (now > exp + profile.getClockSkewSeconds()) {
                throw new RuntimeException("Token has expired");
            }
        }

        // Map claims
        Map<String, String> mappedClaims = new HashMap<>();
        for (ClaimMapping cm : profile.getClaimMappings()) {
            String value = extractJsonField(payloadJson, cm.getExternalClaim());
            if (value != null) {
                mappedClaims.put(cm.getInternalField(), value);
            } else if (cm.isRequired()) {
                throw new RuntimeException(
                        "Required claim '%s' is missing from token".formatted(cm.getExternalClaim()));
            }
        }

        return TokenValidationResult.builder()
                .valid(true)
                .respondentId(subject)
                .email(mappedClaims.get("email"))
                .mappedClaims(mappedClaims)
                .build();
    }

    /**
     * External SSO validation stub — in production this would call JWKS endpoint
     * and validate the JWT/SAML assertion. For MVP, accepts tokens with valid
     * structure.
     */
    private TokenValidationResult validateExternalSso(AuthProfile profile, String token) {
        if (token == null || token.isBlank()) {
            return errorResult("AUTH_TOKEN_MISSING", "SSO token is required");
        }

        // MVP stub: SSO token is treated as a simple payload
        // Production would: fetch JWKS from profile.getJwksEndpoint(), validate JWT
        // signature
        log.info("External SSO validation for campaign {} (JWKS: {}) — MVP stub",
                profile.getCampaignId(), profile.getJwksEndpoint());

        return TokenValidationResult.builder()
                .valid(true)
                .respondentId("sso-user-" + token.hashCode())
                .mappedClaims(Map.of("authMode", "EXTERNAL_SSO_TRUST"))
                .build();
    }

    private TokenValidationResult handleFallback(AuthProfile profile, String errorMsg) {
        return switch (profile.getFallbackPolicy()) {
            case ANONYMOUS_FALLBACK -> {
                log.info("Falling back to anonymous access for campaign {}", profile.getCampaignId());
                yield publicAnonymousResult();
            }
            case SSO_REQUIRED -> errorResult("AUTH_SSO_REQUIRED",
                    "Authentication is required: " + errorMsg);
            case DISABLE_ON_FAILURE -> errorResult("AUTH_SURVEY_DISABLED",
                    "Survey access is disabled due to authentication failure");
        };
    }

    private TokenValidationResult errorResult(String code, String message) {
        return TokenValidationResult.builder()
                .valid(false)
                .errorCode(code)
                .errorMessage(message)
                .build();
    }

    private String hmacSha256(String data, String secret) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("HMAC-SHA256 computation failed", e);
        }
    }

    /** Simple JSON field extraction (avoids Jackson dependency in auth layer) */
    private String extractJsonField(String json, String field) {
        String search = "\"" + field + "\":";
        int idx = json.indexOf(search);
        if (idx == -1)
            return null;
        int valueStart = idx + search.length();
        // Skip whitespace
        while (valueStart < json.length() && json.charAt(valueStart) == ' ')
            valueStart++;
        if (valueStart >= json.length())
            return null;
        if (json.charAt(valueStart) == '"') {
            int valueEnd = json.indexOf('"', valueStart + 1);
            return json.substring(valueStart + 1, valueEnd);
        } else {
            // Number or other
            int valueEnd = valueStart;
            while (valueEnd < json.length() && json.charAt(valueEnd) != ',' && json.charAt(valueEnd) != '}') {
                valueEnd++;
            }
            return json.substring(valueStart, valueEnd).trim();
        }
    }
}
