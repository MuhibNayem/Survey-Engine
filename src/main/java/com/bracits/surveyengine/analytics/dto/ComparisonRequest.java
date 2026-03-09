package com.bracits.surveyengine.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComparisonRequest {
    
    private List<SegmentConfig> segments;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SegmentConfig {
        private String name;
        private Map<String, String> metadataFilters;
    }
}
