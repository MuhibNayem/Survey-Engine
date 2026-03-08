package com.bracits.surveyengine.common.audit.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
@AllArgsConstructor
public class AuditLogResponse {
    private Long id;
    private String tenantId;
    private String entityType;
    private String entityId;
    private String action;
    private String actor;
    private String reason;
    private String beforeValue;
    private String afterValue;
    private String ipAddress;
    private Instant createdAt;
}
