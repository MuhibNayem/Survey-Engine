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

import java.util.List;
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
            @PathVariable UUID campaignId, Pageable pageable) {
        return ResponseEntity.ok(responseService.getByCampaignId(campaignId, pageable));
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
    public ResponseEntity<CampaignAnalytics> getAnalytics(@PathVariable UUID campaignId) {
        return ResponseEntity.ok(analyticsService.getAnalytics(campaignId));
    }
}
