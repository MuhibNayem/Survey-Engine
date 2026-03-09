package com.bracits.surveyengine.admin.service;

import com.bracits.surveyengine.admin.entity.AdminUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

/**
 * Engine-owned JWT service — generates and validates tokens
 * for admin users. Uses HMAC-SHA256 with a configurable secret.
 */
@Service
public class JwtService {

    private final SecretKey signingKey;
    private final long tokenTtlSeconds;

    public JwtService(
            @Value("${survey-engine.jwt.secret:survey-engine-default-secret-key-change-in-production-minimum-32-chars}") String secret,
            @Value("${survey-engine.jwt.ttl-seconds:3600}") long tokenTtlSeconds) {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.tokenTtlSeconds = tokenTtlSeconds;
    }

    /**
     * Generate a JWT for an authenticated admin user.
     */
    public String generateToken(AdminUser user) {
        return generateTokenInternal(user, null);
    }

    /**
     * Generate a JWT for an impersonation session.
     * Includes an {@code impersonated_by} claim identifying the Super Admin who initiated the session.
     */
    public String generateImpersonationToken(AdminUser user, String impersonatorEmail) {
        return generateTokenInternal(user, impersonatorEmail);
    }

    private String generateTokenInternal(AdminUser user, String impersonatorEmail) {
        Instant now = Instant.now();
        var claims = new java.util.HashMap<>(Map.of(
                "tenant_id", user.getTenantId(),
                "email", user.getEmail(),
                "role", user.getRole().name(),
                "name", user.getFullName()));
        if (impersonatorEmail != null) {
            claims.put("impersonated_by", impersonatorEmail);
        }
        return Jwts.builder()
                .subject(user.getId().toString())
                .claims(claims)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(tokenTtlSeconds)))
                .signWith(signingKey)
                .compact();
    }

    /**
     * Parse and validate a JWT, returning its claims.
     */
    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public long getTokenTtlSeconds() {
        return tokenTtlSeconds;
    }
}
