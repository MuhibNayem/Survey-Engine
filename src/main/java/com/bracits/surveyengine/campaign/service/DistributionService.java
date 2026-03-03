package com.bracits.surveyengine.campaign.service;

import com.bracits.surveyengine.campaign.dto.DistributionChannelResponse;

import java.util.List;
import java.util.UUID;

/**
 * Service contract for distribution channel generation.
 * SRS §4.8
 */
public interface DistributionService {

    List<DistributionChannelResponse> generateChannels(UUID campaignId);

    List<DistributionChannelResponse> getChannels(UUID campaignId);
}
