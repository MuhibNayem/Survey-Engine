package com.bracits.surveyengine.campaign.repository;

import com.bracits.surveyengine.campaign.entity.DataCollectionField;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DataCollectionFieldRepository extends JpaRepository<DataCollectionField, UUID> {

    List<DataCollectionField> findByCampaignSettingsIdOrderBySortOrderAsc(UUID campaignSettingsId);

    void deleteByCampaignSettingsId(UUID campaignSettingsId);
}
