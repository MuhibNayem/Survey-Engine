package com.bracits.surveyengine.survey.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

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

    @NotNull(message = "Sort order is required")
    private Integer sortOrder;

    private boolean mandatory;

    private String answerConfig;
}
