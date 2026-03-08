package com.bracits.surveyengine.common.audit.controller;

import com.bracits.surveyengine.admin.context.TenantContext;
import com.bracits.surveyengine.common.audit.AuditLog;
import com.bracits.surveyengine.common.audit.AuditLogRepository;
import com.bracits.surveyengine.common.audit.dto.AuditLogResponse;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditLogRepository auditLogRepository;

    /**
     * Tenant-scoped audit logs (for tenant admins viewing their own activity).
     */
    @GetMapping("/api/v1/audit-logs")
    public ResponseEntity<Page<AuditLogResponse>> getTenantAuditLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String entityType,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to) {

        String tenantId = TenantContext.getTenantId();

        Specification<AuditLog> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("tenantId"), tenantId));

            if (action != null && !action.isBlank()) {
                predicates.add(cb.equal(root.get("action"), action));
            }
            if (entityType != null && !entityType.isBlank()) {
                predicates.add(cb.equal(root.get("entityType"), entityType));
            }
            if (from != null && !from.isBlank()) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), Instant.parse(from)));
            }
            if (to != null && !to.isBlank()) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), Instant.parse(to)));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<AuditLogResponse> result = auditLogRepository.findAll(spec, pageRequest).map(this::toResponse);
        return ResponseEntity.ok(result);
    }

    /**
     * Platform-wide audit logs (Super Admin only).
     */
    @GetMapping("/api/v1/admin/superadmin/audit-logs")
    public ResponseEntity<Page<AuditLogResponse>> getPlatformAuditLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String entityType,
            @RequestParam(required = false) String tenantId,
            @RequestParam(required = false) String actor,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to) {

        Specification<AuditLog> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (tenantId != null && !tenantId.isBlank()) {
                predicates.add(cb.equal(root.get("tenantId"), tenantId));
            }
            if (action != null && !action.isBlank()) {
                predicates.add(cb.equal(root.get("action"), action));
            }
            if (entityType != null && !entityType.isBlank()) {
                predicates.add(cb.equal(root.get("entityType"), entityType));
            }
            if (actor != null && !actor.isBlank()) {
                predicates.add(cb.equal(root.get("actor"), actor));
            }
            if (from != null && !from.isBlank()) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), Instant.parse(from)));
            }
            if (to != null && !to.isBlank()) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), Instant.parse(to)));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<AuditLogResponse> result = auditLogRepository.findAll(spec, pageRequest).map(this::toResponse);
        return ResponseEntity.ok(result);
    }

    private AuditLogResponse toResponse(AuditLog log) {
        return AuditLogResponse.builder()
                .id(log.getId())
                .tenantId(log.getTenantId())
                .entityType(log.getEntityType())
                .entityId(log.getEntityId())
                .action(log.getAction())
                .actor(log.getActor())
                .reason(log.getReason())
                .beforeValue(log.getBeforeValue())
                .afterValue(log.getAfterValue())
                .ipAddress(log.getIpAddress())
                .createdAt(log.getCreatedAt())
                .build();
    }
}
