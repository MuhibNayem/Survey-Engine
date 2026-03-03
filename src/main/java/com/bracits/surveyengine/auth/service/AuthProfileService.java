package com.bracits.surveyengine.auth.service;

import com.bracits.surveyengine.auth.dto.AuthProfileRequest;
import com.bracits.surveyengine.auth.dto.AuthProfileResponse;

import java.util.UUID;

/**
 * Service contract for auth profile management.
 * Auth profiles are configured per tenant (not per campaign).
 */
public interface AuthProfileService {

    AuthProfileResponse create(AuthProfileRequest request);

    AuthProfileResponse update(UUID id, AuthProfileRequest request);

    AuthProfileResponse getByTenantId(String tenantId);

    AuthProfileResponse rotateKey(UUID id);
}
