package com.bracits.surveyengine.response.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CampaignAnalytics {
    private long totalResponses;
    private long submittedCount;
    private long inProgressCount;
    private long lockedCount;
    private BigDecimal completionRate;
}
