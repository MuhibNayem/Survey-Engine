package com.bracits.surveyengine.subscription.service;

import com.bracits.surveyengine.common.exception.ResourceNotFoundException;
import com.bracits.surveyengine.subscription.dto.PlanDefinitionRequest;
import com.bracits.surveyengine.subscription.dto.PlanDefinitionResponse;
import com.bracits.surveyengine.subscription.entity.PlanDefinition;
import com.bracits.surveyengine.subscription.entity.SubscriptionPlan;
import com.bracits.surveyengine.subscription.repository.PlanDefinitionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PlanCatalogService {

    private final PlanDefinitionRepository planDefinitionRepository;

    @Transactional(readOnly = true)
    public List<PlanDefinitionResponse> listActivePlans() {
        return planDefinitionRepository.findByActiveTrueOrderByPriceAsc().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public PlanDefinition getActivePlanEntity(SubscriptionPlan planCode) {
        return planDefinitionRepository.findByPlanCodeAndActiveTrue(planCode)
                .orElseThrow(() -> new ResourceNotFoundException("PlanDefinition", planCode.name()));
    }

    @Transactional
    public PlanDefinitionResponse upsert(PlanDefinitionRequest request) {
        PlanDefinition plan = planDefinitionRepository.findByPlanCode(request.getPlanCode())
                .orElse(PlanDefinition.builder().planCode(request.getPlanCode()).build());

        plan.setDisplayName(request.getDisplayName());
        plan.setPrice(request.getPrice());
        plan.setCurrency(request.getCurrency());
        plan.setBillingCycleDays(request.getBillingCycleDays());
        plan.setTrialDays(request.getTrialDays());
        plan.setMaxCampaigns(request.getMaxCampaigns());
        plan.setMaxResponsesPerCampaign(request.getMaxResponsesPerCampaign());
        plan.setMaxAdminUsers(request.getMaxAdminUsers());
        plan.setWeightProfilesEnabled(Boolean.TRUE.equals(request.getWeightProfilesEnabled()));
        plan.setSignedTokenEnabled(Boolean.TRUE.equals(request.getSignedTokenEnabled()));
        plan.setSsoEnabled(Boolean.TRUE.equals(request.getSsoEnabled()));
        plan.setCustomBrandingEnabled(Boolean.TRUE.equals(request.getCustomBrandingEnabled()));
        plan.setDeviceFingerprintEnabled(Boolean.TRUE.equals(request.getDeviceFingerprintEnabled()));
        plan.setApiAccessEnabled(Boolean.TRUE.equals(request.getApiAccessEnabled()));
        plan.setActive(request.getActive() == null || request.getActive());

        return toResponse(planDefinitionRepository.save(plan));
    }

    private PlanDefinitionResponse toResponse(PlanDefinition p) {
        return PlanDefinitionResponse.builder()
                .id(p.getId())
                .planCode(p.getPlanCode())
                .displayName(p.getDisplayName())
                .price(p.getPrice())
                .currency(p.getCurrency())
                .billingCycleDays(p.getBillingCycleDays())
                .trialDays(p.getTrialDays())
                .maxCampaigns(p.getMaxCampaigns())
                .maxResponsesPerCampaign(p.getMaxResponsesPerCampaign())
                .maxAdminUsers(p.getMaxAdminUsers())
                .weightProfilesEnabled(p.isWeightProfilesEnabled())
                .signedTokenEnabled(p.isSignedTokenEnabled())
                .ssoEnabled(p.isSsoEnabled())
                .customBrandingEnabled(p.isCustomBrandingEnabled())
                .deviceFingerprintEnabled(p.isDeviceFingerprintEnabled())
                .apiAccessEnabled(p.isApiAccessEnabled())
                .active(p.isActive())
                .features(p.getFeatures().stream()
                        .map(this::toFeatureResponse)
                        .sorted(Comparator.comparing(com.bracits.surveyengine.subscription.dto.FeatureResponse::getDisplayOrder))
                        .toList())
                .build();
    }

    private com.bracits.surveyengine.subscription.dto.FeatureResponse toFeatureResponse(com.bracits.surveyengine.subscription.entity.PlanFeature f) {
        return com.bracits.surveyengine.subscription.dto.FeatureResponse.builder()
                .featureCode(f.getFeatureCode())
                .category(f.getCategory())
                .name(f.getName())
                .description(f.getDescription())
                .displayOrder(f.getDisplayOrder())
                .build();
    }
}
