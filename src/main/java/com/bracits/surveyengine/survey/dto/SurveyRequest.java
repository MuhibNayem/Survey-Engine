package com.bracits.surveyengine.survey.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SurveyRequest {

    @NotBlank(message = "Survey title is required")
    private String title;

    private String description;

    @Valid
    private List<SurveyPageRequest> pages;
}
