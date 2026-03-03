package com.bracits.surveyengine.survey.service;

import com.bracits.surveyengine.common.audit.AuditLogService;
import com.bracits.surveyengine.common.exception.BusinessException;
import com.bracits.surveyengine.common.exception.ErrorCode;
import com.bracits.surveyengine.common.exception.ResourceNotFoundException;
import com.bracits.surveyengine.questionbank.service.QuestionService;
import com.bracits.surveyengine.survey.dto.*;
import com.bracits.surveyengine.survey.entity.*;
import com.bracits.surveyengine.survey.repository.SurveyRepository;
import com.bracits.surveyengine.survey.repository.SurveySnapshotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Implementation of {@link SurveyService}.
 * <p>
 * Key business rules (SRS §4.6):
 * - Lifecycle: Draft → Published → Closed → Results_Published → Archived
 * - Reopen (Closed→Published) requires reason + audit log
 * - Structure modification rejected after Published
 * - Publishing creates an immutable snapshot
 */
@Service
@RequiredArgsConstructor
public class SurveyServiceImpl implements SurveyService {

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
    private final AuditLogService auditLogService;

    @Override
    @Transactional
    public SurveyResponse create(SurveyRequest request) {
        Survey survey = Survey.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .build();

        if (request.getPages() != null) {
            for (SurveyPageRequest pageReq : request.getPages()) {
                SurveyPage page = SurveyPage.builder()
                        .survey(survey)
                        .title(pageReq.getTitle())
                        .sortOrder(pageReq.getSortOrder())
                        .build();

                if (pageReq.getQuestions() != null) {
                    for (SurveyQuestionRequest qReq : pageReq.getQuestions()) {
                        var qv = questionService.getLatestVersion(qReq.getQuestionId());
                        SurveyQuestion sq = SurveyQuestion.builder()
                                .page(page)
                                .questionId(qReq.getQuestionId())
                                .questionVersionId(qv.getId())
                                .categoryId(qReq.getCategoryId())
                                .sortOrder(qReq.getSortOrder())
                                .mandatory(qReq.isMandatory())
                                .answerConfig(qReq.getAnswerConfig())
                                .build();
                        page.getQuestions().add(sq);
                    }
                }
                survey.getPages().add(page);
            }
        }

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
        return surveyRepository.findByActiveTrue().stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * SRS §4.6: Reject structure changes after Published.
     */
    @Override
    @Transactional
    public SurveyResponse update(UUID id, SurveyRequest request) {
        Survey survey = findOrThrow(id);
        enforceModifiable(survey);

        survey.setTitle(request.getTitle());
        survey.setDescription(request.getDescription());

        // Replace pages entirely (cascade removes old ones)
        survey.getPages().clear();
        if (request.getPages() != null) {
            for (SurveyPageRequest pageReq : request.getPages()) {
                SurveyPage page = SurveyPage.builder()
                        .survey(survey)
                        .title(pageReq.getTitle())
                        .sortOrder(pageReq.getSortOrder())
                        .build();

                if (pageReq.getQuestions() != null) {
                    for (SurveyQuestionRequest qReq : pageReq.getQuestions()) {
                        var qv = questionService.getLatestVersion(qReq.getQuestionId());
                        SurveyQuestion sq = SurveyQuestion.builder()
                                .page(page)
                                .questionId(qReq.getQuestionId())
                                .questionVersionId(qv.getId())
                                .categoryId(qReq.getCategoryId())
                                .sortOrder(qReq.getSortOrder())
                                .mandatory(qReq.isMandatory())
                                .answerConfig(qReq.getAnswerConfig())
                                .build();
                        page.getQuestions().add(sq);
                    }
                }
                survey.getPages().add(page);
            }
        }

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

    /**
     * SRS §4.6 lifecycle state machine.
     * Reopen (Closed→Published) requires reason and creates audit entry.
     * Publishing (Draft→Published) creates an immutable snapshot.
     */
    @Override
    @Transactional
    public SurveyResponse transitionLifecycle(UUID id, LifecycleTransitionRequest request) {
        Survey survey = findOrThrow(id);
        SurveyLifecycleState targetState = SurveyLifecycleState.valueOf(request.getTargetState());
        SurveyLifecycleState currentState = survey.getLifecycleState();

        // Validate transition
        Set<SurveyLifecycleState> allowed = VALID_TRANSITIONS.getOrDefault(currentState, Set.of());
        if (!allowed.contains(targetState)) {
            throw new BusinessException(ErrorCode.INVALID_LIFECYCLE_TRANSITION,
                    "Cannot transition from %s to %s".formatted(currentState, targetState));
        }

        // Reopen requires reason (SRS §4.6)
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

        // On publish: create immutable snapshot (SRS §4.2, §4.6)
        if (targetState == SurveyLifecycleState.PUBLISHED
                && currentState == SurveyLifecycleState.DRAFT) {
            createSnapshot(survey);
        }

        survey = surveyRepository.save(survey);

        // Audit the transition
        auditLogService.record("Survey", id.toString(), "LIFECYCLE_TRANSITION",
                "system", null, previousState, targetState.name());

        return toResponse(survey);
    }

    @Override
    @Transactional(readOnly = true)
    public SurveySnapshot getLatestSnapshot(UUID surveyId) {
        return snapshotRepository
                .findTopBySurveyIdOrderByVersionNumberDesc(surveyId)
                .orElseThrow(() -> new ResourceNotFoundException("SurveySnapshot", surveyId));
    }

    // ---- Internal helpers ----

    private void enforceModifiable(Survey survey) {
        if (survey.getLifecycleState() != SurveyLifecycleState.DRAFT) {
            throw new BusinessException(ErrorCode.SURVEY_IMMUTABLE_AFTER_PUBLISH,
                    "Survey structure cannot be modified after publishing. Current state: "
                            + survey.getLifecycleState());
        }
    }

    private void createSnapshot(Survey survey) {
        // Build a JSON representation of the survey structure
        StringBuilder json = new StringBuilder();
        json.append("{\"title\":\"").append(escapeJson(survey.getTitle())).append("\"");
        json.append(",\"description\":\"").append(escapeJson(
                survey.getDescription() != null ? survey.getDescription() : "")).append("\"");
        json.append(",\"pages\":[");

        for (int i = 0; i < survey.getPages().size(); i++) {
            SurveyPage page = survey.getPages().get(i);
            if (i > 0)
                json.append(",");
            json.append("{\"title\":\"").append(escapeJson(
                    page.getTitle() != null ? page.getTitle() : "")).append("\"");
            json.append(",\"sortOrder\":").append(page.getSortOrder());
            json.append(",\"questions\":[");

            for (int j = 0; j < page.getQuestions().size(); j++) {
                SurveyQuestion sq = page.getQuestions().get(j);
                if (j > 0)
                    json.append(",");
                json.append("{\"questionId\":\"").append(sq.getQuestionId()).append("\"");
                json.append(",\"questionVersionId\":\"").append(sq.getQuestionVersionId()).append("\"");
                if (sq.getCategoryId() != null) {
                    json.append(",\"categoryId\":\"").append(sq.getCategoryId()).append("\"");
                }
                json.append(",\"sortOrder\":").append(sq.getSortOrder());
                json.append(",\"mandatory\":").append(sq.isMandatory());
                json.append("}");
            }
            json.append("]}");
        }
        json.append("]}");

        SurveySnapshot snapshot = SurveySnapshot.builder()
                .surveyId(survey.getId())
                .versionNumber(survey.getCurrentVersion())
                .snapshotData(json.toString())
                .publishedBy("system")
                .build();

        snapshotRepository.save(snapshot);
        survey.setCurrentVersion(survey.getCurrentVersion() + 1);
    }

    private String escapeJson(String value) {
        if (value == null)
            return "";
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private Survey findOrThrow(UUID id) {
        return surveyRepository.findById(id)
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
}
