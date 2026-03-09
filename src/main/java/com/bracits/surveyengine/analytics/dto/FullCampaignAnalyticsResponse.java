package com.bracits.surveyengine.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FullCampaignAnalyticsResponse {
    private UUID campaignId;
    
    // Overview metrics
    private Map<String, Object> summary;
    
    // Detailed distributions
    private ScoreDistributionResponse scoreDistribution;
    private TemporalAnalyticsResponse temporalTrends;
    
    // All quantifiable questions mapped by ID
    private Map<UUID, QuestionAnalyticsResponse> questionAnalytics;
}
