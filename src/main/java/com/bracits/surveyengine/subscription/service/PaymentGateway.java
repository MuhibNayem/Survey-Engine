package com.bracits.surveyengine.subscription.service;

import com.bracits.surveyengine.subscription.entity.SubscriptionPlan;

import java.math.BigDecimal;

public interface PaymentGateway {
    PaymentResult charge(String tenantId, SubscriptionPlan plan, BigDecimal amount, String currency);

    record PaymentResult(boolean success, String gatewayReference) {
    }
}
