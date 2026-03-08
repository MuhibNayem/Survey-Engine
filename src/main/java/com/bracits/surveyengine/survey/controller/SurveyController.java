package com.bracits.surveyengine.survey.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.bracits.surveyengine.survey.dto.LifecycleTransitionRequest;
import com.bracits.surveyengine.survey.dto.SurveyRequest;
import com.bracits.surveyengine.survey.dto.SurveyResponse;
import com.bracits.surveyengine.survey.service.SurveyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/surveys")
@RequiredArgsConstructor
public class SurveyController {

    private final SurveyService surveyService;

    @PostMapping
    public ResponseEntity<SurveyResponse> create(@Valid @RequestBody SurveyRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(surveyService.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SurveyResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(surveyService.getById(id));
    }

    @GetMapping
    public ResponseEntity<Page<SurveyResponse>> getAll(Pageable pageable) {
        return ResponseEntity.ok(surveyService.getAllActive(pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SurveyResponse> update(@PathVariable UUID id,
            @Valid @RequestBody SurveyRequest request) {
        return ResponseEntity.ok(surveyService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivate(@PathVariable UUID id) {
        surveyService.deactivate(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Transitions the survey lifecycle state.
     * SRS §4.6
     */
    @PostMapping("/{id}/lifecycle")
    public ResponseEntity<SurveyResponse> transitionLifecycle(
            @PathVariable UUID id,
            @Valid @RequestBody LifecycleTransitionRequest request) {
        return ResponseEntity.ok(surveyService.transitionLifecycle(id, request));
    }
}
