package com.bracits.surveyengine.auth.repository;

import com.bracits.surveyengine.auth.entity.AuthConfigAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AuthConfigAuditRepository extends JpaRepository<AuthConfigAudit, UUID> {
    List<AuthConfigAudit> findByAuthProfileIdOrderByChangedAtDesc(UUID authProfileId);
}
