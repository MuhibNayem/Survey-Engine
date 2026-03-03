package com.bracits.surveyengine.campaign.repository;

import com.bracits.surveyengine.campaign.entity.Campaign;
import com.bracits.surveyengine.campaign.entity.CampaignStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CampaignRepository extends JpaRepository<Campaign, UUID> {
    List<Campaign> findByActiveTrue();

    List<Campaign> findBySurveyIdAndActiveTrue(UUID surveyId);

    List<Campaign> findByStatusAndActiveTrue(CampaignStatus status);
}
