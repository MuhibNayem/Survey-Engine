package com.bracits.surveyengine.questionbank.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryQuestionMappingRequest {

    @NotNull(message = "Question ID is required")
    private UUID questionId;

    @NotNull(message = "Sort order is required")
    private Integer sortOrder;

    private BigDecimal weight;
}
