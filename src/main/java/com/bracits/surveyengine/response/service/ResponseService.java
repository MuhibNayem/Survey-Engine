package com.bracits.surveyengine.response.service;

import com.bracits.surveyengine.response.dto.ResponseSubmissionRequest;
import com.bracits.surveyengine.response.dto.SurveyResponseResponse;

import java.util.List;
import java.util.UUID;

/**
 * Service contract for survey response submission and retrieval.
 * SRS §4.7
 */
public interface ResponseService {

    SurveyResponseResponse submit(ResponseSubmissionRequest request);

    SurveyResponseResponse getById(UUID id);

    List<SurveyResponseResponse> getByCampaignId(UUID campaignId);
}
