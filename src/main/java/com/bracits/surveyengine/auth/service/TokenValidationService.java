package com.bracits.surveyengine.auth.service;

import com.bracits.surveyengine.auth.dto.TokenValidationResult;

/**
 * Service contract for token validation across auth modes.
 * Validates respondent tokens against the tenant's auth profile.
 */
public interface TokenValidationService {

    /**
     * Validates a respondent token against the tenant's auth profile.
     *
     * @param tenantId the tenant ID whose auth config to use
     * @param token    the token to validate (signed launch token or JWT)
     * @return validation result with mapped claims or error
     */
    TokenValidationResult validateToken(String tenantId, String token);

    /**
     * Validates a respondent token with additional expected nonce context.
     * Used for OIDC authorization-code callback hardening to prevent replay.
     *
     * @param tenantId      tenant ID whose auth profile is applied
     * @param token         token to validate
     * @param expectedNonce expected nonce from OIDC state (nullable when not applicable)
     * @return validation result
     */
    default TokenValidationResult validateToken(String tenantId, String token, String expectedNonce) {
        return validateToken(tenantId, token);
    }

    /**
     * Evicts a cached JWKS document for the given endpoint.
     * Default no-op for implementations without endpoint caching.
     */
    default void evictJwksCache(String jwksEndpoint) {
        // no-op by default
    }
}
