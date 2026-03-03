package com.bracits.surveyengine.auth.repository;

import com.bracits.surveyengine.auth.entity.AuthTokenReplay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.UUID;

@Repository
public interface AuthTokenReplayRepository extends JpaRepository<AuthTokenReplay, UUID> {
    boolean existsByTenantIdAndJti(String tenantId, String jti);

    void deleteByExpiresAtBefore(Instant threshold);
}
