package com.bracits.surveyengine.scoring.service;

import com.bracits.surveyengine.common.exception.BusinessException;
import com.bracits.surveyengine.common.exception.ErrorCode;
import com.bracits.surveyengine.common.exception.ResourceNotFoundException;
import com.bracits.surveyengine.common.tenant.TenantSupport;
import com.bracits.surveyengine.questionbank.entity.QuestionVersion;
import com.bracits.surveyengine.questionbank.repository.CategoryRepository;
import com.bracits.surveyengine.questionbank.repository.CategoryVersionRepository;
import com.bracits.surveyengine.questionbank.repository.QuestionVersionRepository;
import com.bracits.surveyengine.scoring.dto.ScoreResult;
import com.bracits.surveyengine.scoring.entity.CategoryWeight;
import com.bracits.surveyengine.scoring.entity.WeightProfile;
import com.bracits.surveyengine.scoring.repository.WeightProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * Implementation of the weighted scoring formula (SRS §5.2):
 * <ol>
 * <li><b>Raw Score</b>: Sum of question scores in a category</li>
 * <li><b>Normalized Score</b>: rawScore / categoryMaxScore (0..1)</li>
 * <li><b>Weighted Score</b>: normalizedScore × (weightPercentage / 100)</li>
 * <li><b>Total</b>: Sum of all weighted scores</li>
 * </ol>
 */
@Service
@RequiredArgsConstructor
public class ScoringEngineServiceImpl implements ScoringEngineService {

        private final WeightProfileRepository weightProfileRepository;
        private final CategoryRepository categoryRepository;
        private final CategoryVersionRepository categoryVersionRepository;
        private final QuestionVersionRepository questionVersionRepository;

        @Override
        @Transactional(readOnly = true)
        public ScoreResult calculateScore(UUID weightProfileId, Map<UUID, BigDecimal> categoryRawScores) {
                String tenantId = TenantSupport.currentTenantOrDefault();
                WeightProfile profile = weightProfileRepository.findByIdAndTenantId(weightProfileId, tenantId)
                                .orElseThrow(() -> new ResourceNotFoundException("WeightProfile", weightProfileId));

                List<ScoreResult.CategoryScoreDetail> categoryScoreDetails = new ArrayList<>();
                BigDecimal totalScore = BigDecimal.ZERO;

                for (CategoryWeight cw : profile.getCategoryWeights()) {
                        UUID categoryId = cw.getCategoryId();
                        BigDecimal rawScore = categoryRawScores.getOrDefault(categoryId, BigDecimal.ZERO);

                        // Determine category max score from the latest version's question mappings
                        BigDecimal maxScore = calculateCategoryMaxScore(categoryId, tenantId);
                        if (maxScore.compareTo(BigDecimal.ZERO) <= 0) {
                                throw new BusinessException(ErrorCode.CATEGORY_MAX_SCORE_ZERO,
                                                "Category %s has max score of zero; cannot normalize"
                                                                .formatted(categoryId));
                        }

                        // Step 2: Normalize
                        BigDecimal normalizedScore = rawScore.divide(maxScore, 6, RoundingMode.HALF_UP);

                        // Step 3: Apply weight
                        BigDecimal weightFraction = cw.getWeightPercentage()
                                        .divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP);
                        BigDecimal weightedScore = normalizedScore.multiply(weightFraction)
                                        .setScale(6, RoundingMode.HALF_UP);

                        totalScore = totalScore.add(weightedScore);

                        categoryScoreDetails.add(ScoreResult.CategoryScoreDetail.builder()
                                        .categoryId(categoryId)
                                        .rawScore(rawScore)
                                        .maxScore(maxScore)
                                        .normalizedScore(normalizedScore)
                                        .weightPercentage(cw.getWeightPercentage())
                                        .weightedScore(weightedScore)
                                        .build());
                }

                return ScoreResult.builder()
                                .campaignId(profile.getCampaignId())
                                .weightProfileId(profile.getId())
                                .totalScore(totalScore.setScale(4, RoundingMode.HALF_UP))
                                .categoryScores(categoryScoreDetails)
                                .build();
        }

        /**
         * Computes the max possible score for a category by summing maxScore of
         * all questions in the latest category version, weighted by their mapping
         * weight.
         */
        private BigDecimal calculateCategoryMaxScore(UUID categoryId, String tenantId) {
                categoryRepository.findByIdAndTenantId(categoryId, tenantId)
                                .orElseThrow(() -> new ResourceNotFoundException("Category", categoryId));

                return categoryVersionRepository
                                .findTopByCategoryIdOrderByVersionNumberDesc(categoryId)
                                .map(version -> version.getQuestionMappings().stream()
                                                .map(m -> {
                                                        QuestionVersion qv = questionVersionRepository
                                                                        .findById(m.getQuestionVersionId())
                                                                        .orElseThrow(() -> new BusinessException(
                                                                                        ErrorCode.QUESTION_MAX_SCORE_INVALID,
                                                                                        "Question version %s not found"
                                                                                                        .formatted(m.getQuestionVersionId())));
                                                        BigDecimal questionMax = qv.getMaxScore();
                                                        if (questionMax == null || questionMax
                                                                        .compareTo(BigDecimal.ZERO) <= 0) {
                                                                throw new BusinessException(
                                                                                ErrorCode.QUESTION_MAX_SCORE_INVALID,
                                                                                "Question version %s has invalid max score"
                                                                                                .formatted(qv.getId()));
                                                        }
                                                        BigDecimal weight = m.getWeight() != null ? m.getWeight()
                                                                        : BigDecimal.ONE;
                                                        return questionMax.multiply(weight);
                                                })
                                                .reduce(BigDecimal.ZERO, BigDecimal::add))
                                .orElse(BigDecimal.ZERO);
        }
}
