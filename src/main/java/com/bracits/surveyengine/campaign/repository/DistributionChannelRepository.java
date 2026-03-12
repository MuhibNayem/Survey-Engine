package com.bracits.surveyengine.campaign.repository;

import com.bracits.surveyengine.campaign.entity.DistributionChannel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DistributionChannelRepository extends JpaRepository<DistributionChannel, UUID> {
    List<DistributionChannel> findByCampaignId(UUID campaignId);
    void deleteByCampaignId(UUID campaignId);
}
