package com.bracits.surveyengine.questionbank.service;

import com.bracits.surveyengine.questionbank.dto.QuestionRequest;
import com.bracits.surveyengine.questionbank.dto.QuestionResponse;
import com.bracits.surveyengine.questionbank.entity.Question;
import com.bracits.surveyengine.questionbank.entity.QuestionVersion;

import java.util.List;
import java.util.UUID;

/**
 * Service contract for managing questions and their versions.
 * SRS §4.1, §4.2
 */
public interface QuestionService {

    QuestionResponse create(QuestionRequest request);

    QuestionResponse getById(UUID id);

    List<QuestionResponse> getAllActive();

    QuestionResponse update(UUID id, QuestionRequest request);

    void deactivate(UUID id);

    QuestionVersion createPinnedVersionSnapshot(Question question);

    QuestionVersion getLiveVersion(UUID questionId);
}
