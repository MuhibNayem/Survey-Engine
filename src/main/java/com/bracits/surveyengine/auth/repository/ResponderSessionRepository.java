package com.bracits.surveyengine.auth.repository;

import com.bracits.surveyengine.auth.entity.ResponderSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ResponderSessionRepository extends JpaRepository<ResponderSession, UUID> {

    Optional<ResponderSession> findBySessionHash(String sessionHash);

    void deleteByExpiresAtBefore(Instant threshold);
}
