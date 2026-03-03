package com.bracits.surveyengine.scoring.controller;

import com.bracits.surveyengine.scoring.dto.ScoreResult;
import com.bracits.surveyengine.scoring.dto.WeightProfileRequest;
import com.bracits.surveyengine.scoring.dto.WeightProfileResponse;
import com.bracits.surveyengine.scoring.service.ScoringEngineService;
import com.bracits.surveyengine.scoring.service.WeightProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/scoring")
@RequiredArgsConstructor
public class ScoringController {

    private final WeightProfileService weightProfileService;
    private final ScoringEngineService scoringEngineService;

    @PostMapping("/profiles")
    public ResponseEntity<WeightProfileResponse> createProfile(
            @Valid @RequestBody WeightProfileRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(weightProfileService.create(request));
    }

    @GetMapping("/profiles/{id}")
    public ResponseEntity<WeightProfileResponse> getProfile(@PathVariable UUID id) {
        return ResponseEntity.ok(weightProfileService.getById(id));
    }

    @GetMapping("/profiles/campaign/{campaignId}")
    public ResponseEntity<List<WeightProfileResponse>> getProfilesByCampaign(
            @PathVariable UUID campaignId) {
        return ResponseEntity.ok(weightProfileService.getByCampaignId(campaignId));
    }

    @PutMapping("/profiles/{id}")
    public ResponseEntity<WeightProfileResponse> updateProfile(
            @PathVariable UUID id,
            @Valid @RequestBody WeightProfileRequest request) {
        return ResponseEntity.ok(weightProfileService.update(id, request));
    }

    @DeleteMapping("/profiles/{id}")
    public ResponseEntity<Void> deactivateProfile(@PathVariable UUID id) {
        weightProfileService.deactivate(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/profiles/{id}/validate")
    public ResponseEntity<Void> validateWeightSum(@PathVariable UUID id) {
        weightProfileService.validateWeightSum(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Manual score calculation trigger (SRS §5).
     */
    @PostMapping("/calculate/{profileId}")
    public ResponseEntity<ScoreResult> calculateScore(
            @PathVariable UUID profileId,
            @RequestBody Map<UUID, BigDecimal> categoryRawScores) {
        return ResponseEntity.ok(scoringEngineService.calculateScore(profileId, categoryRawScores));
    }
}
