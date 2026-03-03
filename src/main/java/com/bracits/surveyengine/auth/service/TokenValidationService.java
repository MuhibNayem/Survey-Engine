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
}
