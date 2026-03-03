package com.bracits.surveyengine.subscription.dto;

import com.bracits.surveyengine.subscription.entity.SubscriptionPlan;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanDefinitionRequest {

    @NotNull
    private SubscriptionPlan planCode;

    @NotBlank
    private String displayName;

    @NotNull
    private BigDecimal price;

    @NotBlank
    private String currency;

    @NotNull
    @Min(1)
    private Integer billingCycleDays;

    @NotNull
    @Min(0)
    private Integer trialDays;

    @Min(1)
    private Integer maxCampaigns;

    @Min(1)
    private Integer maxResponsesPerCampaign;

    @Min(1)
    private Integer maxAdminUsers;

    @Builder.Default
    private boolean active = true;
}
