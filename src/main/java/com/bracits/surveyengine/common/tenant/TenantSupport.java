package com.bracits.surveyengine.common.tenant;

import com.bracits.surveyengine.admin.context.TenantContext;
import com.bracits.surveyengine.common.exception.BusinessException;
import com.bracits.surveyengine.common.exception.ErrorCode;

public final class TenantSupport {

    private TenantSupport() {
    }

    public static String currentTenantOrDefault() {
        String tenantId = TenantContext.getTenantId();
        return (tenantId == null || tenantId.isBlank()) ? "default" : tenantId;
    }

    public static String requireCurrentTenant() {
        String tenantId = TenantContext.getTenantId();
        if (tenantId == null || tenantId.isBlank()) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED, "Tenant context is required");
        }
        return tenantId;
    }

    public static void ensureTenantAccess(String resourceTenantId) {
        String currentTenant = requireCurrentTenant();
        if (!currentTenant.equals(resourceTenantId)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED, "Cross-tenant access is not allowed");
        }
    }
}
