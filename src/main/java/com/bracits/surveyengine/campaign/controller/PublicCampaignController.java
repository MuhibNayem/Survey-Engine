package com.bracits.surveyengine.campaign.controller;

import com.bracits.surveyengine.auth.dto.ResponderSessionStatusResponse;
import com.bracits.surveyengine.auth.service.ResponderSessionService;
import com.bracits.surveyengine.campaign.dto.CampaignPreviewResponse;
import com.bracits.surveyengine.campaign.entity.Campaign;
import com.bracits.surveyengine.campaign.service.CampaignService;
import com.bracits.surveyengine.campaign.repository.CampaignRepository;
import com.bracits.surveyengine.response.dto.ResponseDraftLookupRequest;
import com.bracits.surveyengine.response.dto.ResponseSubmissionRequest;
import com.bracits.surveyengine.response.dto.SurveyResponseResponse;
import com.bracits.surveyengine.response.service.ResponseService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/public/campaigns")
@RequiredArgsConstructor
public class PublicCampaignController {

    private final CampaignService campaignService;
    private final ResponseService responseService;
    private final CampaignRepository campaignRepository;
    private final ResponderSessionService responderSessionService;

    @GetMapping("/{id}/preview")
    public ResponseEntity<CampaignPreviewResponse> getPublicPreview(@PathVariable UUID id) {
        return ResponseEntity.ok(campaignService.getPublicPreview(id));
    }

    @GetMapping("/{id}/auth/session")
    public ResponseEntity<ResponderSessionStatusResponse> getResponderSession(
            @PathVariable UUID id,
            HttpServletRequest request) {
        Campaign campaign = campaignRepository.findById(id).orElse(null);
        if (campaign == null || campaign.getAuthMode() != com.bracits.surveyengine.campaign.entity.AuthMode.PRIVATE) {
            return ResponseEntity.ok(ResponderSessionStatusResponse.builder().authenticated(false).build());
        }
        var identity = responderSessionService.resolveIdentity(request, campaign.getTenantId(), campaign.getId());
        return ResponseEntity.ok(ResponderSessionStatusResponse.builder()
                .authenticated(identity.isPresent())
                .email(identity.map(com.bracits.surveyengine.auth.dto.ResponderAccessIdentity::getEmail).orElse(null))
                .build());
    }

    @PostMapping("/{id}/responses/draft")
    public ResponseEntity<SurveyResponseResponse> saveDraft(
            @PathVariable UUID id,
            @Valid @RequestBody ResponseSubmissionRequest request) {
        if (!id.equals(request.getCampaignId())) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(responseService.saveDraft(request));
    }

    @PostMapping("/{id}/responses/draft/load")
    public ResponseEntity<SurveyResponseResponse> loadDraft(
            @PathVariable UUID id,
            @Valid @RequestBody ResponseDraftLookupRequest request) {
        SurveyResponseResponse response = responseService.getPublicDraft(id, request);
        return response != null ? ResponseEntity.ok(response) : ResponseEntity.noContent().build();
    }
}
