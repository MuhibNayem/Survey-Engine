package com.bracits.surveyengine.survey.repository;

import com.bracits.surveyengine.survey.entity.Survey;
import com.bracits.surveyengine.survey.entity.SurveyLifecycleState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SurveyRepository extends JpaRepository<Survey, UUID> {
    List<Survey> findByActiveTrue();

    List<Survey> findByLifecycleStateAndActiveTrue(SurveyLifecycleState state);
}
