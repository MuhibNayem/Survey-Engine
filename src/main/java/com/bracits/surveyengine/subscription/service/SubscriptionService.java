package com.bracits.surveyengine.subscription.service;

import com.bracits.surveyengine.common.exception.BusinessException;
import com.bracits.surveyengine.common.exception.ErrorCode;
import com.bracits.surveyengine.subscription.dto.SubscribeRequest;
import com.bracits.surveyengine.subscription.dto.SubscriptionResponse;
import com.bracits.surveyengine.subscription.entity.PlanDefinition;
import com.bracits.surveyengine.subscription.entity.PaymentStatus;
import com.bracits.surveyengine.subscription.entity.PaymentTransaction;
import com.bracits.surveyengine.subscription.entity.Subscription;
import com.bracits.surveyengine.subscription.entity.SubscriptionPlan;
import com.bracits.surveyengine.subscription.entity.SubscriptionStatus;
import com.bracits.surveyengine.subscription.repository.PaymentTransactionRepository;
import com.bracits.surveyengine.subscription.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private static final List<SubscriptionStatus> ACTIVE_STATES = List.of(SubscriptionStatus.ACTIVE, SubscriptionStatus.TRIAL);

    private final SubscriptionRepository subscriptionRepository;
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final PaymentGateway paymentGateway;
    private final PlanCatalogService planCatalogService;

    @Transactional
    public Subscription ensureTrial(String tenantId) {
        PlanDefinition trialPlan = planCatalogService.getActivePlanEntity(SubscriptionPlan.BASIC);
        return subscriptionRepository.findByTenantId(tenantId)
                .orElseGet(() -> subscriptionRepository.save(Subscription.builder()
                        .tenantId(tenantId)
                        .plan(trialPlan.getPlanCode())
                        .status(SubscriptionStatus.TRIAL)
                        .currentPeriodStart(Instant.now())
                        .currentPeriodEnd(Instant.now().plus(trialPlan.getTrialDays(), ChronoUnit.DAYS))
                        .build()));
    }

    @Transactional
    public SubscriptionResponse subscribe(String tenantId, SubscribeRequest request) {
        Subscription subscription = ensureTrial(tenantId);
        PlanDefinition plan = planCatalogService.getActivePlanEntity(request.getPlan());

        PaymentGateway.PaymentResult paymentResult = paymentGateway.charge(
                tenantId, request.getPlan(), plan.getPrice(), plan.getCurrency());
        PaymentTransaction payment = PaymentTransaction.builder()
                .tenantId(tenantId)
                .subscriptionId(subscription.getId())
                .status(paymentResult.success() ? PaymentStatus.SUCCESS : PaymentStatus.FAILED)
                .gatewayReference(paymentResult.gatewayReference())
                .amount(plan.getPrice())
                .currency(plan.getCurrency())
                .processedAt(Instant.now())
                .build();
        paymentTransactionRepository.save(payment);

        if (!paymentResult.success()) {
            throw new BusinessException(ErrorCode.PAYMENT_FAILED, "Subscription payment failed");
        }

        Instant now = Instant.now();
        subscription.setPlan(request.getPlan());
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        subscription.setCurrentPeriodStart(now);
        subscription.setCurrentPeriodEnd(now.plus(plan.getBillingCycleDays(), ChronoUnit.DAYS));
        subscription = subscriptionRepository.save(subscription);

        return toResponse(subscription, payment.getGatewayReference());
    }

    @Transactional
    public SubscriptionResponse getCurrentSubscription(String tenantId) {
        Subscription subscription = subscriptionRepository.findByTenantId(tenantId)
                .orElseGet(() -> ensureTrial(tenantId));

        String gatewayRef = paymentTransactionRepository.findByTenantIdOrderByProcessedAtDesc(tenantId)
                .stream()
                .findFirst()
                .map(PaymentTransaction::getGatewayReference)
                .orElse(null);

        return toResponse(subscription, gatewayRef);
    }

    @Transactional(readOnly = true)
    public boolean hasActiveSubscription(String tenantId) {
        return subscriptionRepository.existsByTenantIdAndStatusInAndCurrentPeriodEndAfter(
                tenantId, ACTIVE_STATES, Instant.now());
    }

    private SubscriptionResponse toResponse(Subscription subscription, String paymentRef) {
        PlanDefinition plan = planCatalogService.getActivePlanEntity(subscription.getPlan());
        return SubscriptionResponse.builder()
                .id(subscription.getId())
                .tenantId(subscription.getTenantId())
                .plan(subscription.getPlan())
                .status(subscription.getStatus())
                .currentPeriodStart(subscription.getCurrentPeriodStart())
                .currentPeriodEnd(subscription.getCurrentPeriodEnd())
                .lastPaymentGatewayReference(paymentRef)
                .planPrice(plan.getPrice())
                .currency(plan.getCurrency())
                .maxCampaigns(plan.getMaxCampaigns())
                .maxResponsesPerCampaign(plan.getMaxResponsesPerCampaign())
                .maxAdminUsers(plan.getMaxAdminUsers())
                .build();
    }
}
