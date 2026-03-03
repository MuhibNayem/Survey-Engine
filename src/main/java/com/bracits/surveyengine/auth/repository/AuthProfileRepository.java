package com.bracits.surveyengine.auth.repository;

import com.bracits.surveyengine.auth.entity.AuthProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AuthProfileRepository extends JpaRepository<AuthProfile, UUID> {
    Optional<AuthProfile> findByCampaignId(UUID campaignId);
}
