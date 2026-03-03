package com.bracits.surveyengine.questionbank.entity;

import com.bracits.surveyengine.common.audit.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * A reusable question in the question bank.
 * Mutable until referenced by a published survey snapshot.
 * Edits after publish create a new version via {@link QuestionVersion}.
 * <p>
 * SRS §4.1, §4.2
 */
@Entity
@Table(name = "question")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Question extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "text", nullable = false, columnDefinition = "TEXT")
    private String text;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 30)
    private QuestionType type;

    @Column(name = "max_score", nullable = false, precision = 10, scale = 2)
    private BigDecimal maxScore;

    @Column(name = "current_version", nullable = false)
    private Integer currentVersion;

    @Column(name = "tenant_id", nullable = false)
    private String tenantId;

    @Column(name = "active", nullable = false)
    @Builder.Default
    private boolean active = true;

    @PrePersist
    void initVersion() {
        if (this.currentVersion == null) {
            this.currentVersion = 1;
        }
    }
}
