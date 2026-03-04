package com.bracits.surveyengine.campaign.service;

import com.bracits.surveyengine.campaign.dto.*;

import java.util.List;
import java.util.UUID;

/**
 * Service contract for campaign CRUD and settings management.
 * SRS §4.5, §4.6
 */
public interface CampaignService {

    CampaignResponse create(CampaignRequest request);

    CampaignResponse getById(UUID id);

    List<CampaignResponse> getAllActive();

    CampaignResponse update(UUID id, CampaignRequest request);

    void deactivate(UUID id);

    CampaignPreviewResponse getPreview(UUID campaignId);

    CampaignSettingsResponse getSettings(UUID campaignId);

    CampaignResponse updateSettings(UUID campaignId, CampaignSettingsRequest request);

    CampaignResponse activate(UUID id);
}
