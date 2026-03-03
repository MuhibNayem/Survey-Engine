package com.bracits.surveyengine.response.service;

import com.bracits.surveyengine.campaign.entity.Campaign;
import com.bracits.surveyengine.campaign.entity.CampaignSettings;
import com.bracits.surveyengine.campaign.entity.CampaignStatus;
import com.bracits.surveyengine.campaign.repository.CampaignRepository;
import com.bracits.surveyengine.campaign.repository.CampaignSettingsRepository;
import com.bracits.surveyengine.common.exception.BusinessException;
import com.bracits.surveyengine.common.exception.ErrorCode;
import com.bracits.surveyengine.common.exception.ResourceNotFoundException;
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

        // 2. Enforce runtime settings
        CampaignSettings settings = settingsRepository.findByCampaignId(campaign.getId())
                .orElse(null);
        if (settings != null) {
            enforceSettings(settings, request, campaign.getId());
        }

        // 3. Build and save response
        SurveyResponse surveyResponse = SurveyResponse.builder()
                .campaignId(campaign.getId())
                .surveySnapshotId(campaign.getSurveySnapshotId())
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

    @Override
    @Transactional(readOnly = true)
    public SurveyResponseResponse getById(UUID id) {
        return toResponse(findOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SurveyResponseResponse> getByCampaignId(UUID campaignId) {
        return responseRepository.findByCampaignId(campaignId).stream()
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
            ResponseSubmissionRequest request, UUID campaignId) {
        // Quota check
        if (settings.getResponseQuota() != null) {
            long currentCount = responseRepository.countByCampaignIdAndStatus(
                    campaignId, ResponseStatus.LOCKED);
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
            if (responseRepository.existsByCampaignIdAndRespondentDeviceFingerprint(
                    campaignId, request.getRespondentDeviceFingerprint())) {
                throw new BusinessException(ErrorCode.DUPLICATE_RESPONSE,
                        "A response from this device already exists");
            }
        }

        // IP restriction
        if (settings.isIpRestrictionEnabled() && request.getRespondentIp() != null) {
            if (responseRepository.existsByCampaignIdAndRespondentIp(
                    campaignId, request.getRespondentIp())) {
                throw new BusinessException(ErrorCode.DUPLICATE_RESPONSE,
                        "A response from this IP address already exists");
            }
        }

        // Email restriction
        if (settings.isEmailRestrictionEnabled() && request.getRespondentIdentifier() != null) {
            if (responseRepository.existsByCampaignIdAndRespondentIdentifier(
                    campaignId, request.getRespondentIdentifier())) {
                throw new BusinessException(ErrorCode.DUPLICATE_RESPONSE,
                        "A response from this email already exists");
            }
        }
    }

    private SurveyResponse findOrThrow(UUID id) {
        return responseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SurveyResponse", id));
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
