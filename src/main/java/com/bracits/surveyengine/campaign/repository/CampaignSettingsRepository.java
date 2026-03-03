package com.bracits.surveyengine.campaign.repository;

import com.bracits.surveyengine.campaign.entity.CampaignSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CampaignSettingsRepository extends JpaRepository<CampaignSettings, UUID> {
    Optional<CampaignSettings> findByCampaignId(UUID campaignId);
}
