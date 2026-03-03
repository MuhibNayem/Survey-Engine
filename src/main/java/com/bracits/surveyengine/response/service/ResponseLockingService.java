package com.bracits.surveyengine.response.service;

import com.bracits.surveyengine.response.dto.ReopenRequest;
import com.bracits.surveyengine.response.dto.SurveyResponseResponse;

import java.util.UUID;

/**
 * Service contract for response locking and admin-only reopen.
 * SRS §8
 */
public interface ResponseLockingService {

    SurveyResponseResponse lock(UUID responseId);

    SurveyResponseResponse reopen(UUID responseId, ReopenRequest request);
}
