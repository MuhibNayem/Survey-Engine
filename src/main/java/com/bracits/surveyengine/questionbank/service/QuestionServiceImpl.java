package com.bracits.surveyengine.questionbank.service;

import com.bracits.surveyengine.common.exception.ResourceNotFoundException;
import com.bracits.surveyengine.questionbank.dto.QuestionRequest;
import com.bracits.surveyengine.questionbank.dto.QuestionResponse;
import com.bracits.surveyengine.questionbank.entity.Question;
import com.bracits.surveyengine.questionbank.entity.QuestionVersion;
import com.bracits.surveyengine.questionbank.repository.QuestionRepository;
import com.bracits.surveyengine.questionbank.repository.QuestionVersionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Implementation of {@link QuestionService}.
 * <p>
 * SRS §4.2: Editing a question after it has been versioned creates a new
 * version.
 * Existing versions are immutable.
 */
@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;
    private final QuestionVersionRepository questionVersionRepository;

    @Override
    @Transactional
    public QuestionResponse create(QuestionRequest request) {
        Question question = Question.builder()
                .text(request.getText())
                .type(request.getType())
                .maxScore(request.getMaxScore())
                .build();
        question = questionRepository.save(question);

        createVersionSnapshot(question);

        return toResponse(question);
    }

    @Override
    @Transactional(readOnly = true)
    public QuestionResponse getById(UUID id) {
        return toResponse(findOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuestionResponse> getAllActive() {
        return questionRepository.findByActiveTrue().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public QuestionResponse update(UUID id, QuestionRequest request) {
        Question question = findOrThrow(id);
        question.setText(request.getText());
        question.setType(request.getType());
        question.setMaxScore(request.getMaxScore());
        question.setCurrentVersion(question.getCurrentVersion() + 1);
        question = questionRepository.save(question);

        createVersionSnapshot(question);

        return toResponse(question);
    }

    @Override
    @Transactional
    public void deactivate(UUID id) {
        Question question = findOrThrow(id);
        question.setActive(false);
        questionRepository.save(question);
    }

    @Override
    public QuestionVersion createVersionSnapshot(Question question) {
        QuestionVersion version = QuestionVersion.builder()
                .questionId(question.getId())
                .versionNumber(question.getCurrentVersion())
                .text(question.getText())
                .type(question.getType())
                .maxScore(question.getMaxScore())
                .build();
        return questionVersionRepository.save(version);
    }

    @Override
    @Transactional(readOnly = true)
    public QuestionVersion getLatestVersion(UUID questionId) {
        return questionVersionRepository
                .findTopByQuestionIdOrderByVersionNumberDesc(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("QuestionVersion", questionId));
    }

    private Question findOrThrow(UUID id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question", id));
    }

    private QuestionResponse toResponse(Question q) {
        return QuestionResponse.builder()
                .id(q.getId())
                .text(q.getText())
                .type(q.getType())
                .maxScore(q.getMaxScore())
                .currentVersion(q.getCurrentVersion())
                .active(q.isActive())
                .createdBy(q.getCreatedBy())
                .createdAt(q.getCreatedAt())
                .updatedBy(q.getUpdatedBy())
                .updatedAt(q.getUpdatedAt())
                .build();
    }
}
