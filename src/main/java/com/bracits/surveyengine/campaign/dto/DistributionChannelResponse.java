package com.bracits.surveyengine.campaign.dto;

import com.bracits.surveyengine.campaign.entity.DistributionChannelType;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DistributionChannelResponse {
    private UUID id;
    private UUID campaignId;
    private DistributionChannelType channelType;
    private String channelValue;
    private Instant createdAt;
}
