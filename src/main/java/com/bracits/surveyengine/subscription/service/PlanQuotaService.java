package com.bracits.surveyengine.subscription.service;

import com.bracits.surveyengine.admin.repository.AdminUserRepository;
import com.bracits.surveyengine.campaign.repository.CampaignRepository;
import com.bracits.surveyengine.common.exception.BusinessException;
import com.bracits.surveyengine.common.exception.ErrorCode;
import com.bracits.surveyengine.featuremanagement.entity.FeatureDefinition;
import com.bracits.surveyengine.featuremanagement.repository.FeatureDefinitionRepository;
import com.bracits.surveyengine.subscription.entity.PlanDefinition;
import com.bracits.surveyengine.subscription.entity.Subscription;
import com.bracits.surveyengine.subscription.entity.SubscriptionPlan;
import com.bracits.surveyengine.subscription.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class PlanQuotaService {

    private final SubscriptionRepository subscriptionRepository;
    private final PlanCatalogService planCatalogService;
    private final AdminUserRepository adminUserRepository;
    private final CampaignRepository campaignRepository;
    private final FeatureDefinitionRepository featureDefinitionRepository;

    @Transactional(readOnly = true)
    public void enforceAdminUserQuota(String tenantId) {
        PlanDefinition plan = resolvePlan(tenantId);
        if (plan.getMaxAdminUsers() == null) {
            return;
        }
        long current = adminUserRepository.countByTenantIdAndActiveTrue(tenantId);
        if (current >= plan.getMaxAdminUsers()) {
            throw new BusinessException(ErrorCode.QUOTA_EXCEEDED,
                    "Plan quota exceeded: max admin users=" + plan.getMaxAdminUsers());
        }
    }

    @Transactional(readOnly = true)
    public void enforceCampaignQuota(String tenantId) {
        PlanDefinition plan = resolvePlan(tenantId);
        if (plan.getMaxCampaigns() == null) {
            return;
        }
        long current = campaignRepository.countByTenantIdAndActiveTrue(tenantId);
        if (current >= plan.getMaxCampaigns()) {
            throw new BusinessException(ErrorCode.QUOTA_EXCEEDED,
                    "Plan quota exceeded: max active campaigns=" + plan.getMaxCampaigns());
        }
    }

    @Transactional(readOnly = true)
    public Integer getMaxResponsesPerCampaign(String tenantId) {
        return resolvePlan(tenantId).getMaxResponsesPerCampaign();
    }

    @Transactional(readOnly = true)
    public void enforceWeightProfileAccess(String tenantId) {
        if (!resolvePlan(tenantId).isWeightProfilesEnabled()) {
            throw new BusinessException(ErrorCode.QUOTA_EXCEEDED,
                    "Weight profiles are not available on your current plan");
        }
    }

    @Transactional(readOnly = true)
    public void enforceSignedTokenAccess(String tenantId) {
        if (!resolvePlan(tenantId).isSignedTokenEnabled()) {
            throw new BusinessException(ErrorCode.QUOTA_EXCEEDED,
                    "Signed launch tokens are not available on your current plan");
        }
    }

    @Transactional(readOnly = true)
    public void enforceSsoAccess(String tenantId) {
        if (!resolvePlan(tenantId).isSsoEnabled()) {
            throw new BusinessException(ErrorCode.QUOTA_EXCEEDED,
                    "SSO integration is not available on your current plan");
        }
    }

    @Transactional(readOnly = true)
    public void enforceCustomBrandingAccess(String tenantId) {
        if (!resolvePlan(tenantId).isCustomBrandingEnabled()) {
            throw new BusinessException(ErrorCode.QUOTA_EXCEEDED,
                    "Custom branding is not available on your current plan");
        }
    }

    @Transactional(readOnly = true)
    public void enforceDeviceFingerprintAccess(String tenantId) {
        if (!resolvePlan(tenantId).isDeviceFingerprintEnabled()) {
            throw new BusinessException(ErrorCode.QUOTA_EXCEEDED,
                    "Device fingerprint deduplication is not available on your current plan");
        }
    }

    @Transactional(readOnly = true)
    public void enforceApiAccess(String tenantId) {
        if (!resolvePlan(tenantId).isApiAccessEnabled()) {
            throw new BusinessException(ErrorCode.QUOTA_EXCEEDED,
                    "API access is not available on your current plan");
        }
    }

    // ========================================================================
    // Feature Management Integration
    // ========================================================================

    /**
     * Enforce feature access based on plan requirements.
     * Checks if the tenant's plan meets the minimum plan requirement for a feature.
     *
     * @param featureKey Feature key to check
     * @param tenantId   Tenant ID
     * @throws BusinessException if plan is insufficient
     */
    @Transactional(readOnly = true)
    public void enforceFeatureAccess(String featureKey, String tenantId) {
        FeatureDefinition feature = featureDefinitionRepository.findByFeatureKey(featureKey)
            .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                "Feature not found: " + featureKey));

        SubscriptionPlan userPlan = resolvePlan(tenantId).getPlanCode();
        SubscriptionPlan requiredPlan = feature.getMinPlan();

        if (!isPlanSufficient(userPlan, requiredPlan)) {
            log.warn("Feature {} requires plan {} but tenant {} has {}", 
                featureKey, requiredPlan, tenantId, userPlan);
            throw new BusinessException(ErrorCode.QUOTA_EXCEEDED,
                String.format("Feature '%s' requires %s plan or higher. Your current plan: %s",
                    feature.getName(), requiredPlan, userPlan));
        }

        // Check if feature is globally enabled
        if (!feature.isEnabled()) {
            log.warn("Feature {} is globally disabled", featureKey);
            throw new BusinessException(ErrorCode.FEATURE_DISABLED,
                "Feature '" + feature.getName() + "' is currently disabled");
        }
    }

    /**
     * Check if a feature is available for a tenant based on plan.
     *
     * @param featureKey Feature key to check
     * @param tenantId   Tenant ID
     * @return true if feature is available
     */
    @Transactional(readOnly = true)
    public boolean isFeatureAvailableForPlan(String featureKey, String tenantId) {
        try {
            enforceFeatureAccess(featureKey, tenantId);
            return true;
        } catch (BusinessException e) {
            return false;
        }
    }

    /**
     * Get all feature keys available for a tenant's plan.
     *
     * @param tenantId Tenant ID
     * @return List of available feature keys
     */
    @Transactional(readOnly = true)
    public java.util.List<String> getAvailableFeatureKeys(String tenantId) {
        SubscriptionPlan userPlan = resolvePlan(tenantId).getPlanCode();
        
        return featureDefinitionRepository.findAll().stream()
            .filter(f -> isPlanSufficient(userPlan, f.getMinPlan()))
            .filter(FeatureDefinition::isEnabled)
            .map(FeatureDefinition::getFeatureKey)
            .toList();
    }

    /**
     * Check if user's plan is sufficient for feature access.
     */
    private boolean isPlanSufficient(SubscriptionPlan userPlan, SubscriptionPlan requiredPlan) {
        int userLevel = getPlanLevel(userPlan);
        int requiredLevel = getPlanLevel(requiredPlan);
        return userLevel >= requiredLevel;
    }

    /**
     * Get numeric level for plan comparison.
     */
    private int getPlanLevel(SubscriptionPlan plan) {
        return switch (plan) {
            case BASIC -> 1;
            case PRO -> 2;
            case ENTERPRISE -> 3;
        };
    }

    private PlanDefinition resolvePlan(String tenantId) {
        Subscription subscription = subscriptionRepository.findByTenantId(tenantId).orElse(null);
        SubscriptionPlan planCode = subscription != null ? subscription.getPlan() : SubscriptionPlan.BASIC;
        return planCatalogService.getActivePlanEntity(planCode);
    }
}
