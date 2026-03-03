package com.bracits.surveyengine.response.service;

import com.bracits.surveyengine.response.dto.CampaignAnalytics;

import java.util.UUID;

/**
 * Service contract for campaign analytics.
 * SRS §4.7
 */
public interface AnalyticsService {

    CampaignAnalytics getAnalytics(UUID campaignId);
}
