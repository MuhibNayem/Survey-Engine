package com.bracits.surveyengine.auth.repository;

import com.bracits.surveyengine.auth.entity.OidcAuthState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OidcAuthStateRepository extends JpaRepository<OidcAuthState, UUID> {
    Optional<OidcAuthState> findByState(String state);

    void deleteByExpiresAtBefore(Instant threshold);
}
