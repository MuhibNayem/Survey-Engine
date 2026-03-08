package com.bracits.surveyengine.response.service;

import com.bracits.surveyengine.campaign.repository.CampaignRepository;
import com.bracits.surveyengine.common.exception.ResourceNotFoundException;
import com.bracits.surveyengine.common.tenant.TenantSupport;
import com.bracits.surveyengine.response.dto.CampaignAnalytics;
import com.bracits.surveyengine.response.entity.ResponseStatus;
import com.bracits.surveyengine.response.repository.SurveyResponseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.UUID;
import com.bracits.surveyengine.response.repository.SurveyResponseSpecification;

/**
 * Implementation of {@link AnalyticsService}.
 * <p>
 * SRS §4.7: traffic data, completion rate, incomplete count.
 */
@Service
@RequiredArgsConstructor
public class AnalyticsServiceImpl implements AnalyticsService {

    private final SurveyResponseRepository responseRepository;
    private final CampaignRepository campaignRepository;

    @Override
    @Transactional(readOnly = true)
    public CampaignAnalytics getAnalytics(UUID campaignId, Map<String, String> metadataFilters) {
        String tenantId = TenantSupport.currentTenantOrDefault();
        campaignRepository.findByIdAndTenantId(campaignId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign", campaignId));

        long total, submitted, locked, inProgress;

        if (metadataFilters != null && !metadataFilters.isEmpty()) {
            total = responseRepository.count(SurveyResponseSpecification.matchesFilters(campaignId, tenantId, metadataFilters));
            submitted = responseRepository.count(SurveyResponseSpecification.matchesFiltersAndStatus(campaignId, tenantId, ResponseStatus.SUBMITTED, metadataFilters));
            locked = responseRepository.count(SurveyResponseSpecification.matchesFiltersAndStatus(campaignId, tenantId, ResponseStatus.LOCKED, metadataFilters));
            inProgress = responseRepository.count(SurveyResponseSpecification.matchesFiltersAndStatus(campaignId, tenantId, ResponseStatus.IN_PROGRESS, metadataFilters));
        } else {
            total = responseRepository.countByCampaignIdAndTenantId(campaignId, tenantId);
            submitted = responseRepository.countByCampaignIdAndStatusAndTenantId(
                    campaignId, ResponseStatus.SUBMITTED, tenantId);
            locked = responseRepository.countByCampaignIdAndStatusAndTenantId(
                    campaignId, ResponseStatus.LOCKED, tenantId);
            inProgress = responseRepository.countByCampaignIdAndStatusAndTenantId(
                    campaignId, ResponseStatus.IN_PROGRESS, tenantId);
        }

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
