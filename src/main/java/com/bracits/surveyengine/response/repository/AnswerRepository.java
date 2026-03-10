package com.bracits.surveyengine.response.repository;

import com.bracits.surveyengine.response.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AnswerRepository extends JpaRepository<Answer, UUID> {

    void deleteBySurveyResponseId(UUID surveyResponseId);
}
