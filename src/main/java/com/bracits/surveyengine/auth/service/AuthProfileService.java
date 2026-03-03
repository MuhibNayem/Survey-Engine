package com.bracits.surveyengine.auth.service;

import com.bracits.surveyengine.auth.dto.AuthProfileRequest;
import com.bracits.surveyengine.auth.dto.AuthProfileResponse;

import java.util.UUID;

/**
 * Service contract for auth profile management.
 * SRS §4.9.3
 */
public interface AuthProfileService {

    AuthProfileResponse create(AuthProfileRequest request);

    AuthProfileResponse update(UUID id, AuthProfileRequest request);

    AuthProfileResponse getByCampaignId(UUID campaignId);

    AuthProfileResponse rotateKey(UUID id);
}
