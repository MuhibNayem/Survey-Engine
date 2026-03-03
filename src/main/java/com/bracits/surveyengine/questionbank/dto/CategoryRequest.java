package com.bracits.surveyengine.questionbank.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryRequest {

    @NotBlank(message = "Category name is required")
    private String name;

    private String description;

    @Valid
    private List<CategoryQuestionMappingRequest> questionMappings;
}
