package com.bracits.surveyengine.campaign.service;

import com.bracits.surveyengine.campaign.dto.*;
import com.bracits.surveyengine.campaign.entity.*;
import com.bracits.surveyengine.campaign.repository.CampaignRepository;
import com.bracits.surveyengine.campaign.repository.CampaignSettingsRepository;
import com.bracits.surveyengine.campaign.repository.SurveyThemeRepository;
import com.bracits.surveyengine.common.exception.BusinessException;
import com.bracits.surveyengine.common.exception.ErrorCode;
import com.bracits.surveyengine.common.exception.ResourceNotFoundException;
import com.bracits.surveyengine.common.tenant.TenantSupport;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.bracits.surveyengine.questionbank.entity.Question;
import com.bracits.surveyengine.questionbank.entity.QuestionVersion;
import com.bracits.surveyengine.questionbank.entity.CategoryVersion;
import com.bracits.surveyengine.questionbank.repository.QuestionRepository;
import com.bracits.surveyengine.questionbank.repository.QuestionVersionRepository;
import com.bracits.surveyengine.questionbank.repository.CategoryVersionRepository;
import com.bracits.surveyengine.response.service.ResponseLockingService;
import com.bracits.surveyengine.scoring.service.WeightProfileService;
import com.bracits.surveyengine.search.service.SearchCacheInvalidationService;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of {@link CampaignService}.
 */
@Service
@RequiredArgsConstructor
public class CampaignServiceImpl implements CampaignService {

    private final CampaignRepository campaignRepository;
    private final CampaignSettingsRepository settingsRepository;
    private final SurveyThemeRepository surveyThemeRepository;
    private final SurveyRepository surveyRepository;
    private final QuestionRepository questionRepository;
    private final QuestionVersionRepository questionVersionRepository;
    private final CategoryVersionRepository categoryVersionRepository;
    private final SurveyService surveyService;
    private final WeightProfileService weightProfileService;
    private final PlanQuotaService planQuotaService;
    private final TenantService tenantService;
    private final ResponseLockingService responseLockingService;
    private final SearchCacheInvalidationService searchCacheInvalidationService;
    private final ObjectMapper objectMapper;

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
        surveyThemeRepository.save(SurveyTheme.builder()
                .campaignId(campaign.getId())
                .themeConfig(writeThemeConfig(defaultThemeConfig(campaign.getName(), null, null, null, null)))
                .build());
        searchCacheInvalidationService.invalidateTenantAfterCommit(tenantId);

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
        searchCacheInvalidationService.invalidateTenantAfterCommit(campaign.getTenantId());

        return toResponse(campaign);
    }

    @Override
    @Transactional
    @com.bracits.surveyengine.common.audit.annotation.Auditable(action = "CAMPAIGN_CLOSED")
    public void deactivate(UUID id) {
        Campaign campaign = findOrThrow(id);
        campaign.setActive(false);
        campaignRepository.save(campaign);
        searchCacheInvalidationService.invalidateTenantAfterCommit(campaign.getTenantId());
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
        saveTheme(campaignId, campaign, request.getTheme());

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
        SurveyThemeConfigDto theme = resolveThemeConfig(
                settings.getCampaignId(),
                null,
                settings.getStartMessage(),
                settings.getFinishMessage(),
                settings.getHeaderHtml(),
                settings.getFooterHtml());
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
                .theme(theme)
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
        List<CampaignPreviewResponse.CategoryPreview> categories = toPreviewCategories(page, questions);
        return CampaignPreviewResponse.PagePreview.builder()
                .id(page.getId())
                .title(page.getTitle())
                .sortOrder(page.getSortOrder())
                .categories(categories)
                .questions(questions)
                .build();
    }

    private List<CampaignPreviewResponse.CategoryPreview> toPreviewCategories(
            SurveyPage page,
            List<CampaignPreviewResponse.QuestionPreview> questions) {
        Map<UUID, List<CampaignPreviewResponse.QuestionPreview>> groupedQuestions = new LinkedHashMap<>();
        Map<UUID, Integer> categorySortOrders = new LinkedHashMap<>();

        for (CampaignPreviewResponse.QuestionPreview question : questions) {
            if (question.getCategoryVersionId() == null) {
                continue;
            }
            groupedQuestions.computeIfAbsent(question.getCategoryVersionId(), ignored -> new java.util.ArrayList<>())
                    .add(question);
            categorySortOrders.putIfAbsent(question.getCategoryVersionId(), question.getSortOrder());
        }

        return groupedQuestions.entrySet().stream()
                .map(entry -> {
                    UUID categoryVersionId = entry.getKey();
                    CategoryVersion categoryVersion = categoryVersionRepository.findById(categoryVersionId)
                            .orElseThrow(() -> new ResourceNotFoundException("CategoryVersion", categoryVersionId));
                    CampaignPreviewResponse.QuestionPreview firstQuestion = entry.getValue().get(0);
                    return CampaignPreviewResponse.CategoryPreview.builder()
                            .categoryVersionId(categoryVersionId)
                            .versionNumber(categoryVersion.getVersionNumber())
                            .name(categoryVersion.getName())
                            .description(categoryVersion.getDescription())
                            .weightPercentage(firstQuestion.getCategoryWeightPercentage())
                            .sortOrder(categorySortOrders.getOrDefault(categoryVersionId, Integer.MAX_VALUE))
                            .questions(entry.getValue())
                            .build();
                })
                .sorted((a, b) -> Integer.compare(
                        a.getSortOrder() != null ? a.getSortOrder() : Integer.MAX_VALUE,
                        b.getSortOrder() != null ? b.getSortOrder() : Integer.MAX_VALUE))
                .toList();
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
                .categoryWeightPercentage(sq.getCategoryWeightPercentage())
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
        SurveyThemeConfigDto theme = resolveThemeConfig(
                campaign.getId(),
                campaign.getName(),
                settings.getStartMessage(),
                settings.getFinishMessage(),
                settings.getHeaderHtml(),
                settings.getFooterHtml());
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
                .theme(theme)
                .collectName(settings.isCollectName())
                .collectEmail(settings.isCollectEmail())
                .collectPhone(settings.isCollectPhone())
                .collectAddress(settings.isCollectAddress())
                .dataCollectionFields(toFieldResponses(settings.getDataCollectionFields()))
                .pages(pages)
                .build();
    }

    private void saveTheme(UUID campaignId, Campaign campaign, SurveyThemeConfigDto incomingTheme) {
        SurveyTheme theme = surveyThemeRepository.findByCampaignId(campaignId)
                .orElseGet(() -> SurveyTheme.builder().campaignId(campaignId).build());

        SurveyThemeConfigDto normalized = mergeThemeWithDefaults(
                defaultThemeConfig(
                        campaign.getName(),
                        campaign.getDescription(),
                        "Thank you for completing this survey.",
                        null,
                        null),
                incomingTheme);
        theme.setThemeConfig(writeThemeConfig(normalized));
        surveyThemeRepository.save(theme);
    }

    private SurveyThemeConfigDto resolveThemeConfig(
            UUID campaignId,
            String campaignName,
            String startMessage,
            String finishMessage,
            String headerHtml,
            String footerHtml) {
        SurveyThemeConfigDto defaults = defaultThemeConfig(
                campaignName,
                startMessage,
                finishMessage,
                headerHtml,
                footerHtml);
        Optional<SurveyTheme> existing = surveyThemeRepository.findByCampaignId(campaignId);
        if (existing.isEmpty() || existing.get().getThemeConfig() == null || existing.get().getThemeConfig().isBlank()) {
            return defaults;
        }
        try {
            SurveyThemeConfigDto stored = objectMapper.readValue(existing.get().getThemeConfig(), SurveyThemeConfigDto.class);
            return mergeThemeWithDefaults(defaults, stored);
        } catch (JsonProcessingException ex) {
            return defaults;
        }
    }

    private String writeThemeConfig(SurveyThemeConfigDto theme) {
        try {
            return objectMapper.writeValueAsString(theme);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Failed to serialize survey theme config", ex);
        }
    }

    private SurveyThemeConfigDto mergeThemeWithDefaults(SurveyThemeConfigDto defaults, SurveyThemeConfigDto incoming) {
        if (incoming == null) return defaults;
        return SurveyThemeConfigDto.builder()
                .templateKey(coalesce(incoming.getTemplateKey(), defaults.getTemplateKey()))
                .paletteKey(coalesce(incoming.getPaletteKey(), defaults.getPaletteKey()))
                .palette(mergePalette(defaults.getPalette(), incoming.getPalette()))
                .branding(mergeBranding(defaults.getBranding(), incoming.getBranding()))
                .layout(mergeLayout(defaults.getLayout(), incoming.getLayout()))
                .motion(mergeMotion(defaults.getMotion(), incoming.getMotion()))
                .header(mergeHeader(defaults.getHeader(), incoming.getHeader()))
                .footer(mergeFooter(defaults.getFooter(), incoming.getFooter()))
                .advanced(mergeAdvanced(defaults.getAdvanced(), incoming.getAdvanced()))
                .build();
    }

    private SurveyThemeConfigDto defaultThemeConfig(
            String campaignName,
            String subtitle,
            String finishMessage,
            String headerHtml,
            String footerHtml) {
        String resolvedTitle = campaignName == null || campaignName.isBlank() ? "Survey Experience" : campaignName;
        String resolvedSubtitle = subtitle == null || subtitle.isBlank() ? "Share your feedback with clarity and confidence." : subtitle;
        String resolvedFooterLine = finishMessage == null || finishMessage.isBlank()
                ? "Thank you for completing this survey."
                : finishMessage;

        return SurveyThemeConfigDto.builder()
                .templateKey("aurora-premium")
                .paletteKey("ocean-aurora")
                .palette(SurveyThemeConfigDto.Palette.builder()
                        .background("#f8fbfb")
                        .shell("#ffffff")
                        .panel("#e8f6f4")
                        .card("#ffffff")
                        .border("#9fd6cf")
                        .textPrimary("#102a43")
                        .textSecondary("#4e676c")
                        .primary("#0f766e")
                        .primaryText("#f8fffe")
                        .accent("#14b8a6")
                        .accentSoft("#d8f5f1")
                        .headerBackground("#102a43")
                        .headerText("#f8fffe")
                        .footerBackground("#edf9f7")
                        .footerText("#35545a")
                        .build())
                .branding(SurveyThemeConfigDto.Branding.builder()
                        .brandLabel("Confidential Evaluation Ledger")
                        .logoPosition("left")
                        .fontFamily("\"Iowan Old Style\", \"Palatino Linotype\", \"Book Antiqua\", Georgia, serif")
                        .build())
                .layout(SurveyThemeConfigDto.Layout.builder()
                        .contentWidth("standard")
                        .headerStyle("hero")
                        .headerAlignment("left")
                        .footerStyle("support")
                        .footerAlignment("left")
                        .sectionStyle("panel")
                        .questionCardStyle("soft")
                        .categorySeparatorStyle("divider")
                        .build())
                .motion(SurveyThemeConfigDto.Motion.builder()
                        .animationPreset("subtle")
                        .build())
                .header(SurveyThemeConfigDto.Header.builder()
                        .enabled(true)
                        .eyebrow("Confidential Evaluation Ledger")
                        .title(resolvedTitle)
                        .subtitle(resolvedSubtitle)
                        .note("")
                        .build())
                .footer(SurveyThemeConfigDto.Footer.builder()
                        .enabled(true)
                        .line1(resolvedFooterLine)
                        .line2("Need assistance? Contact your survey administrator for support.")
                        .legal("Responses are securely processed under your organization's data policy.")
                        .build())
                .advanced(SurveyThemeConfigDto.Advanced.builder()
                        .useCustomHeaderHtml(false)
                        .useCustomFooterHtml(false)
                        .customHeaderHtml(headerHtml)
                        .customFooterHtml(footerHtml)
                        .customCss("")
                        .build())
                .build();
    }

    private SurveyThemeConfigDto.Palette mergePalette(
            SurveyThemeConfigDto.Palette defaults,
            SurveyThemeConfigDto.Palette incoming) {
        if (incoming == null) return defaults;
        return SurveyThemeConfigDto.Palette.builder()
                .background(coalesce(incoming.getBackground(), defaults.getBackground()))
                .shell(coalesce(incoming.getShell(), defaults.getShell()))
                .panel(coalesce(incoming.getPanel(), defaults.getPanel()))
                .card(coalesce(incoming.getCard(), defaults.getCard()))
                .border(coalesce(incoming.getBorder(), defaults.getBorder()))
                .textPrimary(coalesce(incoming.getTextPrimary(), defaults.getTextPrimary()))
                .textSecondary(coalesce(incoming.getTextSecondary(), defaults.getTextSecondary()))
                .primary(coalesce(incoming.getPrimary(), defaults.getPrimary()))
                .primaryText(coalesce(incoming.getPrimaryText(), defaults.getPrimaryText()))
                .accent(coalesce(incoming.getAccent(), defaults.getAccent()))
                .accentSoft(coalesce(incoming.getAccentSoft(), defaults.getAccentSoft()))
                .headerBackground(coalesce(incoming.getHeaderBackground(), defaults.getHeaderBackground()))
                .headerText(coalesce(incoming.getHeaderText(), defaults.getHeaderText()))
                .footerBackground(coalesce(incoming.getFooterBackground(), defaults.getFooterBackground()))
                .footerText(coalesce(incoming.getFooterText(), defaults.getFooterText()))
                .build();
    }

    private SurveyThemeConfigDto.Branding mergeBranding(
            SurveyThemeConfigDto.Branding defaults,
            SurveyThemeConfigDto.Branding incoming) {
        if (incoming == null) return defaults;
        return SurveyThemeConfigDto.Branding.builder()
                .brandLabel(coalesce(incoming.getBrandLabel(), defaults.getBrandLabel()))
                .logoUrl(coalesce(incoming.getLogoUrl(), defaults.getLogoUrl()))
                .logoPosition(coalesce(incoming.getLogoPosition(), defaults.getLogoPosition()))
                .fontFamily(coalesce(incoming.getFontFamily(), defaults.getFontFamily()))
                .build();
    }

    private SurveyThemeConfigDto.Layout mergeLayout(
            SurveyThemeConfigDto.Layout defaults,
            SurveyThemeConfigDto.Layout incoming) {
        if (incoming == null) return defaults;
        return SurveyThemeConfigDto.Layout.builder()
                .contentWidth(coalesce(incoming.getContentWidth(), defaults.getContentWidth()))
                .headerStyle(coalesce(incoming.getHeaderStyle(), defaults.getHeaderStyle()))
                .headerAlignment(coalesce(incoming.getHeaderAlignment(), defaults.getHeaderAlignment()))
                .footerStyle(coalesce(incoming.getFooterStyle(), defaults.getFooterStyle()))
                .footerAlignment(coalesce(incoming.getFooterAlignment(), defaults.getFooterAlignment()))
                .sectionStyle(coalesce(incoming.getSectionStyle(), defaults.getSectionStyle()))
                .questionCardStyle(coalesce(incoming.getQuestionCardStyle(), defaults.getQuestionCardStyle()))
                .categorySeparatorStyle(coalesce(incoming.getCategorySeparatorStyle(), defaults.getCategorySeparatorStyle()))
                .build();
    }

    private SurveyThemeConfigDto.Motion mergeMotion(
            SurveyThemeConfigDto.Motion defaults,
            SurveyThemeConfigDto.Motion incoming) {
        if (incoming == null) return defaults;
        return SurveyThemeConfigDto.Motion.builder()
                .animationPreset(coalesce(incoming.getAnimationPreset(), defaults.getAnimationPreset()))
                .build();
    }

    private SurveyThemeConfigDto.Header mergeHeader(
            SurveyThemeConfigDto.Header defaults,
            SurveyThemeConfigDto.Header incoming) {
        if (incoming == null) return defaults;
        return SurveyThemeConfigDto.Header.builder()
                .enabled(incoming.isEnabled())
                .eyebrow(coalesce(incoming.getEyebrow(), defaults.getEyebrow()))
                .title(coalesce(incoming.getTitle(), defaults.getTitle()))
                .subtitle(coalesce(incoming.getSubtitle(), defaults.getSubtitle()))
                .note(coalesce(incoming.getNote(), defaults.getNote()))
                .build();
    }

    private SurveyThemeConfigDto.Footer mergeFooter(
            SurveyThemeConfigDto.Footer defaults,
            SurveyThemeConfigDto.Footer incoming) {
        if (incoming == null) return defaults;
        return SurveyThemeConfigDto.Footer.builder()
                .enabled(incoming.isEnabled())
                .line1(coalesce(incoming.getLine1(), defaults.getLine1()))
                .line2(coalesce(incoming.getLine2(), defaults.getLine2()))
                .legal(coalesce(incoming.getLegal(), defaults.getLegal()))
                .build();
    }

    private SurveyThemeConfigDto.Advanced mergeAdvanced(
            SurveyThemeConfigDto.Advanced defaults,
            SurveyThemeConfigDto.Advanced incoming) {
        if (incoming == null) return defaults;
        return SurveyThemeConfigDto.Advanced.builder()
                .useCustomHeaderHtml(incoming.isUseCustomHeaderHtml())
                .useCustomFooterHtml(incoming.isUseCustomFooterHtml())
                .customHeaderHtml(coalesce(incoming.getCustomHeaderHtml(), defaults.getCustomHeaderHtml()))
                .customFooterHtml(coalesce(incoming.getCustomFooterHtml(), defaults.getCustomFooterHtml()))
                .customCss(coalesce(incoming.getCustomCss(), defaults.getCustomCss()))
                .build();
    }

    private String coalesce(String preferred, String fallback) {
        return preferred == null || preferred.isBlank() ? fallback : preferred;
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
