package com.bracits.surveyengine.subscription.dto;

import com.bracits.surveyengine.subscription.entity.SubscriptionPlan;
import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscribeRequest {

    @NotNull
    @JsonAlias("planCode")
    private SubscriptionPlan plan;
}
