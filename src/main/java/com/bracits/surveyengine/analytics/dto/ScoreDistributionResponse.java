package com.bracits.surveyengine.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScoreDistributionResponse {
    private UUID campaignId;
    private long totalScoredResponses;
    
    private BigDecimal averageScore;
    private BigDecimal medianScore;
    private BigDecimal highScore;
    private BigDecimal lowScore;
    
    private List<ScoreBucket> scoreBuckets;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ScoreBucket {
        private String rangeLabel; // e.g. "0-10%", "90-100%"
        private BigDecimal minRange;
        private BigDecimal maxRange;
        private long count;
        private BigDecimal percentage;
    }
}
