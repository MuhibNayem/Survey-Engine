package com.bracits.surveyengine.analytics.service;

import com.bracits.surveyengine.analytics.dto.QuestionAnalyticsResponse;
import com.bracits.surveyengine.analytics.dto.ScoreDistributionResponse;

import java.util.Map;
import java.util.UUID;

public interface AdvancedAnalyticsService {

    /**
     * Retrieves detailed analytics for a specific question within a campaign.
     * @param campaignId the ID of the campaign
     * @param questionId the ID of the question
     * @param metadataFilters optional respondent metadata filters
     * @return QuestionAnalyticsResponse containing frequency counts and stats
     */
    QuestionAnalyticsResponse getQuestionAnalytics(UUID campaignId, UUID questionId, Map<String, String> metadataFilters);

    /**
     * Retrieves the overall score distribution for a campaign.
     * @param campaignId the ID of the campaign
     * @param metadataFilters optional respondent metadata filters
     * @return ScoreDistributionResponse containing score buckets and averages
     */
    ScoreDistributionResponse getScoreDistribution(UUID campaignId, Map<String, String> metadataFilters);

    /**
     * Retrieves an advanced summary for a campaign, including total scores and drop-offs.
     * @param campaignId the ID of the campaign
     * @param metadataFilters optional respondent metadata filters
     * @return Object containing summary statistics (will use existing CampaignAnalytics + extra fields)
     */
    Object getCampaignSummary(UUID campaignId, Map<String, String> metadataFilters);
}
