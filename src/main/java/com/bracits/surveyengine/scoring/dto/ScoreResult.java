package com.bracits.surveyengine.scoring.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Score calculation result (SRS §5.2).
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScoreResult {
    private UUID campaignId;
    private UUID weightProfileId;
    private BigDecimal totalScore;
    private List<CategoryScoreDetail> categoryScores;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryScoreDetail {
        private UUID categoryId;
        private BigDecimal rawScore;
        private BigDecimal maxScore;
        private BigDecimal normalizedScore;
        private BigDecimal weightPercentage;
        private BigDecimal weightedScore;
    }
}
