package com.bracits.surveyengine.response.repository;

import com.bracits.surveyengine.response.entity.ReopenAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReopenAuditRepository extends JpaRepository<ReopenAudit, UUID> {
    List<ReopenAudit> findBySurveyResponseId(UUID surveyResponseId);
}
