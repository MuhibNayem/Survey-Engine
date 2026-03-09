package com.bracits.surveyengine.subscription.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeatureResponse {
    private String featureCode;
    private String category;
    private String name;
    private String description;
    private Integer displayOrder;
}
