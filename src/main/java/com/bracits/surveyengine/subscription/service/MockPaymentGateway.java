package com.bracits.surveyengine.subscription.service;

import com.bracits.surveyengine.subscription.entity.SubscriptionPlan;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class MockPaymentGateway implements PaymentGateway {

    @Override
    public PaymentResult charge(String tenantId, SubscriptionPlan plan, BigDecimal amount, String currency) {
        String ref = "mock-" + tenantId + "-" + plan.name().toLowerCase() + "-" + UUID.randomUUID();
        return new PaymentResult(true, ref);
    }
}
