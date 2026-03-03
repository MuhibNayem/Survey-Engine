package com.bracits.surveyengine.response.service;

import com.bracits.surveyengine.auth.dto.TokenValidationResult;
import com.bracits.surveyengine.auth.dto.ResponderAccessIdentity;
import com.bracits.surveyengine.auth.entity.AuthenticationMode;
import com.bracits.surveyengine.auth.repository.AuthProfileRepository;
import com.bracits.surveyengine.auth.service.OidcResponderAuthService;
import com.bracits.surveyengine.auth.service.TokenValidationService;
import com.bracits.surveyengine.campaign.entity.Campaign;
import com.bracits.surveyengine.campaign.entity.AuthMode;
import com.bracits.surveyengine.campaign.entity.CampaignSettings;
import com.bracits.surveyengine.campaign.entity.CampaignStatus;
import com.bracits.surveyengine.campaign.repository.CampaignRepository;
import com.bracits.surveyengine.campaign.repository.CampaignSettingsRepository;
import com.bracits.surveyengine.common.exception.BusinessException;
import com.bracits.surveyengine.common.exception.ErrorCode;
import com.bracits.surveyengine.common.exception.ResourceNotFoundException;
import com.bracits.surveyengine.common.tenant.TenantSupport;
import com.bracits.surveyengine.subscription.service.PlanQuotaService;
import com.bracits.surveyengine.response.dto.ResponseSubmissionRequest;
import com.bracits.surveyengine.response.dto.SurveyResponseResponse;
import com.bracits.surveyengine.response.entity.Answer;
import com.bracits.surveyengine.response.entity.ResponseStatus;
import com.bracits.surveyengine.response.entity.SurveyResponse;
import com.bracits.surveyengine.response.repository.SurveyResponseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Implementation of {@link ResponseService}.
 * <p>
 * Handles response submission with runtime settings enforcement (SRS §4.5,
 * §4.7):
 * campaign active check, quota check, one-per-device/IP deduplication.
 */
@Service
@RequiredArgsConstructor
public class ResponseServiceImpl implements ResponseService {

    private final SurveyResponseRepository responseRepository;
    private final CampaignRepository campaignRepository;
    private final CampaignSettingsRepository settingsRepository;
    private final TokenValidationService tokenValidationService;
    private final AuthProfileRepository authProfileRepository;
    private final PlanQuotaService planQuotaService;
    private final OidcResponderAuthService oidcResponderAuthService;

    @Override
    @Transactional
    public SurveyResponseResponse submit(ResponseSubmissionRequest request) {
        Campaign campaign = campaignRepository.findById(request.getCampaignId())
                .orElseThrow(() -> new ResourceNotFoundException("Campaign", request.getCampaignId()));

        // 1. Campaign must be ACTIVE
        if (campaign.getStatus() != CampaignStatus.ACTIVE) {
            throw new BusinessException(ErrorCode.CAMPAIGN_NOT_ACTIVE,
                    "Campaign %s is not active".formatted(campaign.getId()));
        }

        // 2. Enforce access mode (PUBLIC vs PRIVATE)
        enforceAccessMode(campaign, request);

        // 2. Enforce runtime settings
        CampaignSettings settings = settingsRepository.findByCampaignId(campaign.getId())
                .orElse(null);
        if (settings != null) {
            enforceSettings(settings, request, campaign.getId(), campaign.getTenantId());
        }
        enforcePlanResponseQuota(campaign.getId(), campaign.getTenantId());

        // 3. Build and save response
        SurveyResponse surveyResponse = SurveyResponse.builder()
                .campaignId(campaign.getId())
                .surveySnapshotId(campaign.getSurveySnapshotId())
                .tenantId(campaign.getTenantId())
                .respondentIdentifier(request.getRespondentIdentifier())
                .respondentIp(request.getRespondentIp())
                .respondentDeviceFingerprint(request.getRespondentDeviceFingerprint())
                .status(ResponseStatus.SUBMITTED)
                .submittedAt(Instant.now())
                .build();

        if (request.getAnswers() != null) {
            for (ResponseSubmissionRequest.AnswerRequest ar : request.getAnswers()) {
                Answer answer = Answer.builder()
                        .surveyResponse(surveyResponse)
                        .questionId(ar.getQuestionId())
                        .questionVersionId(ar.getQuestionVersionId())
                        .value(ar.getValue())
                        .score(ar.getScore())
                        .build();
                surveyResponse.getAnswers().add(answer);
            }
        }

        surveyResponse = responseRepository.save(surveyResponse);

        // 4. Auto-lock on submit (SRS §8)
        surveyResponse.setStatus(ResponseStatus.LOCKED);
        surveyResponse.setLockedAt(Instant.now());
        surveyResponse = responseRepository.save(surveyResponse);

        return toResponse(surveyResponse);
    }

    private void enforceAccessMode(Campaign campaign, ResponseSubmissionRequest request) {
        if (campaign.getAuthMode() == AuthMode.PUBLIC) {
            return;
        }

        if (request.getResponderAccessCode() != null && !request.getResponderAccessCode().isBlank()) {
            ResponderAccessIdentity identity = oidcResponderAuthService.consumeAccessCode(
                    request.getResponderAccessCode(), campaign.getTenantId(), campaign.getId());
            if ((request.getRespondentIdentifier() == null || request.getRespondentIdentifier().isBlank())
                    && identity.getEmail() != null && !identity.getEmail().isBlank()) {
                request.setRespondentIdentifier(identity.getEmail());
            }
            return;
        }

        if (request.getResponderToken() == null || request.getResponderToken().isBlank()) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED,
                    "Responder token or access code is required for private campaigns");
        }

        String tenantId = campaign.getTenantId();
        if (tenantId == null || tenantId.isBlank()) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED,
                    "Campaign tenant is not configured");
        }

        var profile = authProfileRepository.findByTenantId(tenantId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCESS_DENIED,
                        "No tenant auth profile configured for private campaign"));

        if (profile.getAuthMode() == AuthenticationMode.PUBLIC_ANONYMOUS) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED,
                    "Private campaign requires tenant auth mode other than PUBLIC_ANONYMOUS");
        }

        TokenValidationResult validation = tokenValidationService.validateToken(tenantId, request.getResponderToken());
        if (!validation.isValid()) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED,
                    "Responder authentication failed: " + validation.getErrorMessage());
        }

        // Private mode must not degrade to anonymous fallback.
        if (validation.getRespondentId() != null && validation.getRespondentId().startsWith("anon-")) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED,
                    "Anonymous access is not allowed for private campaigns");
        }

        // Populate identifier from auth context when client didn't supply one.
        if ((request.getRespondentIdentifier() == null || request.getRespondentIdentifier().isBlank())
                && validation.getEmail() != null && !validation.getEmail().isBlank()) {
            request.setRespondentIdentifier(validation.getEmail());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public SurveyResponseResponse getById(UUID id) {
        return toResponse(findOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SurveyResponseResponse> getByCampaignId(UUID campaignId) {
        String tenantId = TenantSupport.currentTenantOrDefault();
        campaignRepository.findByIdAndTenantId(campaignId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign", campaignId));
        return responseRepository.findByCampaignIdAndTenantId(campaignId, tenantId).stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Enforces runtime campaign settings (SRS §4.5):
     * - Response quota
     * - Close date/expiry
     * - One response per device (fingerprint or IP)
     * - Email restriction (respondent identifier dedup)
     */
    private void enforceSettings(CampaignSettings settings,
            ResponseSubmissionRequest request, UUID campaignId, String campaignTenantId) {
        // Quota check
        if (settings.getResponseQuota() != null) {
            long currentCount = responseRepository.countByCampaignIdAndStatusAndTenantId(
                    campaignId, ResponseStatus.LOCKED, campaignTenantId);
            if (currentCount >= settings.getResponseQuota()) {
                throw new BusinessException(ErrorCode.RESPONSE_QUOTA_EXCEEDED,
                        "Campaign quota of %d responses has been reached"
                                .formatted(settings.getResponseQuota()));
            }
        }

        // Close date check
        if (settings.getCloseDate() != null && Instant.now().isAfter(settings.getCloseDate())) {
            throw new BusinessException(ErrorCode.CAMPAIGN_NOT_ACTIVE,
                    "Campaign has passed its close date");
        }

        // One response per device
        if (settings.isOneResponsePerDevice() && request.getRespondentDeviceFingerprint() != null) {
            if (responseRepository.existsByCampaignIdAndRespondentDeviceFingerprintAndTenantId(
                    campaignId, request.getRespondentDeviceFingerprint(), campaignTenantId)) {
                throw new BusinessException(ErrorCode.DUPLICATE_RESPONSE,
                        "A response from this device already exists");
            }
        }

        // IP restriction
        if (settings.isIpRestrictionEnabled() && request.getRespondentIp() != null) {
            if (responseRepository.existsByCampaignIdAndRespondentIpAndTenantId(
                    campaignId, request.getRespondentIp(), campaignTenantId)) {
                throw new BusinessException(ErrorCode.DUPLICATE_RESPONSE,
                        "A response from this IP address already exists");
            }
        }

        // Email restriction
        if (settings.isEmailRestrictionEnabled() && request.getRespondentIdentifier() != null) {
            if (responseRepository.existsByCampaignIdAndRespondentIdentifierAndTenantId(
                    campaignId, request.getRespondentIdentifier(), campaignTenantId)) {
                throw new BusinessException(ErrorCode.DUPLICATE_RESPONSE,
                        "A response from this email already exists");
            }
        }
    }

    private SurveyResponse findOrThrow(UUID id) {
        return responseRepository.findByIdAndTenantId(id, TenantSupport.currentTenantOrDefault())
                .orElseThrow(() -> new ResourceNotFoundException("SurveyResponse", id));
    }

    private void enforcePlanResponseQuota(UUID campaignId, String campaignTenantId) {
        Integer maxResponses = planQuotaService.getMaxResponsesPerCampaign(campaignTenantId);
        if (maxResponses == null) {
            return;
        }
        long currentCount = responseRepository.countByCampaignIdAndStatusAndTenantId(
                campaignId, ResponseStatus.LOCKED, campaignTenantId);
        if (currentCount >= maxResponses) {
            throw new BusinessException(ErrorCode.QUOTA_EXCEEDED,
                    "Plan quota exceeded: max responses per campaign=" + maxResponses);
        }
    }

    private SurveyResponseResponse toResponse(SurveyResponse r) {
        List<SurveyResponseResponse.AnswerResponse> answers = r.getAnswers().stream()
                .map(a -> SurveyResponseResponse.AnswerResponse.builder()
                        .id(a.getId())
                        .questionId(a.getQuestionId())
                        .questionVersionId(a.getQuestionVersionId())
                        .value(a.getValue())
                        .score(a.getScore())
                        .build())
                .toList();

        return SurveyResponseResponse.builder()
                .id(r.getId())
                .campaignId(r.getCampaignId())
                .surveySnapshotId(r.getSurveySnapshotId())
                .respondentIdentifier(r.getRespondentIdentifier())
                .status(r.getStatus())
                .startedAt(r.getStartedAt())
                .submittedAt(r.getSubmittedAt())
                .lockedAt(r.getLockedAt())
                .answers(answers)
                .build();
    }
}
