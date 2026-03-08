package com.bracits.surveyengine.scoring.service;

import com.bracits.surveyengine.scoring.dto.ScoreResult;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

/**
 * Scoring engine contract implementing the weighted formula.
 * SRS §5.2: raw → normalized → weighted → step total
 */
public interface ScoringEngineService {

    /**
     * Calculates the total weighted score for a set of category raw scores.
     *
     * @param weightProfileId   the weight profile to use
     * @param categoryRawScores map of categoryId → raw score obtained
     * @return full score breakdown
     */
    ScoreResult calculateScore(UUID weightProfileId, Map<UUID, BigDecimal> categoryRawScores);

    /**
     * Calculates weighted score with an explicit tenant context.
     *
     * @param weightProfileId   the weight profile to use
     * @param tenantId          tenant that owns the scoring assets
     * @param categoryRawScores map of categoryId → raw score obtained
     * @return full score breakdown
     */
    ScoreResult calculateScore(UUID weightProfileId, String tenantId, Map<UUID, BigDecimal> categoryRawScores);
}
