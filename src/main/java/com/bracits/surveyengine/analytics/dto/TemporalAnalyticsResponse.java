package com.bracits.surveyengine.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TemporalAnalyticsResponse {
    
    private List<TrendPoint> trendPoints;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TrendPoint {
        private String date; // YYYY-MM-DD format
        private long count;
    }
}
