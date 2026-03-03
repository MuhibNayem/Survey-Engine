package com.bracits.surveyengine.common.audit;

import com.bracits.surveyengine.common.audit.AuditLog;

/**
 * Service contract for recording business audit events.
 */
public interface AuditLogService {

    /**
     * Record an audit event with before/after state.
     */
    AuditLog record(String entityType, String entityId, String action,
            String actor, String reason,
            String beforeValue, String afterValue);

    /**
     * Record a simple audit event without before/after values.
     */
    AuditLog record(String entityType, String entityId, String action,
            String actor, String reason);
}
