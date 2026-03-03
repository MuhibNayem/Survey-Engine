package com.bracits.surveyengine.common.audit;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;

/**
 * Immutable audit log entry for business events.
 * Covers: survey publish, campaign activation, weight profile changes,
 * auth profile CRUD, claim mapping changes, lifecycle transitions, reopen
 * events.
 */
@Entity
@Table(name = "audit_log", indexes = {
        @Index(name = "idx_audit_log_entity", columnList = "entityType, entityId"),
        @Index(name = "idx_audit_log_actor", columnList = "actor"),
        @Index(name = "idx_audit_log_created_at", columnList = "createdAt")
})
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "entity_type", nullable = false, length = 100)
    private String entityType;

    @Column(name = "entity_id", nullable = false)
    private String entityId;

    @Column(name = "action", nullable = false, length = 50)
    private String action;

    @Column(name = "actor", nullable = false)
    private String actor;

    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "before_value", columnDefinition = "jsonb")
    private String beforeValue;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "after_value", columnDefinition = "jsonb")
    private String afterValue;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = Instant.now();
        }
    }
}
