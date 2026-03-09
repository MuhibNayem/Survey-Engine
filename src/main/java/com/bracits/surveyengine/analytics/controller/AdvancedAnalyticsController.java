package com.bracits.surveyengine.analytics.controller;

import com.bracits.surveyengine.analytics.dto.ComparisonAnalyticsResponse;
import com.bracits.surveyengine.analytics.dto.ComparisonRequest;
import com.bracits.surveyengine.analytics.dto.FullCampaignAnalyticsResponse;
import com.bracits.surveyengine.analytics.dto.QuestionAnalyticsResponse;
import com.bracits.surveyengine.analytics.dto.ScoreDistributionResponse;
import com.bracits.surveyengine.analytics.dto.TemporalAnalyticsResponse;
import com.bracits.surveyengine.analytics.service.AdvancedAnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/analytics/campaigns")
@RequiredArgsConstructor
public class AdvancedAnalyticsController {

    private final AdvancedAnalyticsService advancedAnalyticsService;

    @GetMapping("/{campaignId}/summary")
    public ResponseEntity<Object> getCampaignSummary(
            @PathVariable UUID campaignId,
            @RequestParam Map<String, String> allParams) {
        
        Map<String, String> metadataFilters = extractMetadataFilters(allParams);
        return ResponseEntity.ok(advancedAnalyticsService.getCampaignSummary(campaignId, metadataFilters));
    }

    @GetMapping("/{campaignId}/questions/{questionId}")
    public ResponseEntity<QuestionAnalyticsResponse> getQuestionAnalytics(
            @PathVariable UUID campaignId,
            @PathVariable UUID questionId,
            @RequestParam Map<String, String> allParams) {

        Map<String, String> metadataFilters = extractMetadataFilters(allParams);
        return ResponseEntity.ok(advancedAnalyticsService.getQuestionAnalytics(campaignId, questionId, metadataFilters));
    }

    @GetMapping("/{campaignId}/scores")
    public ResponseEntity<ScoreDistributionResponse> getScoreDistribution(
            @PathVariable UUID campaignId,
            @RequestParam Map<String, String> allParams) {

        Map<String, String> metadataFilters = extractMetadataFilters(allParams);
        return ResponseEntity.ok(advancedAnalyticsService.getScoreDistribution(campaignId, metadataFilters));
    }

    @GetMapping("/{campaignId}/trends")
    public ResponseEntity<TemporalAnalyticsResponse> getTemporalTrends(
            @PathVariable UUID campaignId,
            @RequestParam Map<String, String> allParams) {

        Map<String, String> metadataFilters = extractMetadataFilters(allParams);
        return ResponseEntity.ok(advancedAnalyticsService.getTemporalTrends(campaignId, metadataFilters));
    }

    @GetMapping("/{campaignId}/full-report")
    public ResponseEntity<FullCampaignAnalyticsResponse> getFullCampaignAnalytics(
            @PathVariable UUID campaignId,
            @RequestParam Map<String, String> allParams) {

        Map<String, String> metadataFilters = extractMetadataFilters(allParams);
        return ResponseEntity.ok(advancedAnalyticsService.getFullCampaignAnalytics(campaignId, metadataFilters));
    }

    @PostMapping("/{campaignId}/compare")
    public ResponseEntity<ComparisonAnalyticsResponse> compareSegments(
            @PathVariable UUID campaignId,
            @RequestBody ComparisonRequest request) {

        return ResponseEntity.ok(advancedAnalyticsService.compareSegments(campaignId, request));
    }

    private Map<String, String> extractMetadataFilters(Map<String, String> allParams) {
        Map<String, String> metadataFilters = new HashMap<>();
        allParams.forEach((key, value) -> {
            if (key.startsWith("metadata.")) {
                metadataFilters.put(key.substring("metadata.".length()), value);
            }
        });
        return metadataFilters;
    }
}
