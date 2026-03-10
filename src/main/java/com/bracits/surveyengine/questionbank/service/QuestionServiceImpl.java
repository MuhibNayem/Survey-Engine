package com.bracits.surveyengine.questionbank.service;

import com.bracits.surveyengine.common.exception.BusinessException;
import com.bracits.surveyengine.common.exception.ErrorCode;
import com.bracits.surveyengine.common.exception.ResourceNotFoundException;
import com.bracits.surveyengine.common.tenant.TenantSupport;
import com.bracits.surveyengine.questionbank.dto.QuestionRequest;
import com.bracits.surveyengine.questionbank.dto.QuestionResponse;
import com.bracits.surveyengine.questionbank.entity.Question;
import com.bracits.surveyengine.questionbank.entity.QuestionType;
import com.bracits.surveyengine.questionbank.entity.QuestionVersion;
import com.bracits.surveyengine.questionbank.repository.QuestionRepository;
import com.bracits.surveyengine.questionbank.repository.QuestionVersionRepository;
import com.bracits.surveyengine.search.service.SearchCacheInvalidationService;
import com.bracits.surveyengine.tenant.service.TenantService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Bank questions stay mutable and keep a single live version (v1).
 * Survey creation pins immutable snapshots using dedicated version rows.
 */
@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {

    private static final int LIVE_VERSION_NUMBER = 1;

    private final QuestionRepository questionRepository;
    private final QuestionVersionRepository questionVersionRepository;
    private final TenantService tenantService;
    private final ObjectMapper objectMapper;
    private final SearchCacheInvalidationService searchCacheInvalidationService;

    @Override
    @Transactional
    @com.bracits.surveyengine.common.audit.annotation.Auditable(action = "QUESTION_CREATED")
    public QuestionResponse create(QuestionRequest request) {
        String tenantId = TenantSupport.currentTenantOrDefault();
        tenantService.ensureProvisioned(tenantId);

        Question question = Question.builder()
                .text(request.getText())
                .type(request.getType())
                .maxScore(request.getMaxScore())
                .optionConfig(normalizeOptionConfig(request.getOptionConfig(), request.getType()))
                .tenantId(tenantId)
                .currentVersion(LIVE_VERSION_NUMBER)
                .build();
        question = questionRepository.save(question);

        upsertLiveVersion(question);
        searchCacheInvalidationService.invalidateTenantAfterCommit(tenantId);

        return toResponse(question);
    }

    @Override
    @Transactional(readOnly = true)
    public QuestionResponse getById(UUID id) {
        return toResponse(findOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<QuestionResponse> getAllActive(Pageable pageable) {
        return questionRepository.findByActiveTrueAndTenantId(TenantSupport.currentTenantOrDefault(), pageable)
                .map(this::toResponse);
    }

    @Override
    @Transactional
    @com.bracits.surveyengine.common.audit.annotation.Auditable(action = "QUESTION_UPDATED")
    public QuestionResponse update(UUID id, QuestionRequest request) {
        Question question = findOrThrow(id);
        question.setText(request.getText());
        question.setType(request.getType());
        question.setMaxScore(request.getMaxScore());
        question.setOptionConfig(normalizeOptionConfig(request.getOptionConfig(), request.getType()));
        question.setCurrentVersion(LIVE_VERSION_NUMBER);
        question = questionRepository.save(question);

        upsertLiveVersion(question);
        searchCacheInvalidationService.invalidateTenantAfterCommit(question.getTenantId());

        return toResponse(question);
    }

    @Override
    @Transactional
    @com.bracits.surveyengine.common.audit.annotation.Auditable(action = "QUESTION_DELETED")
    public void deactivate(UUID id) {
        Question question = findOrThrow(id);
        question.setActive(false);
        questionRepository.save(question);
        searchCacheInvalidationService.invalidateTenantAfterCommit(question.getTenantId());
    }

    @Override
    @Transactional
    public QuestionVersion createPinnedVersionSnapshot(Question question) {
        int nextVersion = questionVersionRepository
                .findTopByQuestionIdOrderByVersionNumberDesc(question.getId())
                .map(v -> v.getVersionNumber() + 1)
                .orElse(LIVE_VERSION_NUMBER + 1);

        QuestionVersion version = QuestionVersion.builder()
                .questionId(question.getId())
                .versionNumber(nextVersion)
                .text(question.getText())
                .type(question.getType())
                .maxScore(question.getMaxScore())
                .optionConfig(question.getOptionConfig())
                .build();
        return questionVersionRepository.save(version);
    }

    @Override
    @Transactional(readOnly = true)
    public QuestionVersion getLiveVersion(UUID questionId) {
        findOrThrow(questionId);
        return questionVersionRepository
                .findByQuestionIdAndVersionNumber(questionId, LIVE_VERSION_NUMBER)
                .or(() -> questionVersionRepository.findTopByQuestionIdOrderByVersionNumberDesc(questionId))
                .orElseThrow(() -> new ResourceNotFoundException("QuestionVersion", questionId));
    }

    private void upsertLiveVersion(Question question) {
        QuestionVersion liveVersion = questionVersionRepository
                .findByQuestionIdAndVersionNumber(question.getId(), LIVE_VERSION_NUMBER)
                .orElse(null);

        if (liveVersion == null) {
            liveVersion = QuestionVersion.builder()
                    .questionId(question.getId())
                    .versionNumber(LIVE_VERSION_NUMBER)
                    .text(question.getText())
                    .type(question.getType())
                    .maxScore(question.getMaxScore())
                    .optionConfig(question.getOptionConfig())
                    .build();
        } else {
            liveVersion.setText(question.getText());
            liveVersion.setType(question.getType());
            liveVersion.setMaxScore(question.getMaxScore());
            liveVersion.setOptionConfig(question.getOptionConfig());
        }

        questionVersionRepository.save(liveVersion);
    }

    private String normalizeOptionConfig(String rawOptionConfig, QuestionType questionType) {
        if (rawOptionConfig == null || rawOptionConfig.isBlank()) {
            return null;
        }

        try {
            JsonNode node = objectMapper.readTree(rawOptionConfig);
            if (!node.isObject()) {
                throw new BusinessException(ErrorCode.VALIDATION_FAILED,
                        "optionConfig must be a JSON object");
            }

            boolean needsOptions = questionType == QuestionType.SINGLE_CHOICE
                    || questionType == QuestionType.MULTIPLE_CHOICE
                    || questionType == QuestionType.RANK;

            JsonNode optionsNode = node.get("options");
            if (needsOptions) {
                if (optionsNode == null || !optionsNode.isArray() || optionsNode.isEmpty()) {
                    throw new BusinessException(ErrorCode.VALIDATION_FAILED,
                            "optionConfig.options must be a non-empty array for " + questionType);
                }
                Set<String> unique = new LinkedHashSet<>();
                for (JsonNode optionNode : optionsNode) {
                    String optionValue = optionNode.isObject()
                            ? optionNode.path("value").asText(null)
                            : optionNode.asText(null);
                    if (optionValue == null || optionValue.isBlank()) {
                        throw new BusinessException(ErrorCode.VALIDATION_FAILED,
                                "Each option value must be non-empty");
                    }
                    if (!unique.add(optionValue)) {
                        throw new BusinessException(ErrorCode.VALIDATION_FAILED,
                                "Duplicate option value: " + optionValue);
                    }
                }
            }

            return objectMapper.writeValueAsString(node);
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new BusinessException(ErrorCode.VALIDATION_FAILED,
                    "optionConfig must be valid JSON");
        }
    }

    private Question findOrThrow(UUID id) {
        return questionRepository.findByIdAndTenantId(id, TenantSupport.currentTenantOrDefault())
                .orElseThrow(() -> new ResourceNotFoundException("Question", id));
    }

    private QuestionResponse toResponse(Question q) {
        return QuestionResponse.builder()
                .id(q.getId())
                .text(q.getText())
                .type(q.getType())
                .maxScore(q.getMaxScore())
                .optionConfig(q.getOptionConfig())
                .currentVersion(q.getCurrentVersion())
                .active(q.isActive())
                .createdBy(q.getCreatedBy())
                .createdAt(q.getCreatedAt())
                .updatedBy(q.getUpdatedBy())
                .updatedAt(q.getUpdatedAt())
                .build();
    }
}
