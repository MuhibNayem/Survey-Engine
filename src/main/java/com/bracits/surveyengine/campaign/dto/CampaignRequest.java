package com.bracits.surveyengine.campaign.dto;

import com.bracits.surveyengine.campaign.entity.AuthMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CampaignRequest {

    @NotBlank(message = "Campaign name is required")
    private String name;

    private String description;

    @NotNull(message = "Survey ID is required")
    private UUID surveyId;

    private AuthMode authMode;
}
