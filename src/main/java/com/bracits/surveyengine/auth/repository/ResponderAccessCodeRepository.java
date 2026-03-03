package com.bracits.surveyengine.auth.repository;

import com.bracits.surveyengine.auth.entity.ResponderAccessCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ResponderAccessCodeRepository extends JpaRepository<ResponderAccessCode, UUID> {
    Optional<ResponderAccessCode> findByAccessCode(String accessCode);

    void deleteByExpiresAtBefore(Instant threshold);
}
