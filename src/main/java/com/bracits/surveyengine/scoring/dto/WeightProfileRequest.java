package com.bracits.surveyengine.scoring.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeightProfileRequest {

    @NotBlank(message = "Profile name is required")
    private String name;

    @NotNull(message = "Campaign ID is required")
    private UUID campaignId;

    @Valid
    private List<CategoryWeightRequest> categoryWeights;
}
