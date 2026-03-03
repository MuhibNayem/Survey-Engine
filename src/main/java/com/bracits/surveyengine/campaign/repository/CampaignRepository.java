package com.bracits.surveyengine.campaign.repository;

import com.bracits.surveyengine.campaign.entity.Campaign;
import com.bracits.surveyengine.campaign.entity.CampaignStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CampaignRepository extends JpaRepository<Campaign, UUID> {
    List<Campaign> findByActiveTrueAndTenantId(String tenantId);

    List<Campaign> findBySurveyIdAndActiveTrueAndTenantId(UUID surveyId, String tenantId);

    List<Campaign> findByStatusAndActiveTrueAndTenantId(CampaignStatus status, String tenantId);

    Optional<Campaign> findByIdAndTenantId(UUID id, String tenantId);

    long countByTenantIdAndActiveTrue(String tenantId);
}
