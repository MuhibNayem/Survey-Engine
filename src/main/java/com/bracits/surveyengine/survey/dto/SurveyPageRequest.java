package com.bracits.surveyengine.survey.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SurveyPageRequest {

    private String title;

    @NotNull(message = "Sort order is required")
    private Integer sortOrder;

    @Valid
    private List<SurveyQuestionRequest> questions;
}
