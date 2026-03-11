package com.bracits.surveyengine.subscription.controller;

import com.bracits.surveyengine.common.tenant.TenantSupport;
import com.bracits.surveyengine.subscription.dto.CheckoutSessionResponse;
import com.bracits.surveyengine.subscription.dto.SubscribeRequest;
import com.bracits.surveyengine.subscription.dto.SubscriptionResponse;
import com.bracits.surveyengine.subscription.service.SubscriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @GetMapping("/me")
    public ResponseEntity<SubscriptionResponse> me() {
        return ResponseEntity.ok(subscriptionService.getCurrentSubscription(TenantSupport.requireCurrentTenant()));
    }

    @PostMapping("/checkout")
    public ResponseEntity<CheckoutSessionResponse> checkout(@Valid @RequestBody SubscribeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(subscriptionService.initiateCheckout(TenantSupport.requireCurrentTenant(), request));
    }
}
