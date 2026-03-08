package com.bracits.surveyengine.response.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    Page<SurveyResponseResponse> getByCampaignId(UUID campaignId, Pageable pageable);
}
