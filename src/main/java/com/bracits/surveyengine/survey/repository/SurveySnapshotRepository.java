package com.bracits.surveyengine.survey.repository;

import com.bracits.surveyengine.survey.entity.SurveySnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SurveySnapshotRepository extends JpaRepository<SurveySnapshot, UUID> {
    Optional<SurveySnapshot> findTopBySurveyIdOrderByVersionNumberDesc(UUID surveyId);

    Optional<SurveySnapshot> findBySurveyIdAndVersionNumber(UUID surveyId, Integer versionNumber);
}
