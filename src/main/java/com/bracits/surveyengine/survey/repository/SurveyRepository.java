package com.bracits.surveyengine.survey.repository;

import com.bracits.surveyengine.survey.entity.Survey;
import com.bracits.surveyengine.survey.entity.SurveyLifecycleState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface SurveyRepository extends JpaRepository<Survey, UUID> {
    Page<Survey> findByActiveTrueAndTenantId(String tenantId, Pageable pageable);

    List<Survey> findByLifecycleStateAndActiveTrueAndTenantId(SurveyLifecycleState state, String tenantId);

    Optional<Survey> findByIdAndTenantId(UUID id, String tenantId);
}
