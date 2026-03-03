package com.bracits.surveyengine.questionbank.repository;

import com.bracits.surveyengine.questionbank.entity.QuestionVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface QuestionVersionRepository extends JpaRepository<QuestionVersion, UUID> {

    List<QuestionVersion> findByQuestionIdOrderByVersionNumberDesc(UUID questionId);

    Optional<QuestionVersion> findByQuestionIdAndVersionNumber(UUID questionId, Integer versionNumber);

    Optional<QuestionVersion> findTopByQuestionIdOrderByVersionNumberDesc(UUID questionId);
}
