package com.bracits.surveyengine.subscription.dto;

import com.bracits.surveyengine.subscription.entity.SubscriptionPlan;
import com.bracits.surveyengine.subscription.entity.SubscriptionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;
import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionResponse {
    private UUID id;
    private String tenantId;
    private SubscriptionPlan plan;
    private SubscriptionStatus status;
    private Instant currentPeriodStart;
    private Instant currentPeriodEnd;
    private String lastPaymentGatewayReference;
    private BigDecimal planPrice;
    private String currency;
    private Integer maxCampaigns;
    private Integer maxResponsesPerCampaign;
    private Integer maxAdminUsers;
}
