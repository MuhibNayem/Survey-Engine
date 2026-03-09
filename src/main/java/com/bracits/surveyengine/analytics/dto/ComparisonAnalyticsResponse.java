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
public class ComparisonAnalyticsResponse {
    private UUID campaignId;
    
    // Key is the Segment name (e.g., "HR", "Engineering") 
    // Value is the full pre-calculated survey statistics for that slice
    private Map<String, FullCampaignAnalyticsResponse> segmentReports;
}
