package com.bracits.surveyengine.scoring.service;

import com.bracits.surveyengine.campaign.entity.Campaign;
import com.bracits.surveyengine.campaign.repository.CampaignRepository;
import com.bracits.surveyengine.common.exception.BusinessException;
import com.bracits.surveyengine.common.exception.ErrorCode;
import com.bracits.surveyengine.common.exception.ResourceNotFoundException;
import com.bracits.surveyengine.common.tenant.TenantSupport;
import com.bracits.surveyengine.questionbank.entity.CategoryVersion;
import com.bracits.surveyengine.questionbank.entity.QuestionVersion;
import com.bracits.surveyengine.questionbank.repository.CategoryVersionRepository;
import com.bracits.surveyengine.questionbank.repository.QuestionVersionRepository;
import com.bracits.surveyengine.scoring.dto.ScoreResult;
import com.bracits.surveyengine.scoring.entity.CategoryWeight;
import com.bracits.surveyengine.scoring.entity.WeightProfile;
import com.bracits.surveyengine.scoring.repository.WeightProfileRepository;
import com.bracits.surveyengine.survey.entity.SurveySnapshot;
import com.bracits.surveyengine.survey.repository.SurveySnapshotRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Scoring uses the campaign-linked pinned survey snapshot so bank changes never
 * alter historical scoring semantics.
 */
@Service
@RequiredArgsConstructor
public class ScoringEngineServiceImpl implements ScoringEngineService {

    private final WeightProfileRepository weightProfileRepository;
    private final CampaignRepository campaignRepository;
    private final SurveySnapshotRepository surveySnapshotRepository;
    private final CategoryVersionRepository categoryVersionRepository;
    private final QuestionVersionRepository questionVersionRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional(readOnly = true)
    public ScoreResult calculateScore(UUID weightProfileId, Map<UUID, BigDecimal> categoryRawScores) {
        return calculateScore(weightProfileId, TenantSupport.currentTenantOrDefault(), categoryRawScores);
    }

    @Override
    @Transactional(readOnly = true)
    public ScoreResult calculateScore(UUID weightProfileId, String tenantId, Map<UUID, BigDecimal> categoryRawScores) {
        if (tenantId == null || tenantId.isBlank()) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED, "Tenant context is required for score calculation");
        }

        WeightProfile profile = weightProfileRepository.findByIdAndTenantId(weightProfileId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("WeightProfile", weightProfileId));

        Campaign campaign = campaignRepository.findByIdAndTenantId(profile.getCampaignId(), tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign", profile.getCampaignId()));

        if (campaign.getSurveySnapshotId() == null) {
            throw new BusinessException(ErrorCode.CATEGORY_MAX_SCORE_ZERO,
                    "Campaign has no pinned survey snapshot");
        }

        Map<UUID, UUID> pinnedCategoryVersionByCategoryId = extractPinnedCategoryVersions(campaign.getSurveySnapshotId());

        List<ScoreResult.CategoryScoreDetail> categoryScoreDetails = new ArrayList<>();
        BigDecimal totalScore = BigDecimal.ZERO;

        for (CategoryWeight cw : profile.getCategoryWeights()) {
            UUID categoryId = cw.getCategoryId();
            BigDecimal rawScore = categoryRawScores.getOrDefault(categoryId, BigDecimal.ZERO);

            UUID categoryVersionId = pinnedCategoryVersionByCategoryId.get(categoryId);
            if (categoryVersionId == null) {
                throw new BusinessException(ErrorCode.CATEGORY_MAX_SCORE_ZERO,
                        "Category %s is not pinned in campaign survey snapshot".formatted(categoryId));
            }

            BigDecimal maxScore = calculateCategoryMaxScore(categoryVersionId);
            if (maxScore.compareTo(BigDecimal.ZERO) <= 0) {
                throw new BusinessException(ErrorCode.CATEGORY_MAX_SCORE_ZERO,
                        "Category %s has max score of zero; cannot normalize".formatted(categoryId));
            }

            BigDecimal normalizedScore = rawScore.divide(maxScore, 6, RoundingMode.HALF_UP);
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

    private Map<UUID, UUID> extractPinnedCategoryVersions(UUID surveySnapshotId) {
        SurveySnapshot snapshot = surveySnapshotRepository.findById(surveySnapshotId)
                .orElseThrow(() -> new ResourceNotFoundException("SurveySnapshot", surveySnapshotId));

        try {
            JsonNode root = objectMapper.readTree(snapshot.getSnapshotData());
            JsonNode pagesNode = root.path("pages");
            if (!pagesNode.isArray()) {
                throw new BusinessException(ErrorCode.VALIDATION_FAILED,
                        "Survey snapshot pages structure is invalid");
            }

            Map<UUID, UUID> categoryVersionByCategoryId = new LinkedHashMap<>();
            for (JsonNode pageNode : pagesNode) {
                JsonNode questionsNode = pageNode.path("questions");
                if (!questionsNode.isArray()) {
                    continue;
                }
                for (JsonNode questionNode : questionsNode) {
                    if (!questionNode.hasNonNull("categoryId")) {
                        continue;
                    }

                    UUID categoryId = UUID.fromString(questionNode.path("categoryId").asText());
                    UUID categoryVersionId = null;
                    if (questionNode.hasNonNull("categoryVersionId")) {
                        categoryVersionId = UUID.fromString(questionNode.path("categoryVersionId").asText());
                    }

                    if (categoryVersionId == null) {
                        categoryVersionId = categoryVersionRepository
                                .findTopByCategoryIdOrderByVersionNumberDesc(categoryId)
                                .map(CategoryVersion::getId)
                                .orElse(null);
                    }
                    if (categoryVersionId == null) {
                        continue;
                    }

                    UUID previous = categoryVersionByCategoryId.putIfAbsent(categoryId, categoryVersionId);
                    if (previous != null && !previous.equals(categoryVersionId)) {
                        throw new BusinessException(ErrorCode.VALIDATION_FAILED,
                                "Category %s has multiple pinned versions in snapshot".formatted(categoryId));
                    }
                }
            }

            return categoryVersionByCategoryId;
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new BusinessException(ErrorCode.VALIDATION_FAILED,
                    "Survey snapshot cannot be parsed for scoring");
        }
    }

    private BigDecimal calculateCategoryMaxScore(UUID categoryVersionId) {
        CategoryVersion categoryVersion = categoryVersionRepository.findById(categoryVersionId)
                .orElseThrow(() -> new ResourceNotFoundException("CategoryVersion", categoryVersionId));

        return categoryVersion.getQuestionMappings().stream()
                .map(mapping -> {
                    QuestionVersion questionVersion = questionVersionRepository
                            .findById(mapping.getQuestionVersionId())
                            .orElseThrow(() -> new BusinessException(
                                    ErrorCode.QUESTION_MAX_SCORE_INVALID,
                                    "Question version %s not found".formatted(mapping.getQuestionVersionId())));

                    BigDecimal questionMax = questionVersion.getMaxScore();
                    if (questionMax == null || questionMax.compareTo(BigDecimal.ZERO) <= 0) {
                        throw new BusinessException(ErrorCode.QUESTION_MAX_SCORE_INVALID,
                                "Question version %s has invalid max score".formatted(questionVersion.getId()));
                    }

                    BigDecimal weight = mapping.getWeight() != null ? mapping.getWeight() : BigDecimal.ONE;
                    return questionMax.multiply(weight);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
