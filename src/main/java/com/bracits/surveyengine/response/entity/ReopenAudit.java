package com.bracits.surveyengine.response.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

/**
 * Captures the full audit trail when a locked response is reopened.
 * <p>
 * SRS §8: "Admin-only reopen with reason, actor, and time window."
 */
@Entity
@Table(name = "reopen_audit")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ReopenAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "survey_response_id", nullable = false)
    private UUID surveyResponseId;

    @Column(name = "reopened_by", nullable = false)
    private String reopenedBy;

    @Column(name = "reason", nullable = false, columnDefinition = "TEXT")
    private String reason;

    @Column(name = "reopened_at", nullable = false, updatable = false)
    private Instant reopenedAt;

    @Column(name = "reopen_window_minutes")
    private Integer reopenWindowMinutes;

    @PrePersist
    void prePersist() {
        if (this.reopenedAt == null) {
            this.reopenedAt = Instant.now();
        }
    }
}
