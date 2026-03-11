package com.bracits.surveyengine.subscription.controller;

import com.bracits.surveyengine.subscription.entity.PaymentStatus;
import com.bracits.surveyengine.subscription.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/public/payments/sslcommerz")
@RequiredArgsConstructor
public class SslCommerzCallbackController {

    private final SubscriptionService subscriptionService;

    @PostMapping("/success")
    public ResponseEntity<Void> success(@RequestParam Map<String, String> payload) {
        return redirect(subscriptionService.handleGatewaySuccess(payload));
    }

    @GetMapping("/success")
    public ResponseEntity<Void> successGet(@RequestParam Map<String, String> payload) {
        return redirect(subscriptionService.handleGatewaySuccess(payload));
    }

    @PostMapping("/fail")
    public ResponseEntity<Void> fail(@RequestParam Map<String, String> payload) {
        return redirect(subscriptionService.handleGatewayFailure(payload, PaymentStatus.FAILED));
    }

    @GetMapping("/fail")
    public ResponseEntity<Void> failGet(@RequestParam Map<String, String> payload) {
        return redirect(subscriptionService.handleGatewayFailure(payload, PaymentStatus.FAILED));
    }

    @PostMapping("/cancel")
    public ResponseEntity<Void> cancel(@RequestParam Map<String, String> payload) {
        return redirect(subscriptionService.handleGatewayFailure(payload, PaymentStatus.CANCELED));
    }

    @GetMapping("/cancel")
    public ResponseEntity<Void> cancelGet(@RequestParam Map<String, String> payload) {
        return redirect(subscriptionService.handleGatewayFailure(payload, PaymentStatus.CANCELED));
    }

    private ResponseEntity<Void> redirect(String location) {
        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, URI.create(location).toString())
                .build();
    }
}
