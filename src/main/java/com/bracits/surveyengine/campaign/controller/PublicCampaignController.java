package com.bracits.surveyengine.campaign.controller;

import com.bracits.surveyengine.campaign.dto.CampaignPreviewResponse;
import com.bracits.surveyengine.campaign.service.CampaignService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/public/campaigns")
@RequiredArgsConstructor
public class PublicCampaignController {

    private final CampaignService campaignService;

    @GetMapping("/{id}/preview")
    public ResponseEntity<CampaignPreviewResponse> getPublicPreview(@PathVariable UUID id) {
        return ResponseEntity.ok(campaignService.getPublicPreview(id));
    }
}
