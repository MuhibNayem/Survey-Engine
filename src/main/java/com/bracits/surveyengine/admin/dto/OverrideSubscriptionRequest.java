package com.bracits.surveyengine.admin.dto;

import com.bracits.surveyengine.subscription.entity.SubscriptionPlan;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OverrideSubscriptionRequest {
    @NotNull(message = "Plan is required")
    private SubscriptionPlan plan;
}
