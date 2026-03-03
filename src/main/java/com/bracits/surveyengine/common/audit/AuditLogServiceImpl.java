package com.bracits.surveyengine.common.audit;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of {@link AuditLogService}.
 */
@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;

    @Override
    @Transactional
    public AuditLog record(String entityType, String entityId, String action,
            String actor, String reason,
            String beforeValue, String afterValue) {
        AuditLog entry = AuditLog.builder()
                .entityType(entityType)
                .entityId(entityId)
                .action(action)
                .actor(actor)
                .reason(reason)
                .beforeValue(ensureValidJson(beforeValue))
                .afterValue(ensureValidJson(afterValue))
                .build();
        return auditLogRepository.save(entry);
    }

    @Override
    @Transactional
    public AuditLog record(String entityType, String entityId, String action,
            String actor, String reason) {
        return record(entityType, entityId, action, actor, reason, null, null);
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
