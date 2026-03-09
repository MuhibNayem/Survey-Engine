package com.bracits.surveyengine.campaign.service;

import com.bracits.surveyengine.campaign.dto.*;
import com.bracits.surveyengine.campaign.entity.*;
import com.bracits.surveyengine.campaign.repository.CampaignRepository;
import com.bracits.surveyengine.campaign.repository.CampaignSettingsRepository;
import com.bracits.surveyengine.common.exception.BusinessException;
import com.bracits.surveyengine.common.exception.ErrorCode;
import com.bracits.surveyengine.common.exception.ResourceNotFoundException;
import com.bracits.surveyengine.common.tenant.TenantSupport;
import com.bracits.surveyengine.questionbank.entity.Question;
import com.bracits.surveyengine.questionbank.entity.QuestionVersion;
import com.bracits.surveyengine.questionbank.repository.QuestionRepository;
import com.bracits.surveyengine.questionbank.repository.QuestionVersionRepository;
import com.bracits.surveyengine.response.service.ResponseLockingService;
import com.bracits.surveyengine.scoring.service.WeightProfileService;
import com.bracits.surveyengine.subscription.service.PlanQuotaService;
import com.bracits.surveyengine.tenant.service.TenantService;
import com.bracits.surveyengine.survey.entity.Survey;
import com.bracits.surveyengine.survey.entity.SurveyLifecycleState;
import com.bracits.surveyengine.survey.entity.SurveyPage;
import com.bracits.surveyengine.survey.entity.SurveyQuestion;
import com.bracits.surveyengine.survey.entity.SurveySnapshot;
import com.bracits.surveyengine.survey.repository.SurveyRepository;
import com.bracits.surveyengine.survey.service.SurveyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Implementation of {@link CampaignService}.
 */
@Service
@RequiredArgsConstructor
public class CampaignServiceImpl implements CampaignService {

    private final CampaignRepository campaignRepository;
    private final CampaignSettingsRepository settingsRepository;
    private final SurveyRepository surveyRepository;
    private final QuestionRepository questionRepository;
    private final QuestionVersionRepository questionVersionRepository;
    private final SurveyService surveyService;
    private final WeightProfileService weightProfileService;
    private final PlanQuotaService planQuotaService;
    private final TenantService tenantService;
    private final ResponseLockingService responseLockingService;

    @Override
    @Transactional
    @com.bracits.surveyengine.common.audit.annotation.Auditable(action = "CAMPAIGN_CREATED")
    public CampaignResponse create(CampaignRequest request) {
        String tenantId = resolveTenantId();
        tenantService.ensureProvisioned(tenantId);
        planQuotaService.enforceCampaignQuota(tenantId);
        // Verify survey exists
        surveyRepository.findByIdAndTenantId(request.getSurveyId(), tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Survey", request.getSurveyId()));

        Campaign campaign = Campaign.builder()
                .name(request.getName())
                .description(request.getDescription())
                .surveyId(request.getSurveyId())
                .tenantId(tenantId)
                .authMode(normalizeAccessMode(request.getAuthMode()))
                .build();
        campaign = campaignRepository.save(campaign);

        // Create default settings
        CampaignSettings settings = CampaignSettings.builder()
                .campaignId(campaign.getId())
                .build();
        settingsRepository.save(settings);

        return toResponse(campaign);
    }

    @Override
    @Transactional(readOnly = true)
    public CampaignResponse getById(UUID id) {
        return toResponse(findOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CampaignResponse> getAllActive(Pageable pageable) {
        return campaignRepository.findByActiveTrueAndTenantId(resolveTenantId(), pageable)
                .map(this::toResponse);
    }

    @Override
    @Transactional
    @com.bracits.surveyengine.common.audit.annotation.Auditable(action = "CAMPAIGN_UPDATED")
    public CampaignResponse update(UUID id, CampaignRequest request) {
        Campaign campaign = findOrThrow(id);

        campaign.setName(request.getName());
        campaign.setDescription(request.getDescription());
        if (request.getAuthMode() != null) {
            campaign.setAuthMode(normalizeAccessMode(request.getAuthMode()));
        }
        campaign = campaignRepository.save(campaign);

        return toResponse(campaign);
    }

    @Override
    @Transactional
    @com.bracits.surveyengine.common.audit.annotation.Auditable(action = "CAMPAIGN_CLOSED")
    public void deactivate(UUID id) {
        Campaign campaign = findOrThrow(id);
        campaign.setActive(false);
        campaignRepository.save(campaign);
    }

    @Override
    @Transactional(readOnly = true)
    public CampaignPreviewResponse getPreview(UUID campaignId) {
        Campaign campaign = findOrThrow(campaignId);
        String tenantId = resolveTenantId();

        CampaignSettings settings = settingsRepository.findByCampaignId(campaignId)
                .orElseThrow(() -> new ResourceNotFoundException("CampaignSettings", campaignId));
        Survey survey = surveyRepository.findByIdAndTenantId(campaign.getSurveyId(), tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Survey", campaign.getSurveyId()));

        return buildPreview(campaign, settings, survey);
    }

    @Override
    @Transactional(readOnly = true)
    public CampaignPreviewResponse getPublicPreview(UUID campaignId) {
        Campaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign", campaignId));

        if (!campaign.isActive() || campaign.getStatus() != CampaignStatus.ACTIVE) {
            throw new ResourceNotFoundException("Campaign", campaignId);
        }

        CampaignSettings settings = settingsRepository.findByCampaignId(campaignId)
                .orElseThrow(() -> new ResourceNotFoundException("CampaignSettings", campaignId));
        Survey survey = surveyRepository.findById(campaign.getSurveyId())
                .orElseThrow(() -> new ResourceNotFoundException("Survey", campaign.getSurveyId()));

        return buildPreview(campaign, settings, survey);
    }

    @Override
    @Transactional(readOnly = true)
    public CampaignSettingsResponse getSettings(UUID campaignId) {
        findOrThrow(campaignId);
        CampaignSettings settings = settingsRepository.findByCampaignId(campaignId)
                .orElseThrow(() -> new ResourceNotFoundException("CampaignSettings", campaignId));
        return toSettingsResponse(settings);
    }

    @Override
    @Transactional
    @com.bracits.surveyengine.common.audit.annotation.Auditable(action = "CAMPAIGN_SETTINGS_UPDATED")
    public CampaignResponse updateSettings(UUID campaignId, CampaignSettingsRequest request) {
        Campaign campaign = findOrThrow(campaignId);
        CampaignSettings settings = settingsRepository.findByCampaignId(campaignId)
                .orElseThrow(() -> new ResourceNotFoundException("CampaignSettings", campaignId));

        settings.setPassword(request.getPassword());
        settings.setCaptchaEnabled(request.isCaptchaEnabled());
        settings.setOneResponsePerDevice(request.isOneResponsePerDevice());
        settings.setIpRestrictionEnabled(request.isIpRestrictionEnabled());
        settings.setEmailRestrictionEnabled(request.isEmailRestrictionEnabled());
        settings.setResponseQuota(request.getResponseQuota());
        settings.setCloseDate(request.getCloseDate());
        settings.setSessionTimeoutMinutes(request.getSessionTimeoutMinutes());
        settings.setShowQuestionNumbers(request.isShowQuestionNumbers());
        settings.setShowProgressIndicator(request.isShowProgressIndicator());
        settings.setAllowBackButton(request.isAllowBackButton());
        settings.setStartMessage(request.getStartMessage());
        settings.setFinishMessage(request.getFinishMessage());
        settings.setHeaderHtml(request.getHeaderHtml());
        settings.setFooterHtml(request.getFooterHtml());
        settings.setCollectName(request.isCollectName());
        settings.setCollectEmail(request.isCollectEmail());
        settings.setCollectPhone(request.isCollectPhone());
        settings.setCollectAddress(request.isCollectAddress());

        // Sync data collection fields (clear + re-add)
        if (request.getDataCollectionFields() != null) {
            settings.getDataCollectionFields().clear();
            for (DataCollectionFieldRequest fieldReq : request.getDataCollectionFields()) {
                settings.getDataCollectionFields().add(DataCollectionField.builder()
                        .campaignSettings(settings)
                        .fieldKey(fieldReq.getFieldKey())
                        .label(fieldReq.getLabel())
                        .fieldType(fieldReq.getFieldType() != null ? fieldReq.getFieldType() : DataCollectionFieldType.TEXT)
                        .required(fieldReq.isRequired())
                        .sortOrder(fieldReq.getSortOrder())
                        .enabled(fieldReq.isEnabled())
                        .build());
            }
        }

        settingsRepository.save(settings);

        Instant now = Instant.now();
        if (settings.getCloseDate() != null && !settings.getCloseDate().isAfter(now)) {
            responseLockingService.lockOpenResponsesForCampaignClosure(campaignId, now);
        }
        return toResponse(campaign);
    }

    /**
     * Activates the campaign, linking it to the latest survey snapshot.
     * The referenced survey must be in PUBLISHED state.
     */
    @Override
    @Transactional
    @com.bracits.surveyengine.common.audit.annotation.Auditable(action = "CAMPAIGN_ACTIVATED")
    public CampaignResponse activate(UUID id) {
        Campaign campaign = findOrThrow(id);
        UUID surveyId = campaign.getSurveyId();

        var survey = surveyRepository.findByIdAndTenantId(surveyId, resolveTenantId())
                .orElseThrow(() -> new ResourceNotFoundException("Survey", surveyId));
        if (survey.getLifecycleState() != SurveyLifecycleState.PUBLISHED) {
            throw new BusinessException(ErrorCode.INVALID_LIFECYCLE_TRANSITION,
                    "Survey must be in PUBLISHED state to activate a campaign");
        }

        SurveySnapshot snapshot = surveyService.getLatestSnapshot(surveyId);
        campaign.setSurveySnapshotId(snapshot.getId());
        campaign.setDefaultWeightProfileId(
                weightProfileService.upsertDefaultProfileFromSurveySnapshot(campaign.getId(), snapshot.getId()));
        campaign.setStatus(CampaignStatus.ACTIVE);
        campaign = campaignRepository.save(campaign);

        return toResponse(campaign);
    }

    private Campaign findOrThrow(UUID id) {
        return campaignRepository.findByIdAndTenantId(id, resolveTenantId())
                .orElseThrow(() -> new ResourceNotFoundException("Campaign", id));
    }

    private CampaignResponse toResponse(Campaign c) {
        CampaignSettings settings = settingsRepository.findByCampaignId(c.getId()).orElse(null);
        List<DataCollectionFieldResponse> fieldResponses = settings == null
                ? List.of()
                : toFieldResponses(settings.getDataCollectionFields());
        return CampaignResponse.builder()
                .id(c.getId())
                .name(c.getName())
                .description(c.getDescription())
                .surveyId(c.getSurveyId())
                .surveySnapshotId(c.getSurveySnapshotId())
                .defaultWeightProfileId(c.getDefaultWeightProfileId())
                .authMode(c.getAuthMode())
                .status(c.getStatus())
                .active(c.isActive())
                .createdBy(c.getCreatedBy())
                .createdAt(c.getCreatedAt())
                .updatedBy(c.getUpdatedBy())
                .updatedAt(c.getUpdatedAt())
                .dataCollectionFields(fieldResponses)
                .build();
    }

    private CampaignSettingsResponse toSettingsResponse(CampaignSettings settings) {
        return CampaignSettingsResponse.builder()
                .campaignId(settings.getCampaignId())
                .password(settings.getPassword())
                .captchaEnabled(settings.isCaptchaEnabled())
                .oneResponsePerDevice(settings.isOneResponsePerDevice())
                .ipRestrictionEnabled(settings.isIpRestrictionEnabled())
                .emailRestrictionEnabled(settings.isEmailRestrictionEnabled())
                .responseQuota(settings.getResponseQuota())
                .closeDate(settings.getCloseDate())
                .sessionTimeoutMinutes(settings.getSessionTimeoutMinutes())
                .showQuestionNumbers(settings.isShowQuestionNumbers())
                .showProgressIndicator(settings.isShowProgressIndicator())
                .allowBackButton(settings.isAllowBackButton())
                .startMessage(settings.getStartMessage())
                .finishMessage(settings.getFinishMessage())
                .headerHtml(settings.getHeaderHtml())
                .footerHtml(settings.getFooterHtml())
                .collectName(settings.isCollectName())
                .collectEmail(settings.isCollectEmail())
                .collectPhone(settings.isCollectPhone())
                .collectAddress(settings.isCollectAddress())
                .dataCollectionFields(toFieldResponses(settings.getDataCollectionFields()))
                .build();
    }

    private CampaignPreviewResponse.PagePreview toPreviewPage(SurveyPage page) {
        List<CampaignPreviewResponse.QuestionPreview> questions = page.getQuestions().stream()
                .sorted((a, b) -> Integer.compare(a.getSortOrder(), b.getSortOrder()))
                .map(this::toPreviewQuestion)
                .toList();
        return CampaignPreviewResponse.PagePreview.builder()
                .id(page.getId())
                .title(page.getTitle())
                .sortOrder(page.getSortOrder())
                .questions(questions)
                .build();
    }

    private CampaignPreviewResponse.QuestionPreview toPreviewQuestion(SurveyQuestion sq) {
        QuestionVersion questionVersion = null;
        if (sq.getQuestionVersionId() != null) {
            questionVersion = questionVersionRepository.findById(sq.getQuestionVersionId()).orElse(null);
        }

        Question fallbackQuestion = questionRepository.findById(sq.getQuestionId()).orElse(null);
        if (questionVersion == null && fallbackQuestion == null) {
            throw new ResourceNotFoundException("Question", sq.getQuestionId());
        }

        String text = questionVersion != null ? questionVersion.getText() : fallbackQuestion.getText();
        var type = questionVersion != null ? questionVersion.getType() : fallbackQuestion.getType();
        BigDecimal maxScore = questionVersion != null ? questionVersion.getMaxScore() : fallbackQuestion.getMaxScore();

        return CampaignPreviewResponse.QuestionPreview.builder()
                .id(sq.getId())
                .questionId(sq.getQuestionId())
                .questionVersionId(sq.getQuestionVersionId())
                .categoryVersionId(sq.getCategoryVersionId())
                .text(text)
                .type(type)
                .maxScore(maxScore)
                .mandatory(sq.isMandatory())
                .sortOrder(sq.getSortOrder())
                .optionConfig(questionVersion != null ? questionVersion.getOptionConfig()
                        : fallbackQuestion.getOptionConfig())
                .answerConfig(sq.getAnswerConfig())
                .build();
    }

    private CampaignPreviewResponse buildPreview(Campaign campaign, CampaignSettings settings, Survey survey) {
        List<CampaignPreviewResponse.PagePreview> pages = survey.getPages().stream()
                .sorted((a, b) -> Integer.compare(a.getSortOrder(), b.getSortOrder()))
                .map(this::toPreviewPage)
                .toList();

        return CampaignPreviewResponse.builder()
                .campaignId(campaign.getId())
                .tenantId(campaign.getTenantId())
                .campaignName(campaign.getName())
                .campaignStatus(campaign.getStatus())
                .authMode(campaign.getAuthMode())
                .surveyId(survey.getId())
                .surveyTitle(survey.getTitle())
                .surveyDescription(survey.getDescription())
                .showQuestionNumbers(settings.isShowQuestionNumbers())
                .showProgressIndicator(settings.isShowProgressIndicator())
                .allowBackButton(settings.isAllowBackButton())
                .startMessage(settings.getStartMessage())
                .finishMessage(settings.getFinishMessage())
                .headerHtml(settings.getHeaderHtml())
                .footerHtml(settings.getFooterHtml())
                .collectName(settings.isCollectName())
                .collectEmail(settings.isCollectEmail())
                .collectPhone(settings.isCollectPhone())
                .collectAddress(settings.isCollectAddress())
                .dataCollectionFields(toFieldResponses(settings.getDataCollectionFields()))
                .pages(pages)
                .build();
    }

    private List<DataCollectionFieldResponse> toFieldResponses(List<DataCollectionField> fields) {
        if (fields == null || fields.isEmpty()) return List.of();
        return fields.stream()
                .map(f -> DataCollectionFieldResponse.builder()
                        .id(f.getId())
                        .fieldKey(f.getFieldKey())
                        .label(f.getLabel())
                        .fieldType(f.getFieldType())
                        .required(f.isRequired())
                        .sortOrder(f.getSortOrder())
                        .enabled(f.isEnabled())
                        .build())
                .toList();
    }

    private AuthMode normalizeAccessMode(AuthMode requested) {
        if (requested == null || requested == AuthMode.PUBLIC) {
            return AuthMode.PUBLIC;
        }
        return AuthMode.PRIVATE;
    }

    private String resolveTenantId() {
        return TenantSupport.currentTenantOrDefault();
    }
}
