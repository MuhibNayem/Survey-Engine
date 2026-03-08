package com.bracits.surveyengine.response.service;

import com.bracits.surveyengine.auth.dto.ResponderAccessIdentity;
import com.bracits.surveyengine.auth.dto.TokenValidationResult;
import com.bracits.surveyengine.auth.entity.AuthenticationMode;
import com.bracits.surveyengine.auth.repository.AuthProfileRepository;
import com.bracits.surveyengine.auth.service.OidcResponderAuthService;
import com.bracits.surveyengine.auth.service.TokenValidationService;
import com.bracits.surveyengine.campaign.entity.AuthMode;
import com.bracits.surveyengine.campaign.entity.Campaign;
import com.bracits.surveyengine.campaign.entity.CampaignSettings;
import com.bracits.surveyengine.campaign.entity.CampaignStatus;
import com.bracits.surveyengine.campaign.repository.CampaignRepository;
import com.bracits.surveyengine.campaign.repository.CampaignSettingsRepository;
import com.bracits.surveyengine.common.exception.BusinessException;
import com.bracits.surveyengine.common.exception.ErrorCode;
import com.bracits.surveyengine.common.exception.ResourceNotFoundException;
import com.bracits.surveyengine.common.tenant.TenantSupport;
import com.bracits.surveyengine.questionbank.entity.QuestionType;
import com.bracits.surveyengine.questionbank.entity.QuestionVersion;
import com.bracits.surveyengine.questionbank.repository.QuestionVersionRepository;
import com.bracits.surveyengine.response.dto.ResponseSubmissionRequest;
import com.bracits.surveyengine.response.dto.SurveyResponseResponse;
import com.bracits.surveyengine.response.entity.Answer;
import com.bracits.surveyengine.response.entity.ResponseStatus;
import com.bracits.surveyengine.response.entity.SurveyResponse;
import com.bracits.surveyengine.response.repository.SurveyResponseRepository;
import com.bracits.surveyengine.response.repository.SurveyResponseSpecification;
import com.bracits.surveyengine.scoring.dto.ScoreResult;
import com.bracits.surveyengine.scoring.service.ScoringEngineService;
import com.bracits.surveyengine.subscription.service.PlanQuotaService;
import com.bracits.surveyengine.survey.entity.SurveySnapshot;
import com.bracits.surveyengine.survey.repository.SurveySnapshotRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
    private final SurveySnapshotRepository surveySnapshotRepository;
    private final QuestionVersionRepository questionVersionRepository;
    private final ScoringEngineService scoringEngineService;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    @com.bracits.surveyengine.common.audit.annotation.Auditable(action = "RESPONSE_SUBMITTED")
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
        SurveySnapshotContext snapshotContext = loadSnapshotContext(campaign.getSurveySnapshotId());
        List<ValidatedAnswer> validatedAnswers = validateAndNormalizeAnswers(snapshotContext, request.getAnswers());
        ScoreResult weightedScoreResult = null;
        if (campaign.getDefaultWeightProfileId() != null) {
            Map<UUID, BigDecimal> categoryRawScores = computeCategoryRawScores(snapshotContext, validatedAnswers);
            weightedScoreResult = com.bracits.surveyengine.common.tenant.TenantSupport.executeWithTenant(
                    campaign.getTenantId(),
                    () -> scoringEngineService.calculateScore(campaign.getDefaultWeightProfileId(), categoryRawScores)
            );
        }

        String respondentMetadataJson = null;
        if (request.getRespondentMetadata() != null && !request.getRespondentMetadata().isEmpty()) {
            try {
                respondentMetadataJson = objectMapper.writeValueAsString(request.getRespondentMetadata());
            } catch (Exception e) {
                // Log and ignore, or throw. Throwing is safer for data integrity.
                throw new RuntimeException("Failed to serialize respondent metadata", e);
            }
        }

        // 3. Build and save response
        SurveyResponse surveyResponse = SurveyResponse.builder()
                .campaignId(campaign.getId())
                .surveySnapshotId(campaign.getSurveySnapshotId())
                .tenantId(campaign.getTenantId())
                .respondentIdentifier(request.getRespondentIdentifier())
                .respondentIp(request.getRespondentIp())
                .respondentDeviceFingerprint(request.getRespondentDeviceFingerprint())
                .respondentMetadata(respondentMetadataJson)
                .status(ResponseStatus.SUBMITTED)
                .submittedAt(Instant.now())
                .weightProfileId(weightedScoreResult != null ? weightedScoreResult.getWeightProfileId() : null)
                .weightedTotalScore(weightedScoreResult != null ? weightedScoreResult.getTotalScore() : null)
                .scoredAt(weightedScoreResult != null ? Instant.now() : null)
                .build();

        for (ValidatedAnswer ar : validatedAnswers) {
            Answer answer = Answer.builder()
                    .surveyResponse(surveyResponse)
                    .questionId(ar.questionId())
                    .questionVersionId(ar.questionVersionId())
                    .value(ar.value())
                    .score(ar.score())
                    .build();
            surveyResponse.getAnswers().add(answer);
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
    public Page<SurveyResponseResponse> getByCampaignId(UUID campaignId, Map<String, String> metadataFilters, Pageable pageable) {
        String tenantId = TenantSupport.currentTenantOrDefault();
        campaignRepository.findByIdAndTenantId(campaignId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign", campaignId));
                
        if (metadataFilters != null && !metadataFilters.isEmpty()) {
            return responseRepository.findAll(
                    SurveyResponseSpecification.matchesFilters(campaignId, tenantId, metadataFilters), 
                    pageable)
                    .map(this::toResponse);
        }
        
        return responseRepository.findByCampaignIdAndTenantId(campaignId, tenantId, pageable)
                .map(this::toResponse);
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

    private List<ValidatedAnswer> validateAndNormalizeAnswers(
            SurveySnapshotContext snapshotContext,
            List<ResponseSubmissionRequest.AnswerRequest> submittedAnswers) {
        List<ResponseSubmissionRequest.AnswerRequest> answers = submittedAnswers == null ? List.of() : submittedAnswers;
        Map<UUID, QuestionVersion> questionVersionCache = new HashMap<>();
        Set<UUID> seenQuestionIds = new LinkedHashSet<>();
        List<ValidatedAnswer> validated = new ArrayList<>();

        for (ResponseSubmissionRequest.AnswerRequest answer : answers) {
            UUID questionId = answer.getQuestionId();
            if (questionId == null) {
                throw validationError("questionId is required");
            }
            if (!seenQuestionIds.add(questionId)) {
                throw validationError("Duplicate answer submitted for question " + questionId);
            }

            SnapshotQuestion snapshotQuestion = snapshotContext.questionsById().get(questionId);
            if (snapshotQuestion == null) {
                throw validationError("Question " + questionId + " is not part of the campaign survey");
            }
            if (answer.getQuestionVersionId() != null
                    && !answer.getQuestionVersionId().equals(snapshotQuestion.questionVersionId())) {
                throw validationError("questionVersionId mismatch for question " + questionId);
            }

            QuestionVersion questionVersion = loadQuestionVersion(snapshotQuestion.questionVersionId(),
                    questionVersionCache);
            if (!questionId.equals(questionVersion.getQuestionId())) {
                throw validationError("Snapshot question-version binding is invalid for question " + questionId);
            }

            AnswerPayload payload = validateAnswerPayload(
                    questionVersion,
                    snapshotQuestion.optionConfig() != null
                            ? snapshotQuestion.optionConfig()
                            : questionVersion.getOptionConfig(),
                    snapshotQuestion.answerConfig(),
                    answer.getValue());
            validated.add(new ValidatedAnswer(
                    questionId,
                    snapshotQuestion.questionVersionId(),
                    payload.normalizedValue(),
                    payload.score()));
        }

        enforceMandatoryAnswers(snapshotContext, validated);
        return validated;
    }

    private SurveySnapshotContext loadSnapshotContext(UUID surveySnapshotId) {
        SurveySnapshot snapshot = surveySnapshotRepository.findById(surveySnapshotId)
                .orElseThrow(() -> new ResourceNotFoundException("SurveySnapshot", surveySnapshotId));
        try {
            JsonNode root = objectMapper.readTree(snapshot.getSnapshotData());
            JsonNode pages = root.path("pages");
            if (!pages.isArray()) {
                throw validationError("Survey snapshot is invalid: pages is missing");
            }

            Map<UUID, SnapshotQuestion> questionsById = new LinkedHashMap<>();
            for (JsonNode pageNode : pages) {
                JsonNode questions = pageNode.path("questions");
                if (!questions.isArray()) {
                    continue;
                }
                for (JsonNode questionNode : questions) {
                    UUID questionId = parseUuid(questionNode.path("questionId").asText(null), "questionId");
                    UUID questionVersionId = parseUuid(
                            questionNode.path("questionVersionId").asText(null), "questionVersionId");
                    UUID categoryId = questionNode.hasNonNull("categoryId")
                            ? parseUuid(questionNode.path("categoryId").asText(null), "categoryId")
                            : null;
                    boolean mandatory = questionNode.path("mandatory").asBoolean(false);
                    String optionConfig = extractOptionConfig(questionNode.get("optionConfig"));
                    String answerConfig = extractAnswerConfig(questionNode.get("answerConfig"));

                    SnapshotQuestion previous = questionsById.putIfAbsent(
                            questionId,
                            new SnapshotQuestion(questionId, questionVersionId, categoryId, mandatory, optionConfig,
                                    answerConfig));
                    if (previous != null) {
                        throw validationError("Survey snapshot contains duplicate questionId: " + questionId);
                    }
                }
            }
            return new SurveySnapshotContext(questionsById);
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception ex) {
            throw validationError("Survey snapshot cannot be parsed for response validation");
        }
    }

    private UUID parseUuid(String raw, String field) {
        if (raw == null || raw.isBlank()) {
            throw validationError("Survey snapshot is invalid: " + field + " is missing");
        }
        try {
            return UUID.fromString(raw);
        } catch (Exception ex) {
            throw validationError("Survey snapshot is invalid: " + field + " has bad UUID value");
        }
    }

    private String extractAnswerConfig(JsonNode answerConfigNode) {
        if (answerConfigNode == null || answerConfigNode.isNull()) {
            return null;
        }
        try {
            if (answerConfigNode.isTextual()) {
                String raw = answerConfigNode.asText();
                return raw == null || raw.isBlank() ? null : raw;
            }
            return objectMapper.writeValueAsString(answerConfigNode);
        } catch (Exception ex) {
            throw validationError("Survey snapshot answerConfig is invalid");
        }
    }

    private String extractOptionConfig(JsonNode optionConfigNode) {
        if (optionConfigNode == null || optionConfigNode.isNull()) {
            return null;
        }
        try {
            if (optionConfigNode.isTextual()) {
                String raw = optionConfigNode.asText();
                return raw == null || raw.isBlank() ? null : raw;
            }
            return objectMapper.writeValueAsString(optionConfigNode);
        } catch (Exception ex) {
            throw validationError("Survey snapshot optionConfig is invalid");
        }
    }

    private QuestionVersion loadQuestionVersion(UUID questionVersionId, Map<UUID, QuestionVersion> cache) {
        return cache.computeIfAbsent(questionVersionId, id -> questionVersionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("QuestionVersion", id)));
    }

    private void enforceMandatoryAnswers(SurveySnapshotContext snapshotContext, List<ValidatedAnswer> answers) {
        Set<UUID> answeredQuestionIds = answers.stream()
                .map(ValidatedAnswer::questionId)
                .collect(LinkedHashSet::new, Set::add, Set::addAll);
        List<UUID> missingMandatory = snapshotContext.questionsById().values().stream()
                .filter(SnapshotQuestion::mandatory)
                .map(SnapshotQuestion::questionId)
                .filter(id -> !answeredQuestionIds.contains(id))
                .toList();
        if (!missingMandatory.isEmpty()) {
            throw validationError("Missing mandatory answers for questionIds: " + missingMandatory);
        }
    }

    private AnswerPayload validateAnswerPayload(
            QuestionVersion questionVersion,
            String optionConfig,
            String answerConfig,
            String rawValue) {
        if (rawValue == null || rawValue.isBlank()) {
            throw validationError("Answer value is required for question " + questionVersion.getQuestionId());
        }
        JsonNode config = parseAnswerConfig(answerConfig, questionVersion.getType());
        JsonNode optionNode = parseOptionConfig(optionConfig, questionVersion.getType());
        JsonNode effectiveOptionNode = optionNode != null ? optionNode : config;
        Map<String, OptionSpec> options = parseOptions(effectiveOptionNode, questionVersion.getType());
        return switch (questionVersion.getType()) {
            case RATING_SCALE -> validateRatingScaleAnswer(questionVersion, config, rawValue);
            case SINGLE_CHOICE -> validateSingleChoiceAnswer(questionVersion, options, rawValue);
            case MULTIPLE_CHOICE -> validateMultipleChoiceAnswer(questionVersion, config, options, rawValue);
            case RANK -> validateRankAnswer(questionVersion, config, options, rawValue);
        };
    }

    private JsonNode parseAnswerConfig(String rawConfig, QuestionType questionType) {
        if (rawConfig == null || rawConfig.isBlank()) {
            return null;
        }
        try {
            JsonNode node = objectMapper.readTree(rawConfig);
            if (!node.isObject()) {
                throw validationError("answerConfig must be a JSON object for question type " + questionType);
            }
            return node;
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception ex) {
            throw validationError("answerConfig is invalid JSON for question type " + questionType);
        }
    }

    private JsonNode parseOptionConfig(String rawConfig, QuestionType questionType) {
        if (rawConfig == null || rawConfig.isBlank()) {
            return null;
        }
        try {
            JsonNode node = objectMapper.readTree(rawConfig);
            if (!node.isObject()) {
                throw validationError("optionConfig must be a JSON object for question type " + questionType);
            }
            return node;
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception ex) {
            throw validationError("optionConfig is invalid JSON for question type " + questionType);
        }
    }

    private AnswerPayload validateRatingScaleAnswer(QuestionVersion questionVersion, JsonNode config, String rawValue) {
        BigDecimal value = parseDecimal(rawValue, "RATING_SCALE answer must be numeric");
        BigDecimal min = readBigDecimal(config, "min", BigDecimal.ONE);
        BigDecimal max = readBigDecimal(config, "max", questionVersion.getMaxScore());
        if (max.compareTo(min) <= 0) {
            throw validationError("RATING_SCALE config requires max > min");
        }
        if (value.compareTo(min) < 0 || value.compareTo(max) > 0) {
            throw validationError("RATING_SCALE value out of range [%s, %s]".formatted(min, max));
        }
        BigDecimal step = readBigDecimalOptional(config, "step");
        if (step != null) {
            if (step.compareTo(BigDecimal.ZERO) <= 0) {
                throw validationError("RATING_SCALE step must be > 0");
            }
            BigDecimal delta = value.subtract(min);
            BigDecimal remainder = delta.remainder(step).abs();
            BigDecimal epsilon = new BigDecimal("0.000001");
            if (remainder.compareTo(epsilon) > 0 && step.subtract(remainder).abs().compareTo(epsilon) > 0) {
                throw validationError("RATING_SCALE value must align with step " + step);
            }
        }
        BigDecimal ratio = value.subtract(min)
                .divide(max.subtract(min), 8, RoundingMode.HALF_UP);
        BigDecimal score = ratio.multiply(questionVersion.getMaxScore()).setScale(2, RoundingMode.HALF_UP);
        return new AnswerPayload(value.stripTrailingZeros().toPlainString(), score);
    }

    private AnswerPayload validateSingleChoiceAnswer(
            QuestionVersion questionVersion,
            Map<String, OptionSpec> options,
            String rawValue) {
        String value = rawValue.trim();
        if (value.isBlank()) {
            throw validationError("SINGLE_CHOICE answer cannot be empty");
        }
        if (!options.isEmpty() && !options.containsKey(value)) {
            throw validationError("SINGLE_CHOICE value must be one configured option");
        }
        BigDecimal score = options.isEmpty() ? null : options.get(value).score();
        validateScoreWithinMax(score, questionVersion.getMaxScore(), "SINGLE_CHOICE");
        return new AnswerPayload(value, score);
    }

    private AnswerPayload validateMultipleChoiceAnswer(
            QuestionVersion questionVersion,
            JsonNode config,
            Map<String, OptionSpec> options,
            String rawValue) {
        List<String> selections = parseStringList(rawValue, "MULTIPLE_CHOICE");
        ensureNoDuplicates(selections, "MULTIPLE_CHOICE");
        if (!options.isEmpty()) {
            for (String selected : selections) {
                if (!options.containsKey(selected)) {
                    throw validationError("MULTIPLE_CHOICE selection '%s' is not configured".formatted(selected));
                }
            }
        }

        int minSelections = readInt(config, "minSelections", 1);
        int maxSelections = readInt(config, "maxSelections", options.isEmpty() ? Integer.MAX_VALUE : options.size());
        if (minSelections < 1) {
            throw validationError("minSelections must be >= 1");
        }
        if (maxSelections < minSelections) {
            throw validationError("maxSelections must be >= minSelections");
        }
        if (selections.size() < minSelections || selections.size() > maxSelections) {
            throw validationError("MULTIPLE_CHOICE selections must be between %d and %d"
                    .formatted(minSelections, maxSelections));
        }

        BigDecimal score = null;
        if (!options.isEmpty() && options.values().stream().anyMatch(o -> o.score() != null)) {
            score = BigDecimal.ZERO;
            for (String selected : selections) {
                BigDecimal optionScore = options.get(selected).score();
                if (optionScore != null) {
                    score = score.add(optionScore);
                }
            }
            if (score.compareTo(questionVersion.getMaxScore()) > 0) {
                score = questionVersion.getMaxScore();
            }
            score = score.setScale(2, RoundingMode.HALF_UP);
            validateScoreWithinMax(score, questionVersion.getMaxScore(), "MULTIPLE_CHOICE");
        }

        return new AnswerPayload(writeJsonArray(selections), score);
    }

    private AnswerPayload validateRankAnswer(
            QuestionVersion questionVersion,
            JsonNode config,
            Map<String, OptionSpec> options,
            String rawValue) {
        List<String> rankings = parseStringList(rawValue, "RANK");
        ensureNoDuplicates(rankings, "RANK");
        if (!options.isEmpty()) {
            for (String rankedValue : rankings) {
                if (!options.containsKey(rankedValue)) {
                    throw validationError("RANK value '%s' is not configured".formatted(rankedValue));
                }
            }
        }

        boolean allowPartial = readBoolean(config, "allowPartialRanking", false);
        if (!allowPartial && !options.isEmpty() && rankings.size() != options.size()) {
            throw validationError("RANK answer must include all configured options");
        }

        BigDecimal score = null;
        List<String> correctOrder = readStringArray(config, "correctOrder");
        if (!correctOrder.isEmpty()) {
            int compareLen = Math.min(rankings.size(), correctOrder.size());
            int matched = 0;
            for (int i = 0; i < compareLen; i++) {
                if (rankings.get(i).equals(correctOrder.get(i))) {
                    matched++;
                }
            }
            score = BigDecimal.valueOf(matched)
                    .divide(BigDecimal.valueOf(correctOrder.size()), 8, RoundingMode.HALF_UP)
                    .multiply(questionVersion.getMaxScore())
                    .setScale(2, RoundingMode.HALF_UP);
        } else if (!options.isEmpty() && options.values().stream().anyMatch(o -> o.score() != null)) {
            int n = rankings.size();
            BigDecimal raw = BigDecimal.ZERO;
            List<BigDecimal> factors = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                String rankedValue = rankings.get(i);
                BigDecimal factor = options.get(rankedValue).score() == null
                        ? BigDecimal.ONE
                        : options.get(rankedValue).score();
                factors.add(factor);
                raw = raw.add(BigDecimal.valueOf(n - i).multiply(factor));
            }
            factors.sort((a, b) -> b.compareTo(a));
            BigDecimal maxRaw = BigDecimal.ZERO;
            for (int i = 0; i < factors.size(); i++) {
                maxRaw = maxRaw.add(BigDecimal.valueOf(n - i).multiply(factors.get(i)));
            }
            if (maxRaw.compareTo(BigDecimal.ZERO) > 0) {
                score = raw.divide(maxRaw, 8, RoundingMode.HALF_UP)
                        .multiply(questionVersion.getMaxScore())
                        .setScale(2, RoundingMode.HALF_UP);
            }
        }

        validateScoreWithinMax(score, questionVersion.getMaxScore(), "RANK");
        return new AnswerPayload(writeJsonArray(rankings), score);
    }

    private Map<String, OptionSpec> parseOptions(JsonNode config, QuestionType questionType) {
        if (config == null || config.get("options") == null || config.get("options").isNull()) {
            return Map.of();
        }
        JsonNode optionsNode = config.get("options");
        if (!optionsNode.isArray()) {
            throw validationError("optionConfig.options must be an array for " + questionType);
        }
        Map<String, OptionSpec> options = new LinkedHashMap<>();
        for (JsonNode optionNode : optionsNode) {
            String value;
            BigDecimal score = null;
            if (optionNode.isTextual()) {
                value = optionNode.asText();
            } else if (optionNode.isObject()) {
                value = optionNode.path("value").asText(null);
                score = readBigDecimalOptional(optionNode, "score");
            } else {
                throw validationError("Each option must be string or object for " + questionType);
            }
            if (value == null || value.isBlank()) {
                throw validationError("Each option value must be non-empty for " + questionType);
            }
            if (score != null && score.compareTo(BigDecimal.ZERO) < 0) {
                throw validationError("Option score must be >= 0 for " + questionType);
            }
            OptionSpec previous = options.putIfAbsent(value, new OptionSpec(value, score));
            if (previous != null) {
                throw validationError("Duplicate option value '%s' in optionConfig".formatted(value));
            }
        }
        return options;
    }

    private List<String> parseStringList(String raw, String questionTypeLabel) {
        if (raw == null || raw.isBlank()) {
            throw validationError(questionTypeLabel + " answer cannot be empty");
        }
        try {
            String trimmed = raw.trim();
            List<String> values = new ArrayList<>();
            if (trimmed.startsWith("[")) {
                JsonNode arr = objectMapper.readTree(trimmed);
                if (!arr.isArray()) {
                    throw validationError(questionTypeLabel + " answer must be an array");
                }
                for (JsonNode item : arr) {
                    if (!item.isTextual()) {
                        throw validationError(questionTypeLabel + " answer array must contain only strings");
                    }
                    String value = item.asText().trim();
                    if (value.isBlank()) {
                        throw validationError(questionTypeLabel + " answer array contains blank value");
                    }
                    values.add(value);
                }
            } else {
                for (String part : trimmed.split(",")) {
                    String value = part.trim();
                    if (value.startsWith("\"") && value.endsWith("\"") && value.length() >= 2) {
                        value = value.substring(1, value.length() - 1);
                    }
                    if (value.isBlank()) {
                        throw validationError(questionTypeLabel + " answer contains blank value");
                    }
                    values.add(value);
                }
            }
            if (values.isEmpty()) {
                throw validationError(questionTypeLabel + " answer must contain at least one value");
            }
            return values;
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception ex) {
            throw validationError(questionTypeLabel + " answer format is invalid");
        }
    }

    private void ensureNoDuplicates(List<String> values, String questionTypeLabel) {
        Set<String> unique = new LinkedHashSet<>(values);
        if (unique.size() != values.size()) {
            throw validationError(questionTypeLabel + " answer contains duplicate values");
        }
    }

    private BigDecimal parseDecimal(String raw, String message) {
        try {
            return new BigDecimal(raw.trim());
        } catch (Exception ex) {
            throw validationError(message);
        }
    }

    private BigDecimal readBigDecimal(JsonNode node, String field, BigDecimal defaultValue) {
        BigDecimal value = readBigDecimalOptional(node, field);
        return value != null ? value : defaultValue;
    }

    private BigDecimal readBigDecimalOptional(JsonNode node, String field) {
        if (node == null || node.get(field) == null || node.get(field).isNull()) {
            return null;
        }
        JsonNode fieldNode = node.get(field);
        if (!fieldNode.isNumber() && !fieldNode.isTextual()) {
            throw validationError("answerConfig.%s must be numeric".formatted(field));
        }
        try {
            return new BigDecimal(fieldNode.asText());
        } catch (Exception ex) {
            throw validationError("answerConfig.%s must be numeric".formatted(field));
        }
    }

    private int readInt(JsonNode node, String field, int defaultValue) {
        if (node == null || node.get(field) == null || node.get(field).isNull()) {
            return defaultValue;
        }
        JsonNode fieldNode = node.get(field);
        if (!fieldNode.isInt() && !fieldNode.isTextual()) {
            throw validationError("answerConfig.%s must be integer".formatted(field));
        }
        try {
            return Integer.parseInt(fieldNode.asText());
        } catch (Exception ex) {
            throw validationError("answerConfig.%s must be integer".formatted(field));
        }
    }

    private boolean readBoolean(JsonNode node, String field, boolean defaultValue) {
        if (node == null || node.get(field) == null || node.get(field).isNull()) {
            return defaultValue;
        }
        JsonNode fieldNode = node.get(field);
        if (fieldNode.isBoolean()) {
            return fieldNode.asBoolean();
        }
        if (!fieldNode.isTextual()) {
            throw validationError("answerConfig.%s must be boolean".formatted(field));
        }
        String raw = fieldNode.asText().trim().toLowerCase();
        if ("true".equals(raw)) {
            return true;
        }
        if ("false".equals(raw)) {
            return false;
        }
        throw validationError("answerConfig.%s must be boolean".formatted(field));
    }

    private List<String> readStringArray(JsonNode node, String field) {
        if (node == null || node.get(field) == null || node.get(field).isNull()) {
            return List.of();
        }
        JsonNode arr = node.get(field);
        if (!arr.isArray()) {
            throw validationError("answerConfig.%s must be an array".formatted(field));
        }
        List<String> values = new ArrayList<>();
        for (JsonNode item : arr) {
            if (!item.isTextual()) {
                throw validationError("answerConfig.%s must contain only strings".formatted(field));
            }
            String value = item.asText().trim();
            if (value.isBlank()) {
                throw validationError("answerConfig.%s contains blank value".formatted(field));
            }
            values.add(value);
        }
        return values;
    }

    private void validateScoreWithinMax(BigDecimal score, BigDecimal maxScore, String questionTypeLabel) {
        if (score == null) {
            return;
        }
        if (score.compareTo(BigDecimal.ZERO) < 0 || score.compareTo(maxScore) > 0) {
            throw validationError("%s score must be between 0 and question maxScore".formatted(questionTypeLabel));
        }
    }

    private String writeJsonArray(List<String> values) {
        try {
            return objectMapper.writeValueAsString(values);
        } catch (Exception ex) {
            throw validationError("Failed to normalize answer payload");
        }
    }

    private BusinessException validationError(String message) {
        return new BusinessException(ErrorCode.VALIDATION_FAILED, message);
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
                .weightProfileId(r.getWeightProfileId())
                .weightedTotalScore(r.getWeightedTotalScore())
                .scoredAt(r.getScoredAt())
                .answers(answers)
                .build();
    }

    private Map<UUID, BigDecimal> computeCategoryRawScores(
            SurveySnapshotContext snapshotContext,
            List<ValidatedAnswer> answers) {
        Map<UUID, BigDecimal> categoryRawScores = new LinkedHashMap<>();
        for (ValidatedAnswer answer : answers) {
            SnapshotQuestion snapshotQuestion = snapshotContext.questionsById().get(answer.questionId());
            if (snapshotQuestion == null || snapshotQuestion.categoryId() == null) {
                continue;
            }
            BigDecimal answerScore = answer.score() != null ? answer.score() : BigDecimal.ZERO;
            categoryRawScores.merge(snapshotQuestion.categoryId(), answerScore, BigDecimal::add);
        }
        return categoryRawScores;
    }

    private record SurveySnapshotContext(Map<UUID, SnapshotQuestion> questionsById) {
    }

    private record SnapshotQuestion(
            UUID questionId,
            UUID questionVersionId,
            UUID categoryId,
            boolean mandatory,
            String optionConfig,
            String answerConfig) {
    }

    private record ValidatedAnswer(UUID questionId, UUID questionVersionId, String value, BigDecimal score) {
    }

    private record AnswerPayload(String normalizedValue, BigDecimal score) {
    }

    private record OptionSpec(String value, BigDecimal score) {
    }
}
