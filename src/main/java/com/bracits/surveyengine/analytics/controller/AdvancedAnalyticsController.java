package com.bracits.surveyengine.analytics.controller;

import com.bracits.surveyengine.analytics.dto.QuestionAnalyticsResponse;
import com.bracits.surveyengine.analytics.dto.ScoreDistributionResponse;
import com.bracits.surveyengine.analytics.service.AdvancedAnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
