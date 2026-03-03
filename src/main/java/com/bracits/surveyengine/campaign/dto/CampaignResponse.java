package com.bracits.surveyengine.campaign.dto;

import com.bracits.surveyengine.campaign.entity.AuthMode;
import com.bracits.surveyengine.campaign.entity.CampaignStatus;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CampaignResponse {
    private UUID id;
    private String name;
    private String description;
    private UUID surveyId;
    private UUID surveySnapshotId;
    private AuthMode authMode;
    private CampaignStatus status;
    private boolean active;
    private String createdBy;
    private Instant createdAt;
    private String updatedBy;
    private Instant updatedAt;
}
