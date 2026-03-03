package com.bracits.surveyengine.response.service;

import com.bracits.surveyengine.response.dto.CampaignAnalytics;
import com.bracits.surveyengine.response.entity.ResponseStatus;
import com.bracits.surveyengine.response.repository.SurveyResponseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

/**
 * Implementation of {@link AnalyticsService}.
 * <p>
 * SRS §4.7: traffic data, completion rate, incomplete count.
 */
@Service
@RequiredArgsConstructor
public class AnalyticsServiceImpl implements AnalyticsService {

    private final SurveyResponseRepository responseRepository;

    @Override
    @Transactional(readOnly = true)
    public CampaignAnalytics getAnalytics(UUID campaignId) {
        long total = responseRepository.countByCampaignId(campaignId);
        long submitted = responseRepository.countByCampaignIdAndStatus(campaignId, ResponseStatus.SUBMITTED);
        long locked = responseRepository.countByCampaignIdAndStatus(campaignId, ResponseStatus.LOCKED);
        long inProgress = responseRepository.countByCampaignIdAndStatus(campaignId, ResponseStatus.IN_PROGRESS);

        long completedCount = submitted + locked;
        BigDecimal completionRate = total > 0
                ? BigDecimal.valueOf(completedCount)
                        .divide(BigDecimal.valueOf(total), 4, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        return CampaignAnalytics.builder()
                .totalResponses(total)
                .submittedCount(submitted)
                .lockedCount(locked)
                .inProgressCount(inProgress)
                .completionRate(completionRate)
                .build();
    }
}
