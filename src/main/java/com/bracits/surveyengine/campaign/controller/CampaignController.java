package com.bracits.surveyengine.campaign.controller;

import com.bracits.surveyengine.campaign.dto.*;
import com.bracits.surveyengine.campaign.service.CampaignService;
import com.bracits.surveyengine.campaign.service.DistributionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/campaigns")
@RequiredArgsConstructor
public class CampaignController {

    private final CampaignService campaignService;
    private final DistributionService distributionService;

    @PostMapping
    public ResponseEntity<CampaignResponse> create(@Valid @RequestBody CampaignRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(campaignService.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CampaignResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(campaignService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<CampaignResponse>> getAll() {
        return ResponseEntity.ok(campaignService.getAllActive());
    }

    @PutMapping("/{id}")
    public ResponseEntity<CampaignResponse> update(@PathVariable UUID id,
            @Valid @RequestBody CampaignRequest request) {
        return ResponseEntity.ok(campaignService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivate(@PathVariable UUID id) {
        campaignService.deactivate(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/settings")
    public ResponseEntity<CampaignResponse> updateSettings(
            @PathVariable UUID id,
            @RequestBody CampaignSettingsRequest request) {
        return ResponseEntity.ok(campaignService.updateSettings(id, request));
    }

    @PostMapping("/{id}/activate")
    public ResponseEntity<CampaignResponse> activate(@PathVariable UUID id) {
        return ResponseEntity.ok(campaignService.activate(id));
    }

    @PostMapping("/{id}/distribute")
    public ResponseEntity<List<DistributionChannelResponse>> distribute(@PathVariable UUID id) {
        return ResponseEntity.status(HttpStatus.CREATED).body(distributionService.generateChannels(id));
    }

    @GetMapping("/{id}/channels")
    public ResponseEntity<List<DistributionChannelResponse>> getChannels(@PathVariable UUID id) {
        return ResponseEntity.ok(distributionService.getChannels(id));
    }
}
