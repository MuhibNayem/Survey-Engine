package com.bracits.surveyengine.common.audit.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method as auditable.
 * The AuditAspect will intercept methods annotated with exactly this to
 * generate Audit Logs.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Auditable {

    /**
     * The action to record in the audit log.
     * E.g., "CAMPAIGN_UPDATED", "TENANT_SUSPENDED", "LOGIN"
     */
    String action();
}
