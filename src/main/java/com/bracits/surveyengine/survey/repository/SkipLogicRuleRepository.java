package com.bracits.surveyengine.survey.repository;

import com.bracits.surveyengine.survey.entity.SkipLogicRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SkipLogicRuleRepository extends JpaRepository<SkipLogicRule, UUID> {
    List<SkipLogicRule> findBySurveyIdOrderBySortOrderAsc(UUID surveyId);

    void deleteBySurveyId(UUID surveyId);
}
