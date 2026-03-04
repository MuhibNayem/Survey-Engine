package com.bracits.surveyengine.auth.service;

import com.bracits.surveyengine.auth.dto.TokenValidationResult;
import com.bracits.surveyengine.auth.entity.AuthProfile;
import com.bracits.surveyengine.auth.entity.AuthTokenReplay;
import com.bracits.surveyengine.auth.entity.AuthenticationMode;
import com.bracits.surveyengine.auth.entity.ClaimMapping;
import com.bracits.surveyengine.auth.entity.FallbackPolicy;
import com.bracits.surveyengine.auth.repository.AuthProfileRepository;
import com.bracits.surveyengine.auth.repository.AuthTokenReplayRepository;
import com.bracits.surveyengine.common.exception.BusinessException;
import com.bracits.surveyengine.common.exception.ErrorCode;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.RSAPublicKeySpec;
import java.time.Instant;
import java.time.Duration;
import java.util.*;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Implementation of {@link TokenValidationService}.
 * <p>
 * SRS §4.9.2: Supports three auth modes:
 * <ul>
 * <li>PUBLIC_ANONYMOUS — always valid, no token needed</li>
 * <li>SIGNED_LAUNCH_TOKEN — HMAC-SHA256 signature validation with expiry</li>
 * <li>EXTERNAL_SSO_TRUST — validates external JWT tokens using JWKS</li>
 * </ul>
 * SRS §4.9.4: Deterministic error codes for invalid tokens.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TokenValidationServiceImpl implements TokenValidationService {

    private final AuthProfileRepository authProfileRepository;
    private final AuthTokenReplayRepository authTokenReplayRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final Cache<String, JwksKeySet> jwksCache = Caffeine.newBuilder()
            .maximumSize(256)
            .expireAfterWrite(Duration.ofMinutes(15))
            .build();

    @Override
    @Transactional
    public TokenValidationResult validateToken(String tenantId, String token) {
        return validateToken(tenantId, token, null);
    }

    @Override
    @Transactional
    public TokenValidationResult validateToken(String tenantId, String token, String expectedNonce) {
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
                case EXTERNAL_SSO_TRUST -> validateExternalSso(profile, token, expectedNonce);
            };
        } catch (Exception e) {
            log.warn("Token validation failed for tenant {}: {}", tenantId, e.getMessage());
            return handleFallback(profile, e.getMessage());
        }
    }

    private TokenValidationResult publicAnonymousResult() {
        return TokenValidationResult.builder()
                .valid(true)
                .respondentId("anon-" + UUID.randomUUID().toString().substring(0, 8))
                .mappedClaims(Map.of())
                .build();
    }

    /**
     * Validates a signed launch token.
     * Token format: base64url(header).base64url(payload).base64url(signature)
     * Signature: HMAC-SHA256 over "<header>.<payload>"
     */
    private TokenValidationResult validateSignedToken(AuthProfile profile, String token) {
        if (token == null || token.isBlank()) {
            throw new RuntimeException("Token is required for signed launch mode");
        }

        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            throw new RuntimeException("Token must have JWT format: header.payload.signature");
        }

        String headerBase64 = parts[0];
        String payloadBase64 = parts[1];
        String signatureBase64 = parts[2];
        String signingInput = headerBase64 + "." + payloadBase64;

        // Verify header algorithm
        Map<String, Object> header = parseJsonMap(base64UrlDecodeToString(headerBase64));
        String alg = Objects.toString(header.get("alg"), null);
        if (!"HS256".equals(alg)) {
            throw new RuntimeException("Unsupported JWT alg: " + alg);
        }

        // Verify HMAC signature (base64url)
        String expectedSignature = hmacSha256Base64Url(signingInput, profile.getSigningSecret());
        if (!signatureBase64.equals(expectedSignature)) {
            throw new RuntimeException("Token signature verification failed");
        }

        String payloadJson = base64UrlDecodeToString(payloadBase64);
        Map<String, Object> claims = parseJsonMap(payloadJson);

        String issuer = toStringClaim(claims.get("iss"));
        String audience = toStringClaim(claims.get("aud"));
        Long exp = toLongClaim(claims.get("exp"));
        Long iat = toLongClaim(claims.get("iat"));
        Long nbf = toLongClaim(claims.get("nbf"));
        String jti = toStringClaim(claims.get("jti"));

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
        if (exp == null) {
            throw new RuntimeException("exp claim is required");
        }
        long now = Instant.now().getEpochSecond();
        if (now > exp + profile.getClockSkewSeconds()) {
            throw new RuntimeException("Token has expired");
        }
        if (nbf != null && now + profile.getClockSkewSeconds() < nbf) {
            throw new RuntimeException("Token is not valid yet");
        }
        if (iat != null && now + profile.getClockSkewSeconds() < iat) {
            throw new RuntimeException("Token issued-at is in the future");
        }

        // Replay protection (mandatory for signed launch mode)
        if (jti == null || jti.isBlank()) {
            throw new RuntimeException("jti claim is required");
        }
        authTokenReplayRepository.deleteByExpiresAtBefore(Instant.now());
        if (authTokenReplayRepository.existsByTenantIdAndJti(profile.getTenantId(), jti)) {
            throw new RuntimeException("Token replay detected");
        }
        authTokenReplayRepository.save(AuthTokenReplay.builder()
                .tenantId(profile.getTenantId())
                .jti(jti)
                .expiresAt(Instant.ofEpochSecond(exp))
                .usedAt(Instant.now())
                .build());

        // Map claims
        MappedIdentity identity = resolveMappedIdentity(profile, claims);

        return TokenValidationResult.builder()
                .valid(true)
                .respondentId(identity.respondentId())
                .email(identity.email())
                .mappedClaims(identity.claims())
                .build();
    }

    private TokenValidationResult validateExternalSso(AuthProfile profile, String token, String expectedNonce) {
        if (token == null || token.isBlank()) {
            return errorResult("AUTH_TOKEN_MISSING", "SSO token is required");
        }

        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            throw new RuntimeException("External SSO token must be JWT format");
        }
        String headerJson = base64UrlDecodeToString(parts[0]);
        String payloadJson = base64UrlDecodeToString(parts[1]);
        Map<String, Object> header = parseJsonMap(headerJson);
        Map<String, Object> claims = parseJsonMap(payloadJson);
        String alg = toStringClaim(header.get("alg"));
        String signingInput = parts[0] + "." + parts[1];
        byte[] signature = Base64.getUrlDecoder().decode(parts[2]);

        if (!"RS256".equals(alg) && !"HS256".equals(alg)) {
            throw new RuntimeException("Unsupported external token alg: " + alg);
        }

        if ("HS256".equals(alg)) {
            String expected = hmacSha256Base64Url(signingInput, profile.getSigningSecret());
            if (!expected.equals(parts[2])) {
                throw new RuntimeException("External token HMAC signature verification failed");
            }
        } else {
            verifyRsaSignature(profile, header, signingInput, signature);
        }

        String issuer = toStringClaim(claims.get("iss"));
        if (profile.getIssuer() != null && !profile.getIssuer().equals(issuer)) {
            throw new RuntimeException("External token issuer mismatch");
        }

        if (profile.getAudience() != null && !audienceMatches(claims.get("aud"), profile.getAudience())) {
            throw new RuntimeException("External token audience mismatch");
        }

        long now = Instant.now().getEpochSecond();
        Long exp = toLongClaim(claims.get("exp"));
        Long nbf = toLongClaim(claims.get("nbf"));
        if (exp == null) {
            throw new RuntimeException("exp claim is required for external token");
        }
        if (now > exp + profile.getClockSkewSeconds()) {
            throw new RuntimeException("External token has expired");
        }
        if (nbf != null && now + profile.getClockSkewSeconds() < nbf) {
            throw new RuntimeException("External token is not valid yet");
        }

        if (expectedNonce != null && !expectedNonce.isBlank()) {
            String tokenNonce = toStringClaim(claims.get("nonce"));
            if (tokenNonce == null || !expectedNonce.equals(tokenNonce)) {
                throw new RuntimeException("External token nonce validation failed");
            }
        }

        MappedIdentity identity = resolveMappedIdentity(profile, claims);
        return TokenValidationResult.builder()
                .valid(true)
                .respondentId(identity.respondentId())
                .email(identity.email())
                .mappedClaims(identity.claims())
                .build();
    }

    private TokenValidationResult handleFallback(AuthProfile profile, String errorMsg) {
        return switch (profile.getFallbackPolicy()) {
            case ANONYMOUS_FALLBACK -> {
                log.info("Falling back to anonymous access for tenant {}", profile.getTenantId());
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

    private String hmacSha256Base64Url(String data, String secret) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("HMAC-SHA256 computation failed", e);
        }
    }

    private String base64UrlDecodeToString(String value) {
        try {
            return new String(Base64.getUrlDecoder().decode(value), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Invalid base64url token segment", e);
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> parseJsonMap(String json) {
        try {
            return objectMapper.readValue(json, Map.class);
        } catch (Exception e) {
            throw new RuntimeException("Invalid token JSON payload", e);
        }
    }

    private String toStringClaim(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Collection<?> collection) {
            String joined = collection.stream().map(String::valueOf).reduce((a, b) -> a + "," + b).orElse(null);
            return joined == null || joined.isBlank() ? null : joined;
        }
        String v = String.valueOf(value);
        return v.isBlank() ? null : v;
    }

    private Long toLongClaim(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number n) {
            return n.longValue();
        }
        try {
            return Long.parseLong(String.valueOf(value));
        } catch (Exception e) {
            throw new RuntimeException("Invalid numeric claim value: " + value, e);
        }
    }

    private boolean audienceMatches(Object audClaim, String expectedAudience) {
        if (audClaim == null || expectedAudience == null) {
            return false;
        }
        if (audClaim instanceof String s) {
            return expectedAudience.equals(s);
        }
        if (audClaim instanceof Collection<?> c) {
            return c.stream().map(String::valueOf).anyMatch(expectedAudience::equals);
        }
        return expectedAudience.equals(String.valueOf(audClaim));
    }

    private MappedIdentity resolveMappedIdentity(AuthProfile profile, Map<String, Object> claims) {
        List<ClaimMapping> mappings = profile.getClaimMappings();
        if (mappings == null || mappings.isEmpty()) {
            mappings = List.of(
                    ClaimMapping.builder().externalClaim("sub").internalField("respondentId").required(true).build(),
                    ClaimMapping.builder().externalClaim("email").internalField("email").required(false).build());
        }

        Map<String, String> mappedClaims = new HashMap<>();
        for (ClaimMapping mapping : mappings) {
            String value = extractMappedClaim(claims, mapping.getExternalClaim());
            if (value == null) {
                if (mapping.isRequired()) {
                    throw new RuntimeException(
                            "Required claim '%s' is missing".formatted(mapping.getExternalClaim()));
                }
                continue;
            }
            mappedClaims.put(mapping.getInternalField(), value);
        }

        String respondentId = mappedClaims.get("respondentId");
        if (respondentId == null || respondentId.isBlank()) {
            throw new RuntimeException("Required mapped internal field 'respondentId' is missing");
        }
        return new MappedIdentity(respondentId, mappedClaims.get("email"), mappedClaims);
    }

    @SuppressWarnings("unchecked")
    private String extractMappedClaim(Map<String, Object> claims, String claimPath) {
        if (claimPath == null || claimPath.isBlank()) {
            return null;
        }
        if (!claimPath.contains(".")) {
            return toStringClaim(claims.get(claimPath));
        }
        Object current = claims;
        for (String part : claimPath.split("\\.")) {
            if (!(current instanceof Map<?, ?> map)) {
                return null;
            }
            current = ((Map<String, Object>) map).get(part);
            if (current == null) {
                return null;
            }
        }
        return toStringClaim(current);
    }

    @SuppressWarnings("unchecked")
    private void verifyRsaSignature(AuthProfile profile, Map<String, Object> header, String signingInput, byte[] signature) {
        try {
            String jwksEndpoint = profile.getJwksEndpoint();
            if (jwksEndpoint == null || jwksEndpoint.isBlank()) {
                throw new BusinessException(ErrorCode.ACCESS_DENIED, "JWKS endpoint is required for RS256 validation");
            }

            String kid = toStringClaim(header.get("kid"));
            JwksKeySet jwks = getOrLoadJwks(jwksEndpoint);
            PublicKey publicKey = jwks.resolvePublicKey(kid);
            if (publicKey == null) {
                // Key rotation fallback: refresh JWKS once and retry.
                jwksCache.invalidate(jwksEndpoint);
                JwksKeySet refreshed = getOrLoadJwks(jwksEndpoint);
                publicKey = refreshed.resolvePublicKey(kid);
            }
            if (publicKey == null) {
                throw new RuntimeException("No matching JWK found for kid");
            }

            Signature verifier = Signature.getInstance("SHA256withRSA");
            verifier.initVerify(publicKey);
            verifier.update(signingInput.getBytes(StandardCharsets.UTF_8));
            if (!verifier.verify(signature)) {
                throw new RuntimeException("External token RSA signature verification failed");
            }
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception e) {
            throw new RuntimeException("Failed to verify RS256 token: " + e.getMessage(), e);
        }
    }

    private record MappedIdentity(String respondentId, String email, Map<String, String> claims) {
    }

    private JwksKeySet getOrLoadJwks(String jwksEndpoint) {
        JwksKeySet cached = jwksCache.getIfPresent(jwksEndpoint);
        if (cached != null) {
            return cached;
        }
        JwksKeySet loaded = fetchJwks(jwksEndpoint);
        jwksCache.put(jwksEndpoint, loaded);
        return loaded;
    }

    @SuppressWarnings("unchecked")
    private JwksKeySet fetchJwks(String jwksEndpoint) {
        try {
            HttpRequest req = HttpRequest.newBuilder(URI.create(jwksEndpoint)).GET().build();
            HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() < 200 || resp.statusCode() >= 300) {
                throw new RuntimeException("JWKS fetch failed with status " + resp.statusCode());
            }
            Map<String, Object> jwksJson = parseJsonMap(resp.body());
            Object keysObj = jwksJson.get("keys");
            if (!(keysObj instanceof List<?> keys) || keys.isEmpty()) {
                throw new RuntimeException("No keys found in JWKS");
            }
            Map<String, PublicKey> byKid = new HashMap<>();
            PublicKey firstKey = null;
            for (Object k : keys) {
                if (!(k instanceof Map<?, ?> candidate)) {
                    continue;
                }
                String kty = toStringClaim(candidate.get("kty"));
                if (kty != null && !"RSA".equals(kty)) {
                    continue;
                }
                String n = toStringClaim(candidate.get("n"));
                String e = toStringClaim(candidate.get("e"));
                if (n == null || e == null) {
                    continue;
                }
                BigInteger modulus = new BigInteger(1, Base64.getUrlDecoder().decode(n));
                BigInteger exponent = new BigInteger(1, Base64.getUrlDecoder().decode(e));
                RSAPublicKeySpec keySpec = new RSAPublicKeySpec(modulus, exponent);
                PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(keySpec);
                String keyId = toStringClaim(candidate.get("kid"));
                if (keyId != null) {
                    byKid.put(keyId, publicKey);
                }
                if (firstKey == null) {
                    firstKey = publicKey;
                }
            }
            if (byKid.isEmpty() && firstKey == null) {
                throw new RuntimeException("No usable RSA keys found in JWKS");
            }
            return new JwksKeySet(byKid, firstKey);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch JWKS: " + e.getMessage(), e);
        }
    }

    private record JwksKeySet(Map<String, PublicKey> keysByKid, PublicKey fallbackKey) {
        PublicKey resolvePublicKey(String kid) {
            if (kid != null && !kid.isBlank()) {
                return keysByKid.get(kid);
            }
            return fallbackKey;
        }
    }
}
