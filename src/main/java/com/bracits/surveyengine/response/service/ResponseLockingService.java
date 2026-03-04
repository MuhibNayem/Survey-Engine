package com.bracits.surveyengine.response.service;

import com.bracits.surveyengine.response.dto.ReopenRequest;
import com.bracits.surveyengine.response.dto.SurveyResponseResponse;

import java.time.Instant;
import java.util.UUID;

/**
 * Service contract for response locking and admin-only reopen.
 * SRS §8
 */
public interface ResponseLockingService {

    SurveyResponseResponse lock(UUID responseId);

    SurveyResponseResponse reopen(UUID responseId, ReopenRequest request);

    /**
     * Locks all open responses (IN_PROGRESS/REOPENED) for a campaign.
     * Used by close-time automation.
     *
     * @return number of responses locked
     */
    int lockOpenResponsesForCampaignClosure(UUID campaignId, Instant lockedAt);
}
