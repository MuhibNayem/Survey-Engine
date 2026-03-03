package com.bracits.surveyengine.scoring.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeightProfileResponse {
    private UUID id;
    private String name;
    private UUID campaignId;
    private boolean active;
    private List<CategoryWeightResponse> categoryWeights;
    private String createdBy;
    private Instant createdAt;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryWeightResponse {
        private UUID id;
        private UUID categoryId;
        private BigDecimal weightPercentage;
    }
}
