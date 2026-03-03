package com.bracits.surveyengine.subscription.dto;

import com.bracits.surveyengine.subscription.entity.SubscriptionPlan;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanDefinitionResponse {
    private UUID id;
    private SubscriptionPlan planCode;
    private String displayName;
    private BigDecimal price;
    private String currency;
    private Integer billingCycleDays;
    private Integer trialDays;
    private Integer maxCampaigns;
    private Integer maxResponsesPerCampaign;
    private Integer maxAdminUsers;
    private boolean active;
}
