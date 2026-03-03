package com.bracits.surveyengine.scoring.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryWeightRequest {

    @NotNull(message = "Category ID is required")
    private UUID categoryId;

    @NotNull(message = "Weight percentage is required")
    private BigDecimal weightPercentage;
}
