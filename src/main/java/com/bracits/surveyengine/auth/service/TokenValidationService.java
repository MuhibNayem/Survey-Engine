package com.bracits.surveyengine.auth.service;

import com.bracits.surveyengine.auth.dto.TokenValidationResult;

import java.util.UUID;

/**
 * Service contract for token validation across auth modes.
 * SRS §4.9.2, §4.9.4
 */
public interface TokenValidationService {

    /**
     * Validates a token against the campaign's auth profile.
     *
     * @param campaignId the campaign ID
     * @param token      the token to validate (signed launch token or JWT)
     * @return validation result with mapped claims or error
     */
    TokenValidationResult validateToken(UUID campaignId, String token);
}
