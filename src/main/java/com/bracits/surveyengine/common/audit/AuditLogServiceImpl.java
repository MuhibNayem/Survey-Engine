package com.bracits.surveyengine.common.audit;

import com.bracits.surveyengine.admin.context.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of {@link AuditLogService}.
 */
@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public AuditLog record(String tenantId, String entityType, String entityId, String action,
            String actor, String reason,
            String beforeValue, String afterValue,
            String ipAddress) {
        AuditLog entry = AuditLog.builder()
                .tenantId(tenantId)
                .entityType(entityType)
                .entityId(entityId)
                .action(action)
                .actor(actor)
                .reason(reason)
                .beforeValue(ensureValidJson(beforeValue))
                .afterValue(ensureValidJson(afterValue))
                .ipAddress(ipAddress)
                .build();
        return auditLogRepository.save(entry);
    }

    @Override
    @Transactional
    public AuditLog record(String entityType, String entityId, String action,
            String actor, String reason,
            String beforeValue, String afterValue) {
        return record(TenantContext.getTenantId(), entityType, entityId, action,
                actor, reason, beforeValue, afterValue, null);
    }

    @Override
    @Transactional
    public AuditLog record(String entityType, String entityId, String action,
            String actor, String reason) {
        return record(TenantContext.getTenantId(), entityType, entityId, action,
                actor, reason, null, null, null);
    }

    /**
     * Ensures the value is valid JSON for JSONB storage.
     * Wraps plain strings as JSON strings; passes through already-valid JSON.
     */
    private String ensureValidJson(String value) {
        if (value == null)
            return null;
        String trimmed = value.trim();
        if (trimmed.startsWith("{") || trimmed.startsWith("[") || trimmed.startsWith("\"")) {
            return value;
        }
        // Wrap plain string as a JSON string value
        return "\"" + value.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }
}
