package com.bracits.surveyengine.campaign.service;

import com.bracits.surveyengine.campaign.entity.CampaignSettings;
import com.bracits.surveyengine.campaign.repository.CampaignSettingsRepository;
import com.bracits.surveyengine.response.service.ResponseLockingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

/**
 * Enforces close-date behavior by locking still-open responses when a campaign
 * passes its close date.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CampaignCloseResponseLockScheduler {

    private final CampaignSettingsRepository campaignSettingsRepository;
    private final ResponseLockingService responseLockingService;

    @Scheduled(fixedDelayString = "${survey.campaign-close-lock.fixed-delay-ms:60000}")
    @Transactional
    public void lockOpenResponsesForExpiredCampaigns() {
        Instant now = Instant.now();
        List<CampaignSettings> expiredSettings = campaignSettingsRepository
                .findByCloseDateNotNullAndCloseDateLessThanEqual(now);

        for (CampaignSettings settings : expiredSettings) {
            int lockedCount = responseLockingService.lockOpenResponsesForCampaignClosure(
                    settings.getCampaignId(), now);
            if (lockedCount > 0) {
                log.info("Auto-locked {} response(s) for campaign {} due to close date {}",
                        lockedCount, settings.getCampaignId(), settings.getCloseDate());
            }
        }
    }
}
