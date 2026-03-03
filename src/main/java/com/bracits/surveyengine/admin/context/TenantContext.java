package com.bracits.surveyengine.admin.context;

import java.util.Set;

/**
 * Holds the authenticated admin's tenant context for the current request.
 * Populated by the JWT filter, consumed by services and repos.
 */
public class TenantContext {

    private static final ThreadLocal<TenantInfo> CONTEXT = new ThreadLocal<>();

    public static void set(TenantInfo info) {
        CONTEXT.set(info);
    }

    public static TenantInfo get() {
        return CONTEXT.get();
    }

    public static void clear() {
        CONTEXT.remove();
    }

    public static String getTenantId() {
        TenantInfo info = CONTEXT.get();
        return info != null ? info.tenantId() : null;
    }

    public static String getUserId() {
        TenantInfo info = CONTEXT.get();
        return info != null ? info.userId() : null;
    }

    public static Set<String> getRoles() {
        TenantInfo info = CONTEXT.get();
        return info != null ? info.roles() : Set.of();
    }

    public record TenantInfo(
            String tenantId,
            String userId,
            String email,
            Set<String> roles) {
    }
}
