package com.bracits.surveyengine.subscription.service;

import com.bracits.surveyengine.subscription.entity.SubscriptionPlan;

import java.math.BigDecimal;
import java.util.Map;

public interface PaymentGateway {
    CheckoutSession initiateCheckout(CheckoutRequest request);

    ValidationResult validatePayment(ValidationRequest request);

    String providerName();

    record CheckoutRequest(
            String tenantId,
            String tenantName,
            String customerName,
            String customerEmail,
            SubscriptionPlan plan,
            BigDecimal amount,
            String currency,
            String gatewayReference,
            String checkoutSource) {
    }

    record CheckoutSession(
            String paymentUrl,
            String gatewayReference,
            String sessionKey,
            String gatewayStatus) {
    }

    record ValidationRequest(
            String gatewayReference,
            String validationReference,
            BigDecimal amount,
            String currency,
            Map<String, String> payload) {
    }

    record ValidationResult(
            boolean success,
            String gatewayReference,
            String validationReference,
            String gatewayStatus) {
    }
}
