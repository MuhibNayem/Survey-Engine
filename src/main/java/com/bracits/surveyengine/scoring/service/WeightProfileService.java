package com.bracits.surveyengine.scoring.service;

import com.bracits.surveyengine.scoring.dto.WeightProfileRequest;
import com.bracits.surveyengine.scoring.dto.WeightProfileResponse;

import java.util.List;
import java.util.UUID;

/**
 * Service contract for weight profile CRUD with validation.
 * SRS §5.1, §5.3
 */
public interface WeightProfileService {

    WeightProfileResponse create(WeightProfileRequest request);

    WeightProfileResponse getById(UUID id);

    List<WeightProfileResponse> getByCampaignId(UUID campaignId);

    WeightProfileResponse update(UUID id, WeightProfileRequest request);

    void deactivate(UUID id);

    /**
     * Validates that category weights sum to exactly 100%.
     * SRS §5.3: Activation blocker.
     */
    void validateWeightSum(UUID profileId);

    /**
     * Creates or updates the campaign default profile from pinned survey snapshot
     * category weights and returns profile id.
     */
    UUID upsertDefaultProfileFromSurveySnapshot(UUID campaignId, UUID surveySnapshotId);
}
