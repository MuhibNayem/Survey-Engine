package com.bracits.surveyengine.subscription.service;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "survey-engine.payment.sslcommerz", name = "enabled", havingValue = "false")
public class MockPaymentGateway implements PaymentGateway {

    @Override
    public CheckoutSession initiateCheckout(CheckoutRequest request) {
        return new CheckoutSession(
                "https://sandbox.sslcommerz.com/mock/" + request.gatewayReference(),
                request.gatewayReference(),
                "mock-session-" + request.gatewayReference(),
                "PENDING");
    }

    @Override
    public ValidationResult validatePayment(ValidationRequest request) {
        return new ValidationResult(true, request.gatewayReference(), request.validationReference(), "VALID");
    }

    @Override
    public String providerName() {
        return "MOCK";
    }
}
