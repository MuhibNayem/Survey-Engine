package com.bracits.surveyengine.analytics.service;

import com.bracits.surveyengine.analytics.dto.ComparisonAnalyticsResponse;
import com.bracits.surveyengine.analytics.dto.ComparisonRequest;
import com.bracits.surveyengine.analytics.dto.FullCampaignAnalyticsResponse;
import com.bracits.surveyengine.analytics.dto.QuestionAnalyticsResponse;
import com.bracits.surveyengine.analytics.dto.ScoreDistributionResponse;
import com.bracits.surveyengine.analytics.dto.TemporalAnalyticsResponse;

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

    /**
     * Retrieves the temporal trend of response submissions for a campaign.
     * @param campaignId the ID of the campaign
     * @param metadataFilters optional respondent metadata filters
     * @return TemporalAnalyticsResponse containing counts grouped by submission date
     */
    TemporalAnalyticsResponse getTemporalTrends(UUID campaignId, Map<String, String> metadataFilters);

    /**
     * Retrieves a consolidated view of all analytics (Summary, Scores, Trends, Questions) in a single payload.
     * @param campaignId the ID of the campaign
     * @param metadataFilters optional respondent metadata filters
     * @return FullCampaignAnalyticsResponse containing all metrics
     */
    FullCampaignAnalyticsResponse getFullCampaignAnalytics(UUID campaignId, Map<String, String> metadataFilters);

    /**
     * Executes advanced analytics grouped by named segments for A/B comparison.
     * @param campaignId the ID of the campaign
     * @param request the ComparisonRequest containing multiple named metadata filter segments
     * @return ComparisonAnalyticsResponse mapping each segment name to its FullCampaignAnalyticsResponse
     */
    ComparisonAnalyticsResponse compareSegments(UUID campaignId, ComparisonRequest request);
}
