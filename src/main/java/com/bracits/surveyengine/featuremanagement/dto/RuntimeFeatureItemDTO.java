package com.bracits.surveyengine.featuremanagement.dto;

import com.bracits.surveyengine.featuremanagement.entity.FeatureType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RuntimeFeatureItemDTO {

    private UUID featureId;
    private String featureKey;
    private String name;
    private String description;
    private FeatureType featureType;
    private String surface;
    private Integer priority;
    private Boolean blocking;
    private Boolean shouldShow;
    @Builder.Default
    private Map<String, Object> metadata = new HashMap<>();
}

