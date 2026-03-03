package com.bracits.surveyengine.campaign.service;

import com.bracits.surveyengine.campaign.dto.DistributionChannelResponse;
import com.bracits.surveyengine.campaign.entity.DistributionChannel;
import com.bracits.surveyengine.campaign.entity.DistributionChannelType;
import com.bracits.surveyengine.campaign.repository.CampaignRepository;
import com.bracits.surveyengine.campaign.repository.DistributionChannelRepository;
import com.bracits.surveyengine.common.exception.ResourceNotFoundException;
import com.bracits.surveyengine.common.tenant.TenantSupport;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Implementation of {@link DistributionService}.
 * <p>
 * SRS §4.8: Generates public link, private link, HTML embed, WordPress embed,
 * JS embed code, and email distribution trigger.
 */
@Service
@RequiredArgsConstructor
public class DistributionServiceImpl implements DistributionService {

    private static final String BASE_URL = "https://survey.example.com";

    private final DistributionChannelRepository channelRepository;
    private final CampaignRepository campaignRepository;

    @Override
    @Transactional
    public List<DistributionChannelResponse> generateChannels(UUID campaignId) {
        campaignRepository.findByIdAndTenantId(campaignId, TenantSupport.currentTenantOrDefault())
                .orElseThrow(() -> new ResourceNotFoundException("Campaign", campaignId));

        List<DistributionChannel> channels = new ArrayList<>();
        String surveyPath = BASE_URL + "/s/" + campaignId;

        // Public link
        channels.add(buildChannel(campaignId, DistributionChannelType.PUBLIC_LINK,
                surveyPath));

        // Private link (with token)
        channels.add(buildChannel(campaignId, DistributionChannelType.PRIVATE_LINK,
                surveyPath + "?token=" + UUID.randomUUID()));

        // HTML embed
        channels.add(buildChannel(campaignId, DistributionChannelType.HTML_EMBED,
                "<iframe src=\"%s\" width=\"100%%\" height=\"600\" frameborder=\"0\"></iframe>"
                        .formatted(surveyPath)));

        // WordPress embed
        channels.add(buildChannel(campaignId, DistributionChannelType.WORDPRESS_EMBED,
                "[survey_engine id=\"%s\"]".formatted(campaignId)));

        // JS embed
        channels.add(buildChannel(campaignId, DistributionChannelType.JS_EMBED,
                "<script src=\"%s/embed.js\" data-campaign=\"%s\"></script>"
                        .formatted(BASE_URL, campaignId)));

        // Email distribution
        channels.add(buildChannel(campaignId, DistributionChannelType.EMAIL,
                surveyPath + "?source=email&respondent={{respondent_id}}"));

        List<DistributionChannel> saved = channelRepository.saveAll(channels);
        return saved.stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DistributionChannelResponse> getChannels(UUID campaignId) {
        campaignRepository.findByIdAndTenantId(campaignId, TenantSupport.currentTenantOrDefault())
                .orElseThrow(() -> new ResourceNotFoundException("Campaign", campaignId));
        return channelRepository.findByCampaignId(campaignId).stream()
                .map(this::toResponse)
                .toList();
    }

    private DistributionChannel buildChannel(UUID campaignId,
            DistributionChannelType type, String value) {
        return DistributionChannel.builder()
                .campaignId(campaignId)
                .channelType(type)
                .channelValue(value)
                .build();
    }

    private DistributionChannelResponse toResponse(DistributionChannel dc) {
        return DistributionChannelResponse.builder()
                .id(dc.getId())
                .campaignId(dc.getCampaignId())
                .channelType(dc.getChannelType())
                .channelValue(dc.getChannelValue())
                .createdAt(dc.getCreatedAt())
                .build();
    }
}
