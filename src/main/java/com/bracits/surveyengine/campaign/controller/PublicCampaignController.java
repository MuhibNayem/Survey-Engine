package com.bracits.surveyengine.campaign.controller;

import com.bracits.surveyengine.auth.dto.ResponderSessionStatusResponse;
import com.bracits.surveyengine.auth.dto.ResponderAuthExchangeRequest;
import com.bracits.surveyengine.auth.dto.ResponderAccessIdentity;
import com.bracits.surveyengine.auth.entity.AuthenticationMode;
import com.bracits.surveyengine.auth.repository.AuthProfileRepository;
import com.bracits.surveyengine.auth.service.OidcResponderAuthService;
import com.bracits.surveyengine.auth.service.ResponderExchangeSecurityService;
import com.bracits.surveyengine.auth.service.ResponderSessionService;
import com.bracits.surveyengine.auth.service.TokenValidationService;
import com.bracits.surveyengine.campaign.dto.CampaignPreviewResponse;
import com.bracits.surveyengine.campaign.entity.Campaign;
import com.bracits.surveyengine.campaign.service.CampaignService;
import com.bracits.surveyengine.campaign.repository.CampaignRepository;
import com.bracits.surveyengine.common.exception.BusinessException;
import com.bracits.surveyengine.common.exception.ErrorCode;
import com.bracits.surveyengine.response.dto.ResponseDraftLookupRequest;
import com.bracits.surveyengine.response.dto.ResponseSubmissionRequest;
import com.bracits.surveyengine.response.dto.SurveyResponseResponse;
import com.bracits.surveyengine.response.service.ResponseService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
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
    private final OidcResponderAuthService oidcResponderAuthService;
    private final TokenValidationService tokenValidationService;
    private final AuthProfileRepository authProfileRepository;
    private final ResponderExchangeSecurityService responderExchangeSecurityService;

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

    @PostMapping("/{id}/auth/logout")
    public ResponseEntity<Void> logoutResponderSession(
            @PathVariable UUID id,
            HttpServletRequest request) {
        Campaign campaign = campaignRepository.findById(id).orElse(null);
        if (campaign == null || campaign.getAuthMode() != com.bracits.surveyengine.campaign.entity.AuthMode.PRIVATE) {
            return ResponseEntity.noContent().build();
        }

        ResponseEntity.HeadersBuilder<?> builder = ResponseEntity.noContent();
        responderSessionService
                .revokeSessionCookies(request, campaign.getTenantId(), campaign.getId())
                .forEach(cookie -> builder.header(HttpHeaders.SET_COOKIE, cookie.toString()));
        return builder.build();
    }

    @PostMapping("/{id}/auth/exchange")
    public ResponseEntity<ResponderSessionStatusResponse> exchangeResponderSession(
            @PathVariable UUID id,
            @Valid @RequestBody ResponderAuthExchangeRequest request,
            HttpServletRequest httpRequest) {
        Campaign campaign = campaignRepository.findById(id).orElse(null);
        if (campaign == null || campaign.getAuthMode() != com.bracits.surveyengine.campaign.entity.AuthMode.PRIVATE) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED, "Campaign is not private or not available");
        }
        var securityContext = responderExchangeSecurityService.enforceAndResolveContext(httpRequest, campaign);
        String method = "UNKNOWN";

        ResponderAccessIdentity identity;
        try {
            String accessCode = request.getResponderAccessCode();
            String responderToken = request.getResponderToken();

            if (accessCode != null && !accessCode.isBlank()) {
                method = "OIDC_ACCESS_CODE";
                identity = oidcResponderAuthService.consumeAccessCode(
                        accessCode,
                        campaign.getTenantId(),
                        campaign.getId());
            } else if (responderToken != null && !responderToken.isBlank()) {
                method = "SIGNED_OR_SSO_TOKEN";
                String tenantId = campaign.getTenantId();
                if (tenantId == null || tenantId.isBlank()) {
                    throw new BusinessException(ErrorCode.ACCESS_DENIED, "Campaign tenant is not configured");
                }

                var profile = authProfileRepository.findByTenantId(tenantId)
                        .orElseThrow(() -> new BusinessException(
                                ErrorCode.ACCESS_DENIED,
                                "No tenant auth profile configured for private campaign"));
                if (profile.getAuthMode() == AuthenticationMode.PUBLIC_ANONYMOUS) {
                    throw new BusinessException(
                            ErrorCode.ACCESS_DENIED,
                            "Private campaign requires tenant auth mode other than PUBLIC_ANONYMOUS");
                }

                var validation = tokenValidationService.validateToken(tenantId, responderToken);
                if (!validation.isValid()) {
                    throw new BusinessException(
                            ErrorCode.ACCESS_DENIED,
                            "Responder authentication failed: " + validation.getErrorMessage());
                }
                if (validation.getRespondentId() != null && validation.getRespondentId().startsWith("anon-")) {
                    throw new BusinessException(
                            ErrorCode.ACCESS_DENIED,
                            "Anonymous access is not allowed for private campaigns");
                }
                identity = ResponderAccessIdentity.builder()
                        .respondentId(validation.getRespondentId())
                        .email(validation.getEmail())
                        .build();
            } else {
                throw new BusinessException(
                        ErrorCode.ACCESS_DENIED,
                        "Responder token or access code is required");
            }

            if (identity == null || identity.getRespondentId() == null || identity.getRespondentId().isBlank()) {
                throw new BusinessException(ErrorCode.ACCESS_DENIED, "Unable to resolve responder identity");
            }

            ResponseEntity.BodyBuilder builder = ResponseEntity.ok();
            responderSessionService
                    .createSessionCookies(httpRequest, campaign.getTenantId(), campaign.getId(), identity)
                    .forEach(cookie -> builder.header(HttpHeaders.SET_COOKIE, cookie.toString()));
            responderExchangeSecurityService.auditSuccess(
                    campaign,
                    securityContext,
                    identity.getRespondentId(),
                    identity.getEmail(),
                    method);
            return builder.body(ResponderSessionStatusResponse.builder()
                    .authenticated(true)
                    .email(identity.getEmail())
                    .build());
        } catch (BusinessException ex) {
            responderExchangeSecurityService.auditFailure(
                    campaign,
                    securityContext,
                    ex.getMessage(),
                    method,
                    ex.getErrorCode().name());
            throw ex;
        } catch (RuntimeException ex) {
            responderExchangeSecurityService.auditFailure(
                    campaign,
                    securityContext,
                    ex.getMessage(),
                    method,
                    ErrorCode.INTERNAL_ERROR.name());
            throw ex;
        }
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
