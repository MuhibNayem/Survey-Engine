package com.bracits.surveyengine.subscription.service;

import com.bracits.surveyengine.admin.repository.AdminUserRepository;
import com.bracits.surveyengine.campaign.repository.CampaignRepository;
import com.bracits.surveyengine.common.exception.BusinessException;
import com.bracits.surveyengine.common.exception.ErrorCode;
import com.bracits.surveyengine.subscription.entity.PlanDefinition;
import com.bracits.surveyengine.subscription.entity.Subscription;
import com.bracits.surveyengine.subscription.entity.SubscriptionPlan;
import com.bracits.surveyengine.subscription.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PlanQuotaService {

    private final SubscriptionRepository subscriptionRepository;
    private final PlanCatalogService planCatalogService;
    private final AdminUserRepository adminUserRepository;
    private final CampaignRepository campaignRepository;

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

    private PlanDefinition resolvePlan(String tenantId) {
        Subscription subscription = subscriptionRepository.findByTenantId(tenantId).orElse(null);
        SubscriptionPlan planCode = subscription != null ? subscription.getPlan() : SubscriptionPlan.BASIC;
        return planCatalogService.getActivePlanEntity(planCode);
    }
}
