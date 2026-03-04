package com.bracits.surveyengine.survey.service;

import com.bracits.surveyengine.common.audit.AuditLogService;
import com.bracits.surveyengine.common.exception.BusinessException;
import com.bracits.surveyengine.common.exception.ErrorCode;
import com.bracits.surveyengine.common.exception.ResourceNotFoundException;
import com.bracits.surveyengine.common.tenant.TenantSupport;
import com.bracits.surveyengine.questionbank.entity.Category;
import com.bracits.surveyengine.questionbank.entity.CategoryQuestionMapping;
import com.bracits.surveyengine.questionbank.entity.CategoryVersion;
import com.bracits.surveyengine.questionbank.entity.Question;
import com.bracits.surveyengine.questionbank.entity.QuestionVersion;
import com.bracits.surveyengine.questionbank.repository.CategoryRepository;
import com.bracits.surveyengine.questionbank.repository.CategoryVersionRepository;
import com.bracits.surveyengine.questionbank.repository.QuestionRepository;
import com.bracits.surveyengine.questionbank.repository.QuestionVersionRepository;
import com.bracits.surveyengine.questionbank.service.QuestionService;
import com.bracits.surveyengine.survey.dto.LifecycleTransitionRequest;
import com.bracits.surveyengine.survey.dto.SurveyPageRequest;
import com.bracits.surveyengine.survey.dto.SurveyQuestionRequest;
import com.bracits.surveyengine.survey.dto.SurveyRequest;
import com.bracits.surveyengine.survey.dto.SurveyResponse;
import com.bracits.surveyengine.survey.entity.Survey;
import com.bracits.surveyengine.survey.entity.SurveyLifecycleState;
import com.bracits.surveyengine.survey.entity.SurveyPage;
import com.bracits.surveyengine.survey.entity.SurveyQuestion;
import com.bracits.surveyengine.survey.entity.SurveySnapshot;
import com.bracits.surveyengine.survey.repository.SurveyRepository;
import com.bracits.surveyengine.survey.repository.SurveySnapshotRepository;
import com.bracits.surveyengine.tenant.service.TenantService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Draft surveys carry pinned question/category versions from creation time.
 * Bank updates never mutate those pinned copies.
 */
@Service
@RequiredArgsConstructor
public class SurveyServiceImpl implements SurveyService {

    private static final int LIVE_VERSION_NUMBER = 1;
    private static final BigDecimal DEFAULT_CATEGORY_WEIGHT = BigDecimal.ONE;

    private static final Map<SurveyLifecycleState, Set<SurveyLifecycleState>> VALID_TRANSITIONS;

    static {
        VALID_TRANSITIONS = new EnumMap<>(SurveyLifecycleState.class);
        VALID_TRANSITIONS.put(SurveyLifecycleState.DRAFT,
                Set.of(SurveyLifecycleState.PUBLISHED));
        VALID_TRANSITIONS.put(SurveyLifecycleState.PUBLISHED,
                Set.of(SurveyLifecycleState.CLOSED));
        VALID_TRANSITIONS.put(SurveyLifecycleState.CLOSED,
                Set.of(SurveyLifecycleState.RESULTS_PUBLISHED, SurveyLifecycleState.PUBLISHED));
        VALID_TRANSITIONS.put(SurveyLifecycleState.RESULTS_PUBLISHED,
                Set.of(SurveyLifecycleState.ARCHIVED));
        VALID_TRANSITIONS.put(SurveyLifecycleState.ARCHIVED, Set.of());
    }

    private final SurveyRepository surveyRepository;
    private final SurveySnapshotRepository snapshotRepository;
    private final QuestionService questionService;
    private final QuestionRepository questionRepository;
    private final QuestionVersionRepository questionVersionRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryVersionRepository categoryVersionRepository;
    private final AuditLogService auditLogService;
    private final TenantService tenantService;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public SurveyResponse create(SurveyRequest request) {
        String tenantId = TenantSupport.currentTenantOrDefault();
        tenantService.ensureProvisioned(tenantId);

        Survey survey = Survey.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .tenantId(tenantId)
                .build();

        rebuildDraftStructure(survey, request.getPages());
        survey = surveyRepository.save(survey);
        return toResponse(survey);
    }

    @Override
    @Transactional(readOnly = true)
    public SurveyResponse getById(UUID id) {
        return toResponse(findOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SurveyResponse> getAllActive() {
        return surveyRepository.findByActiveTrueAndTenantId(TenantSupport.currentTenantOrDefault()).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public SurveyResponse update(UUID id, SurveyRequest request) {
        Survey survey = findOrThrow(id);
        enforceModifiable(survey);

        survey.setTitle(request.getTitle());
        survey.setDescription(request.getDescription());
        rebuildDraftStructure(survey, request.getPages());

        survey = surveyRepository.save(survey);
        return toResponse(survey);
    }

    @Override
    @Transactional
    public void deactivate(UUID id) {
        Survey survey = findOrThrow(id);
        survey.setActive(false);
        surveyRepository.save(survey);
    }

    @Override
    @Transactional
    public SurveyResponse transitionLifecycle(UUID id, LifecycleTransitionRequest request) {
        Survey survey = findOrThrow(id);
        SurveyLifecycleState targetState = SurveyLifecycleState.valueOf(request.getTargetState());
        SurveyLifecycleState currentState = survey.getLifecycleState();

        Set<SurveyLifecycleState> allowed = VALID_TRANSITIONS.getOrDefault(currentState, Set.of());
        if (!allowed.contains(targetState)) {
            throw new BusinessException(ErrorCode.INVALID_LIFECYCLE_TRANSITION,
                    "Cannot transition from %s to %s".formatted(currentState, targetState));
        }

        if (currentState == SurveyLifecycleState.CLOSED
                && targetState == SurveyLifecycleState.PUBLISHED) {
            if (request.getReason() == null || request.getReason().isBlank()) {
                throw new BusinessException(ErrorCode.INVALID_LIFECYCLE_TRANSITION,
                        "Reason is required when reopening a closed survey");
            }
            auditLogService.record("Survey", id.toString(), "REOPEN",
                    "system", request.getReason());
        }

        String previousState = currentState.name();
        survey.setLifecycleState(targetState);

        if (targetState == SurveyLifecycleState.PUBLISHED
                && currentState == SurveyLifecycleState.DRAFT) {
            createSnapshot(survey);
        }

        survey = surveyRepository.save(survey);

        auditLogService.record("Survey", id.toString(), "LIFECYCLE_TRANSITION",
                "system", null, previousState, targetState.name());

        return toResponse(survey);
    }

    @Override
    @Transactional(readOnly = true)
    public SurveySnapshot getLatestSnapshot(UUID surveyId) {
        findOrThrow(surveyId);
        return snapshotRepository
                .findTopBySurveyIdOrderByVersionNumberDesc(surveyId)
                .orElseThrow(() -> new ResourceNotFoundException("SurveySnapshot", surveyId));
    }

    private void enforceModifiable(Survey survey) {
        if (survey.getLifecycleState() != SurveyLifecycleState.DRAFT) {
            throw new BusinessException(ErrorCode.SURVEY_IMMUTABLE_AFTER_PUBLISH,
                    "Survey structure cannot be modified after publishing. Current state: "
                            + survey.getLifecycleState());
        }
    }

    private void rebuildDraftStructure(Survey survey, List<SurveyPageRequest> pageRequests) {
        survey.getPages().clear();
        if (pageRequests == null || pageRequests.isEmpty()) {
            return;
        }

        List<PendingSurveyQuestion> pendingQuestions = new ArrayList<>();
        String tenantId = TenantSupport.currentTenantOrDefault();

        for (SurveyPageRequest pageReq : pageRequests) {
            SurveyPage page = SurveyPage.builder()
                    .survey(survey)
                    .title(pageReq.getTitle())
                    .sortOrder(pageReq.getSortOrder())
                    .build();
            survey.getPages().add(page);

            if (pageReq.getQuestions() == null) {
                continue;
            }

            for (SurveyQuestionRequest questionReq : pageReq.getQuestions()) {
                Question question = questionRepository
                        .findByIdAndTenantId(questionReq.getQuestionId(), tenantId)
                        .orElseThrow(() -> new ResourceNotFoundException("Question", questionReq.getQuestionId()));

                QuestionVersion pinnedQuestionVersion = questionService.createPinnedVersionSnapshot(question);
                pendingQuestions.add(new PendingSurveyQuestion(
                        page,
                        questionReq,
                        pinnedQuestionVersion));
            }
        }

        Map<UUID, UUID> pinnedCategoryVersionIds = createPinnedCategoryVersions(pendingQuestions, tenantId);

        for (PendingSurveyQuestion pending : pendingQuestions) {
            UUID categoryId = pending.request().getCategoryId();
            UUID categoryVersionId = categoryId != null ? pinnedCategoryVersionIds.get(categoryId) : null;

            SurveyQuestion surveyQuestion = SurveyQuestion.builder()
                    .page(pending.page())
                    .questionId(pending.request().getQuestionId())
                    .questionVersionId(pending.pinnedQuestionVersion().getId())
                    .categoryId(categoryId)
                    .categoryVersionId(categoryVersionId)
                    .sortOrder(pending.request().getSortOrder())
                    .mandatory(pending.request().isMandatory())
                    .answerConfig(normalizeAnswerConfig(pending.request().getAnswerConfig()))
                    .build();
            pending.page().getQuestions().add(surveyQuestion);
        }
    }

    private Map<UUID, UUID> createPinnedCategoryVersions(List<PendingSurveyQuestion> pendingQuestions, String tenantId) {
        Map<UUID, List<PendingSurveyQuestion>> groupedByCategory = new LinkedHashMap<>();
        for (PendingSurveyQuestion pending : pendingQuestions) {
            UUID categoryId = pending.request().getCategoryId();
            if (categoryId == null) {
                continue;
            }
            groupedByCategory.computeIfAbsent(categoryId, ignored -> new ArrayList<>()).add(pending);
        }

        Map<UUID, UUID> pinnedVersionByCategoryId = new LinkedHashMap<>();

        for (Map.Entry<UUID, List<PendingSurveyQuestion>> entry : groupedByCategory.entrySet()) {
            UUID categoryId = entry.getKey();
            List<PendingSurveyQuestion> categoryQuestions = entry.getValue();

            Category category = categoryRepository.findByIdAndTenantId(categoryId, tenantId)
                    .orElseThrow(() -> new ResourceNotFoundException("Category", categoryId));

            CategoryVersion liveVersion = categoryVersionRepository
                    .findByCategoryIdAndVersionNumber(categoryId, LIVE_VERSION_NUMBER)
                    .orElse(null);

            Map<UUID, BigDecimal> weightByQuestionId = new LinkedHashMap<>();
            if (liveVersion != null && liveVersion.getQuestionMappings() != null) {
                for (CategoryQuestionMapping mapping : liveVersion.getQuestionMappings()) {
                    weightByQuestionId.put(mapping.getQuestionId(),
                            mapping.getWeight() != null ? mapping.getWeight() : DEFAULT_CATEGORY_WEIGHT);
                }
            }

            int nextVersion = categoryVersionRepository
                    .findTopByCategoryIdOrderByVersionNumberDesc(categoryId)
                    .map(v -> v.getVersionNumber() + 1)
                    .orElse(LIVE_VERSION_NUMBER + 1);

            CategoryVersion pinnedVersion = CategoryVersion.builder()
                    .categoryId(categoryId)
                    .versionNumber(nextVersion)
                    .name(category.getName())
                    .description(category.getDescription())
                    .questionMappings(new ArrayList<>())
                    .build();

            int sortOrder = 1;
            Set<UUID> seenQuestionIds = new LinkedHashSet<>();
            for (PendingSurveyQuestion pending : categoryQuestions) {
                UUID questionId = pending.request().getQuestionId();
                if (!seenQuestionIds.add(questionId)) {
                    continue;
                }
                BigDecimal weight = weightByQuestionId.getOrDefault(questionId, DEFAULT_CATEGORY_WEIGHT);

                CategoryQuestionMapping mapping = CategoryQuestionMapping.builder()
                        .categoryVersion(pinnedVersion)
                        .questionId(questionId)
                        .questionVersionId(pending.pinnedQuestionVersion().getId())
                        .sortOrder(sortOrder++)
                        .weight(weight)
                        .build();
                pinnedVersion.getQuestionMappings().add(mapping);
            }

            pinnedVersion = categoryVersionRepository.save(pinnedVersion);
            pinnedVersionByCategoryId.put(categoryId, pinnedVersion.getId());
        }

        return pinnedVersionByCategoryId;
    }

    private void createSnapshot(Survey survey) {
        String snapshotJson;
        try {
            Map<String, Object> root = new LinkedHashMap<>();
            root.put("title", survey.getTitle());
            root.put("description", survey.getDescription() != null ? survey.getDescription() : "");
            List<Map<String, Object>> pages = new ArrayList<>();

            for (SurveyPage page : survey.getPages()) {
                Map<String, Object> pageJson = new LinkedHashMap<>();
                pageJson.put("title", page.getTitle() != null ? page.getTitle() : "");
                pageJson.put("sortOrder", page.getSortOrder());
                List<Map<String, Object>> questions = new ArrayList<>();

                for (SurveyQuestion sq : page.getQuestions()) {
                    QuestionVersion questionVersion = questionVersionRepository.findById(sq.getQuestionVersionId())
                            .orElseThrow(() -> new ResourceNotFoundException("QuestionVersion", sq.getQuestionVersionId()));

                    Map<String, Object> questionJson = new LinkedHashMap<>();
                    questionJson.put("questionId", sq.getQuestionId().toString());
                    questionJson.put("questionVersionId", sq.getQuestionVersionId().toString());
                    questionJson.put("text", questionVersion.getText());
                    questionJson.put("type", questionVersion.getType().name());
                    questionJson.put("maxScore", questionVersion.getMaxScore());

                    if (questionVersion.getOptionConfig() != null && !questionVersion.getOptionConfig().isBlank()) {
                        questionJson.put("optionConfig", objectMapper.readTree(questionVersion.getOptionConfig()));
                    }
                    if (sq.getCategoryId() != null) {
                        questionJson.put("categoryId", sq.getCategoryId().toString());
                    }
                    if (sq.getCategoryVersionId() != null) {
                        questionJson.put("categoryVersionId", sq.getCategoryVersionId().toString());
                    }
                    questionJson.put("sortOrder", sq.getSortOrder());
                    questionJson.put("mandatory", sq.isMandatory());
                    if (sq.getAnswerConfig() != null && !sq.getAnswerConfig().isBlank()) {
                        questionJson.put("answerConfig", objectMapper.readTree(sq.getAnswerConfig()));
                    }
                    questions.add(questionJson);
                }
                pageJson.put("questions", questions);
                pages.add(pageJson);
            }
            root.put("pages", pages);
            snapshotJson = objectMapper.writeValueAsString(root);
        } catch (Exception ex) {
            throw new BusinessException(ErrorCode.VALIDATION_FAILED,
                    "Survey snapshot generation failed due to invalid pinned configuration");
        }

        SurveySnapshot snapshot = SurveySnapshot.builder()
                .surveyId(survey.getId())
                .versionNumber(survey.getCurrentVersion())
                .snapshotData(snapshotJson)
                .publishedBy("system")
                .build();

        snapshotRepository.save(snapshot);
        survey.setCurrentVersion(survey.getCurrentVersion() + 1);
    }

    private String normalizeAnswerConfig(String rawAnswerConfig) {
        if (rawAnswerConfig == null || rawAnswerConfig.isBlank()) {
            return null;
        }
        try {
            JsonNode node = objectMapper.readTree(rawAnswerConfig);
            if (!node.isObject()) {
                throw new BusinessException(ErrorCode.VALIDATION_FAILED,
                        "answerConfig must be a JSON object");
            }
            return objectMapper.writeValueAsString(node);
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new BusinessException(ErrorCode.VALIDATION_FAILED,
                    "answerConfig must be valid JSON");
        }
    }

    private Survey findOrThrow(UUID id) {
        return surveyRepository.findByIdAndTenantId(id, TenantSupport.currentTenantOrDefault())
                .orElseThrow(() -> new ResourceNotFoundException("Survey", id));
    }

    private SurveyResponse toResponse(Survey survey) {
        List<SurveyResponse.PageResponse> pageResponses = survey.getPages().stream()
                .map(page -> SurveyResponse.PageResponse.builder()
                        .id(page.getId())
                        .title(page.getTitle())
                        .sortOrder(page.getSortOrder())
                        .questions(page.getQuestions().stream()
                                .map(sq -> SurveyResponse.QuestionResponse.builder()
                                        .id(sq.getId())
                                        .questionId(sq.getQuestionId())
                                        .questionVersionId(sq.getQuestionVersionId())
                                        .categoryId(sq.getCategoryId())
                                        .categoryVersionId(sq.getCategoryVersionId())
                                        .sortOrder(sq.getSortOrder())
                                        .mandatory(sq.isMandatory())
                                        .answerConfig(sq.getAnswerConfig())
                                        .build())
                                .toList())
                        .build())
                .toList();

        return SurveyResponse.builder()
                .id(survey.getId())
                .title(survey.getTitle())
                .description(survey.getDescription())
                .lifecycleState(survey.getLifecycleState())
                .currentVersion(survey.getCurrentVersion())
                .active(survey.isActive())
                .pages(pageResponses)
                .createdBy(survey.getCreatedBy())
                .createdAt(survey.getCreatedAt())
                .updatedBy(survey.getUpdatedBy())
                .updatedAt(survey.getUpdatedAt())
                .build();
    }

    private record PendingSurveyQuestion(
            SurveyPage page,
            SurveyQuestionRequest request,
            QuestionVersion pinnedQuestionVersion) {
    }
}
