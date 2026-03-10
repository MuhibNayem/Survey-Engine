package com.bracits.surveyengine.featuremanagement.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RuntimeFeaturesResponse {

    @Builder.Default
    private List<RuntimeFeatureItemDTO> items = new ArrayList<>();
    private Instant generatedAt;
}

