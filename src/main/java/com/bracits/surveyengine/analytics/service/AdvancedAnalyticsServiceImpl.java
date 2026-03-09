package com.bracits.surveyengine.analytics.service;

import com.bracits.surveyengine.analytics.dto.OptionFrequency;
import com.bracits.surveyengine.analytics.dto.QuestionAnalyticsResponse;
import com.bracits.surveyengine.analytics.dto.ScoreDistributionResponse;
import com.bracits.surveyengine.analytics.dto.TemporalAnalyticsResponse;
import com.bracits.surveyengine.campaign.repository.CampaignRepository;
import com.bracits.surveyengine.common.exception.ResourceNotFoundException;
import com.bracits.surveyengine.common.tenant.TenantSupport;
import com.bracits.surveyengine.questionbank.entity.QuestionType;
import com.bracits.surveyengine.questionbank.entity.QuestionVersion;
import com.bracits.surveyengine.questionbank.repository.QuestionVersionRepository;
import com.bracits.surveyengine.response.entity.Answer;
import com.bracits.surveyengine.response.entity.ResponseStatus;
import com.bracits.surveyengine.response.entity.SurveyResponse;
import com.bracits.surveyengine.response.repository.SurveyResponseRepository;
import com.bracits.surveyengine.response.repository.SurveyResponseSpecification;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdvancedAnalyticsServiceImpl implements AdvancedAnalyticsService {

    private final SurveyResponseRepository responseRepository;
    private final CampaignRepository campaignRepository;
    private final QuestionVersionRepository questionVersionRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional(readOnly = true)
    public QuestionAnalyticsResponse getQuestionAnalytics(UUID campaignId, UUID questionId, Map<String, String> metadataFilters) {
        String tenantId = TenantSupport.currentTenantOrDefault();
        campaignRepository.findByIdAndTenantId(campaignId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign", campaignId));

        List<SurveyResponse> responses = fetchValidResponses(campaignId, tenantId, metadataFilters);

        Map<String, Long> frequencyMap = new HashMap<>();
        long totalAnswers = 0;
        BigDecimal sumScore = BigDecimal.ZERO;
        BigDecimal maxScore = null;
        QuestionType detectedType = null;

        for (SurveyResponse response : responses) {
            for (Answer answer : response.getAnswers()) {
                if (answer.getQuestionId().equals(questionId)) {
                    totalAnswers++;
                    
                    if (detectedType == null) {
                        UUID finalQuestionVersionId;
                        if (answer.getQuestionVersionId() != null) {
                            finalQuestionVersionId = answer.getQuestionVersionId();
                        } else {
                            finalQuestionVersionId = questionVersionRepository.findTopByQuestionIdOrderByVersionNumberDesc(questionId)
                                    .map(QuestionVersion::getId)
                                    .orElse(null);
                        }

                        if (finalQuestionVersionId != null) {
                            QuestionVersion qv = questionVersionRepository.findById(finalQuestionVersionId).orElse(null);
                            if (qv != null) {
                                detectedType = qv.getType();
                            }
                        }
                    }

                    if (answer.getScore() != null) {
                        sumScore = sumScore.add(answer.getScore());
                        if (maxScore == null || answer.getScore().compareTo(maxScore) > 0) {
                            maxScore = answer.getScore();
                        }
                    }

                    List<String> values = extractValues(answer.getValue());
                    for (String v : values) {
                        frequencyMap.put(v, frequencyMap.getOrDefault(v, 0L) + 1);
                    }
                }
            }
        }

        List<OptionFrequency> frequencies = new ArrayList<>();
        for (Map.Entry<String, Long> entry : frequencyMap.entrySet()) {
            BigDecimal percentage = totalAnswers > 0 
                ? BigDecimal.valueOf(entry.getValue())
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(totalAnswers), 2, RoundingMode.HALF_UP) 
                : BigDecimal.ZERO;
                
            frequencies.add(OptionFrequency.builder()
                    .optionValue(entry.getKey())
                    .count(entry.getValue())
                    .percentage(percentage)
                    .build());
        }

        // Sort by frequency descending
        frequencies.sort((a, b) -> Long.compare(b.getCount(), a.getCount()));

        BigDecimal averageScore = null;
        if (totalAnswers > 0 && sumScore.compareTo(BigDecimal.ZERO) > 0) {
            averageScore = sumScore.divide(BigDecimal.valueOf(totalAnswers), 2, RoundingMode.HALF_UP);
        }

        return QuestionAnalyticsResponse.builder()
                .campaignId(campaignId)
                .questionId(questionId)
                .questionType(detectedType)
                .totalAnswers(totalAnswers)
                .averageScore(averageScore)
                .maxScore(maxScore)
                .optionFrequencies(frequencies)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ScoreDistributionResponse getScoreDistribution(UUID campaignId, Map<String, String> metadataFilters) {
        String tenantId = TenantSupport.currentTenantOrDefault();
        campaignRepository.findByIdAndTenantId(campaignId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign", campaignId));

        List<SurveyResponse> responses = fetchValidResponses(campaignId, tenantId, metadataFilters);

        List<BigDecimal> scores = responses.stream()
                .map(SurveyResponse::getWeightedTotalScore)
                .filter(Objects::nonNull)
                .sorted()
                .collect(Collectors.toList());

        long totalScored = scores.size();
        if (totalScored == 0) {
            return ScoreDistributionResponse.builder()
                    .campaignId(campaignId)
                    .totalScoredResponses(0)
                    .scoreBuckets(Collections.emptyList())
                    .build();
        }

        BigDecimal sumScore = scores.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal averageScore = sumScore.divide(BigDecimal.valueOf(totalScored), 2, RoundingMode.HALF_UP);
        BigDecimal lowScore = scores.get(0);
        BigDecimal highScore = scores.get(scores.size() - 1);
        
        BigDecimal medianScore;
        if (totalScored % 2 == 0) {
            medianScore = scores.get((int)totalScored / 2 - 1)
                            .add(scores.get((int)totalScored / 2))
                            .divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);
        } else {
            medianScore = scores.get((int)totalScored / 2);
        }

        // Create 10 buckets (0-10, 10-20, ... 90-100) based on percentage assuming max possible is 100 for now, 
        // or we can just divide high-low into bins. For simplicity, let's create dynamic buckets.
        List<ScoreDistributionResponse.ScoreBucket> buckets = createScoreBuckets(scores, lowScore, highScore, 5);

        return ScoreDistributionResponse.builder()
                .campaignId(campaignId)
                .totalScoredResponses(totalScored)
                .averageScore(averageScore)
                .medianScore(medianScore)
                .highScore(highScore)
                .lowScore(lowScore)
                .scoreBuckets(buckets)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getCampaignSummary(UUID campaignId, Map<String, String> metadataFilters) {
        String tenantId = TenantSupport.currentTenantOrDefault();
        campaignRepository.findByIdAndTenantId(campaignId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign", campaignId));

        List<SurveyResponse> allMatches;
        if (metadataFilters != null && !metadataFilters.isEmpty()) {
            allMatches = responseRepository.findAll(SurveyResponseSpecification.matchesFilters(campaignId, tenantId, metadataFilters));
        } else {
            allMatches = responseRepository.findByCampaignId(campaignId); 
        }

        allMatches = allMatches.stream().filter(r -> r.getTenantId().equals(tenantId)).collect(Collectors.toList());

        long total = allMatches.size();
        long submitted = allMatches.stream().filter(r -> r.getStatus() == ResponseStatus.SUBMITTED).count();
        long locked = allMatches.stream().filter(r -> r.getStatus() == ResponseStatus.LOCKED).count();
        long inProgress = allMatches.stream().filter(r -> r.getStatus() == ResponseStatus.IN_PROGRESS).count();
        long completed = submitted + locked;

        BigDecimal completionRate = total > 0 
                ? BigDecimal.valueOf(completed).divide(BigDecimal.valueOf(total), 4, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        Map<String, Object> summary = new HashMap<>();
        summary.put("campaignId", campaignId);
        summary.put("totalResponses", total);
        summary.put("submittedCount", submitted);
        summary.put("lockedCount", locked);
        summary.put("inProgressCount", inProgress);
        summary.put("completionRate", completionRate);

        // Calculate average time if we have startedAt vs submittedAt (assuming in the future)
        // For now, we return basic advanced metrics.

        return summary;
    }

    @Override
    @Transactional(readOnly = true)
    public TemporalAnalyticsResponse getTemporalTrends(UUID campaignId, Map<String, String> metadataFilters) {
        String tenantId = TenantSupport.currentTenantOrDefault();
        campaignRepository.findByIdAndTenantId(campaignId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign", campaignId));

        List<SurveyResponse> responses = fetchValidResponses(campaignId, tenantId, metadataFilters);

        // Group by Date string (YYYY-MM-DD)
        Map<String, Long> grouping = new TreeMap<>(); // Treemap for chronological sorting
        
        for (SurveyResponse response : responses) {
            if (response.getSubmittedAt() != null) {
                // simple truncate to 10 chars "2023-10-05T..."
                String date = response.getSubmittedAt().toString().substring(0, 10);
                grouping.put(date, grouping.getOrDefault(date, 0L) + 1);
            }
        }

        List<TemporalAnalyticsResponse.TrendPoint> trendPoints = grouping.entrySet().stream()
                .map(e -> TemporalAnalyticsResponse.TrendPoint.builder()
                        .date(e.getKey())
                        .count(e.getValue())
                        .build())
                .collect(Collectors.toList());

        return TemporalAnalyticsResponse.builder()
                .trendPoints(trendPoints)
                .build();
    }

    private List<SurveyResponse> fetchValidResponses(UUID campaignId, String tenantId, Map<String, String> metadataFilters) {
        // Only count SUBMITTED or LOCKED responses for analytics
        List<SurveyResponse> allMatches;
        if (metadataFilters != null && !metadataFilters.isEmpty()) {
            allMatches = responseRepository.findAll(SurveyResponseSpecification.matchesFilters(campaignId, tenantId, metadataFilters));
        } else {
            allMatches = responseRepository.findByCampaignId(campaignId); 
            // Filter by tenant manually if Native query is used, but mostly handled by DB layer.
        }

        return allMatches.stream()
                .filter(r -> r.getTenantId().equals(tenantId))
                .filter(r -> r.getStatus() == ResponseStatus.SUBMITTED || r.getStatus() == ResponseStatus.LOCKED)
                .collect(Collectors.toList());
    }

    private List<String> extractValues(String rawValue) {
        if (rawValue == null || rawValue.isBlank()) {
            return Collections.emptyList();
        }
        
        String trimmed = rawValue.trim();
        if (trimmed.startsWith("[")) {
            try {
                return objectMapper.readValue(trimmed, new TypeReference<List<String>>() {});
            } catch (JsonProcessingException e) {
                return Collections.singletonList(trimmed);
            }
        } else {
            return Collections.singletonList(trimmed);
        }
    }

    private List<ScoreDistributionResponse.ScoreBucket> createScoreBuckets(List<BigDecimal> sortedScores, BigDecimal min, BigDecimal max, int numBuckets) {
        if (max.compareTo(min) == 0) {
            // All scores are exactly the same
            ScoreDistributionResponse.ScoreBucket bucket = ScoreDistributionResponse.ScoreBucket.builder()
                    .rangeLabel(min.toString())
                    .minRange(min)
                    .maxRange(max)
                    .count(sortedScores.size())
                    .percentage(BigDecimal.valueOf(100))
                    .build();
            return Collections.singletonList(bucket);
        }

        BigDecimal range = max.subtract(min);
        BigDecimal step = range.divide(BigDecimal.valueOf(numBuckets), 2, RoundingMode.HALF_UP);

        List<ScoreDistributionResponse.ScoreBucket> buckets = new ArrayList<>();
        BigDecimal currentMin = min;

        for (int i = 0; i < numBuckets; i++) {
            BigDecimal currentMax = (i == numBuckets - 1) ? max : currentMin.add(step);
            
            final BigDecimal cMin = currentMin;
            final BigDecimal cMax = currentMax;
            
            final boolean isLastBucket = (i == numBuckets - 1);
            long count = sortedScores.stream().filter(s -> 
                    (s.compareTo(cMin) >= 0) && (isLastBucket ? s.compareTo(cMax) <= 0 : s.compareTo(cMax) < 0)
            ).count();

            BigDecimal percentage = sortedScores.isEmpty() ? BigDecimal.ZERO : 
                    BigDecimal.valueOf(count).multiply(BigDecimal.valueOf(100))
                            .divide(BigDecimal.valueOf(sortedScores.size()), 2, RoundingMode.HALF_UP);

            String label = cMin.setScale(1, RoundingMode.HALF_UP) + " - " + cMax.setScale(1, RoundingMode.HALF_UP);

            buckets.add(ScoreDistributionResponse.ScoreBucket.builder()
                    .rangeLabel(label)
                    .minRange(cMin)
                    .maxRange(cMax)
                    .count(count)
                    .percentage(percentage)
                    .build());

            currentMin = currentMax;
        }

        return buckets;
    }
}
