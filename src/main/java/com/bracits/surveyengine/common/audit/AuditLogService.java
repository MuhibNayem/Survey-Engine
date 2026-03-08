package com.bracits.surveyengine.common.audit;

/**
 * Service contract for recording business audit events.
 */
public interface AuditLogService {

        /**
         * Record a full audit event with before/after state.
         */
        AuditLog record(String tenantId, String entityType, String entityId, String action,
                        String actor, String reason,
                        String beforeValue, String afterValue,
                        String ipAddress);

        /**
         * Record an audit event with before/after state, auto-resolving tenant from
         * context.
         */
        AuditLog record(String entityType, String entityId, String action,
                        String actor, String reason,
                        String beforeValue, String afterValue);

        /**
         * Record a simple audit event (no before/after), auto-resolving tenant.
         */
        AuditLog record(String entityType, String entityId, String action,
                        String actor, String reason);
}
