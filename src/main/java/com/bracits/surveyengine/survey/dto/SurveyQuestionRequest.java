package com.bracits.surveyengine.survey.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SurveyQuestionRequest {

    @NotNull(message = "Question ID is required")
    private UUID questionId;

    private UUID categoryId;

    private BigDecimal categoryWeightPercentage;

    @NotNull(message = "Sort order is required")
    private Integer sortOrder;

    private boolean mandatory;

    private String answerConfig;

    /**
     * Optional overrides for the survey-pinned question copy.
     * Applied only to draft survey copies and must never mutate question-bank records.
     */
    private String pinnedQuestionText;

    private BigDecimal pinnedQuestionMaxScore;

    private String pinnedQuestionOptionConfig;

    /**
     * Optional overrides for the survey-pinned category copy.
     * Applied only to draft survey copies and must never mutate category-bank records.
     */
    private String pinnedCategoryName;

    private String pinnedCategoryDescription;
}
