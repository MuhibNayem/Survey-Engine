package com.bracits.surveyengine.subscription.service;

import com.bracits.surveyengine.admin.context.TenantContext;
import com.bracits.surveyengine.common.exception.BusinessException;
import com.bracits.surveyengine.common.exception.ErrorCode;
import com.bracits.surveyengine.subscription.config.SslCommerzProperties;
import com.bracits.surveyengine.subscription.dto.CheckoutSessionResponse;
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
import com.bracits.surveyengine.tenant.entity.Tenant;
import com.bracits.surveyengine.tenant.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private static final List<SubscriptionStatus> ACTIVE_STATES = List.of(SubscriptionStatus.ACTIVE,
            SubscriptionStatus.TRIAL);

    private final SubscriptionRepository subscriptionRepository;
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final PaymentGateway paymentGateway;
    private final PlanCatalogService planCatalogService;
    private final TenantRepository tenantRepository;
    private final SslCommerzProperties sslCommerzProperties;

    @Transactional
    @com.bracits.surveyengine.common.audit.annotation.Auditable(action = "SUBSCRIPTION_CREATED")
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
    @com.bracits.surveyengine.common.audit.annotation.Auditable(action = "SUBSCRIPTION_CHECKOUT_INITIATED")
    public CheckoutSessionResponse initiateCheckout(String tenantId, SubscribeRequest request) {
        Subscription subscription = ensureTrial(tenantId);
        SubscriptionPlan requestedPlan = request.getPlan();
        enforceCheckoutRestrictions(subscription, requestedPlan);

        PlanDefinition plan = planCatalogService.getActivePlanEntity(requestedPlan);
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Tenant not found"));
        String gatewayReference = buildGatewayReference(tenantId, requestedPlan);
        String source = normalizeSource(request.getSource());
        TenantContext.TenantInfo tenantInfo = TenantContext.get();
        String customerEmail = tenantInfo != null && tenantInfo.email() != null ? tenantInfo.email() : tenantId + "@example.com";
        String customerName = tenantInfo != null && tenantInfo.email() != null ? tenantInfo.email() : tenant.getName();

        PaymentGateway.CheckoutSession checkoutSession = paymentGateway.initiateCheckout(new PaymentGateway.CheckoutRequest(
                tenantId,
                tenant.getName(),
                customerName,
                customerEmail,
                requestedPlan,
                plan.getPrice(),
                plan.getCurrency(),
                gatewayReference,
                source));

        paymentTransactionRepository.save(PaymentTransaction.builder()
                .tenantId(tenantId)
                .subscriptionId(subscription.getId())
                .status(PaymentStatus.PENDING)
                .gatewayReference(checkoutSession.gatewayReference())
                .planCode(requestedPlan)
                .checkoutSource(source)
                .paymentProvider(paymentGateway.providerName())
                .gatewaySessionKey(checkoutSession.sessionKey())
                .gatewayStatus(checkoutSession.gatewayStatus())
                .amount(plan.getPrice())
                .currency(plan.getCurrency())
                .processedAt(Instant.now())
                .build());

        return CheckoutSessionResponse.builder()
                .paymentUrl(checkoutSession.paymentUrl())
                .gatewayReference(checkoutSession.gatewayReference())
                .provider(paymentGateway.providerName())
                .status(checkoutSession.gatewayStatus())
                .build();
    }

    @Transactional
    public String handleGatewaySuccess(Map<String, String> payload) {
        PaymentTransaction transaction = loadTransaction(payload);
        if (transaction.getStatus() == PaymentStatus.SUCCESS) {
            return buildResultRedirect(transaction, "success");
        }

        String validationReference = payload.get("val_id");
        if (validationReference == null || validationReference.isBlank()) {
            transaction.setStatus(PaymentStatus.FAILED);
            transaction.setGatewayStatus(payload.getOrDefault("status", "MISSING_VAL_ID"));
            paymentTransactionRepository.save(transaction);
            return buildResultRedirect(transaction, "failed");
        }

        PaymentGateway.ValidationResult validationResult = paymentGateway.validatePayment(
                new PaymentGateway.ValidationRequest(
                        transaction.getGatewayReference(),
                        validationReference,
                        transaction.getAmount(),
                        transaction.getCurrency(),
                        payload));

        transaction.setValidationReference(validationResult.validationReference());
        transaction.setGatewayStatus(validationResult.gatewayStatus());
        transaction.setProcessedAt(Instant.now());

        if (!validationResult.success()) {
            transaction.setStatus(PaymentStatus.FAILED);
            paymentTransactionRepository.save(transaction);
            return buildResultRedirect(transaction, "failed");
        }

        activateSubscription(transaction);
        transaction.setStatus(PaymentStatus.SUCCESS);
        paymentTransactionRepository.save(transaction);
        return buildResultRedirect(transaction, "success");
    }

    @Transactional
    public String handleGatewayFailure(Map<String, String> payload, PaymentStatus status) {
        PaymentTransaction transaction = loadTransaction(payload);
        if (transaction.getStatus() != PaymentStatus.SUCCESS) {
            transaction.setStatus(status);
            transaction.setGatewayStatus(payload.getOrDefault("status", status.name()));
            transaction.setProcessedAt(Instant.now());
            paymentTransactionRepository.save(transaction);
        }
        return buildResultRedirect(transaction, status == PaymentStatus.CANCELED ? "canceled" : "failed");
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

    private void activateSubscription(PaymentTransaction transaction) {
        Subscription subscription = subscriptionRepository.findById(transaction.getSubscriptionId())
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Subscription not found"));
        PlanDefinition plan = planCatalogService.getActivePlanEntity(transaction.getPlanCode());
        Instant now = Instant.now();
        subscription.setPlan(transaction.getPlanCode());
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        subscription.setCurrentPeriodStart(now);
        subscription.setCurrentPeriodEnd(now.plus(plan.getBillingCycleDays(), ChronoUnit.DAYS));
        subscriptionRepository.save(subscription);
    }

    private PaymentTransaction loadTransaction(Map<String, String> payload) {
        String gatewayReference = payload.get("tran_id");
        if (gatewayReference == null || gatewayReference.isBlank()) {
            throw new BusinessException(ErrorCode.PAYMENT_FAILED, "Missing transaction reference");
        }
        return paymentTransactionRepository.findByGatewayReference(gatewayReference)
                .orElseThrow(() -> new BusinessException(ErrorCode.PAYMENT_FAILED, "Unknown payment transaction"));
    }

    private String buildResultRedirect(PaymentTransaction transaction, String paymentStatus) {
        return UriComponentsBuilder.fromUriString(sslCommerzProperties.getCallbackBaseUrl())
                .path(sslCommerzProperties.getResultPagePath())
                .queryParam("planCode", transaction.getPlanCode())
                .queryParam("source", normalizeSource(transaction.getCheckoutSource()))
                .queryParam("paymentStatus", paymentStatus)
                .queryParam("gatewayReference", transaction.getGatewayReference())
                .build()
                .toUriString();
    }

    private String buildGatewayReference(String tenantId, SubscriptionPlan plan) {
        return "sslcz-" + tenantId + "-" + plan.name().toLowerCase() + "-" + UUID.randomUUID();
    }

    private String normalizeSource(String source) {
        return "onboarding".equalsIgnoreCase(source) ? "onboarding" : "settings";
    }

    private void enforceCheckoutRestrictions(Subscription currentSubscription, SubscriptionPlan requestedPlan) {
        SubscriptionStatus status = currentSubscription.getStatus();
        if (!ACTIVE_STATES.contains(status)) {
            return;
        }

        SubscriptionPlan currentPlan = currentSubscription.getPlan();
        if (currentPlan == requestedPlan) {
            throw new BusinessException(ErrorCode.INVALID_LIFECYCLE_TRANSITION,
                    "Requested plan is already active for this tenant");
        }

        if (!isUpgrade(currentPlan, requestedPlan)) {
            throw new BusinessException(ErrorCode.INVALID_LIFECYCLE_TRANSITION,
                    "Only plan upgrades are allowed for active or trial subscriptions");
        }
    }

    private boolean isUpgrade(SubscriptionPlan current, SubscriptionPlan requested) {
        return planRank(requested) > planRank(current);
    }

    private int planRank(SubscriptionPlan plan) {
        return switch (plan) {
            case BASIC -> 1;
            case PRO -> 2;
            case ENTERPRISE -> 3;
        };
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
