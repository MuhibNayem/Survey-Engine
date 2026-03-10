package com.bracits.surveyengine.featuremanagement.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FeatureEventRequest {

    @NotBlank(message = "eventType is required")
    private String eventType;

    @Builder.Default
    private Map<String, Object> metadata = new HashMap<>();
}

