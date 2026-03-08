package com.bracits.surveyengine.response.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.bracits.surveyengine.response.dto.*;
import com.bracits.surveyengine.response.service.AnalyticsService;
import com.bracits.surveyengine.response.service.ResponseLockingService;
import com.bracits.surveyengine.response.service.ResponseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/responses")
@RequiredArgsConstructor
public class ResponseController {

    private final ResponseService responseService;
    private final ResponseLockingService lockingService;
    private final AnalyticsService analyticsService;

    @PostMapping
    public ResponseEntity<SurveyResponseResponse> submit(
            @Valid @RequestBody ResponseSubmissionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(responseService.submit(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SurveyResponseResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(responseService.getById(id));
    }

    @GetMapping("/campaign/{campaignId}")
    public ResponseEntity<Page<SurveyResponseResponse>> getByCampaign(
            @PathVariable UUID campaignId, 
            @RequestParam Map<String, String> allParams,
            Pageable pageable) {
        
        Map<String, String> metadataFilters = new HashMap<>();
        allParams.forEach((key, value) -> {
            if (key.startsWith("metadata.")) {
                metadataFilters.put(key.substring("metadata.".length()), value);
            }
        });
        
        return ResponseEntity.ok(responseService.getByCampaignId(campaignId, metadataFilters, pageable));
    }

    @PostMapping("/{id}/lock")
    public ResponseEntity<SurveyResponseResponse> lock(@PathVariable UUID id) {
        return ResponseEntity.ok(lockingService.lock(id));
    }

    @PostMapping("/{id}/reopen")
    public ResponseEntity<SurveyResponseResponse> reopen(
            @PathVariable UUID id,
            @Valid @RequestBody ReopenRequest request) {
        return ResponseEntity.ok(lockingService.reopen(id, request));
    }

    @GetMapping("/analytics/{campaignId}")
    public ResponseEntity<CampaignAnalytics> getAnalytics(
            @PathVariable UUID campaignId,
            @RequestParam Map<String, String> allParams) {
        
        Map<String, String> metadataFilters = new HashMap<>();
        allParams.forEach((key, value) -> {
            if (key.startsWith("metadata.")) {
                metadataFilters.put(key.substring("metadata.".length()), value);
            }
        });
        
        return ResponseEntity.ok(analyticsService.getAnalytics(campaignId, metadataFilters));
    }
}
