package com.bracits.surveyengine.scoring.repository;

import com.bracits.surveyengine.scoring.entity.WeightProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface WeightProfileRepository extends JpaRepository<WeightProfile, UUID> {
    List<WeightProfile> findByCampaignIdAndActiveTrue(UUID campaignId);
}
