package com.bracits.surveyengine.admin.dto;

import com.bracits.surveyengine.subscription.entity.SubscriptionPlan;
import com.bracits.surveyengine.subscription.entity.SubscriptionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantOverviewResponse {
    private String tenantId;
    private String name;
    private String primaryEmail;
    private SubscriptionPlan plan;
    private SubscriptionStatus subscriptionStatus;
    private boolean active;
    private Instant createdAt;
}
