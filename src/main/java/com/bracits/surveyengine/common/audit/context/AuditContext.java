package com.bracits.surveyengine.common.audit.context;

import lombok.Builder;
import lombok.Getter;

/**
 * Thread-local context to hold audit information passed from the Aspect layer
 * down to the JPA EntityListener layer.
 */
@Getter
@Builder
public class AuditContext {
    private final String action;
    private final String actor;
    private final String tenantId;
    private final String ipAddress;
    private final String impersonatedBy;

    private static final ThreadLocal<AuditContext> CONTEXT = new ThreadLocal<>();

    public static void setContext(AuditContext context) {
        CONTEXT.set(context);
    }

    public static AuditContext getContext() {
        return CONTEXT.get();
    }

    public static void clearContext() {
        CONTEXT.remove();
    }
}
