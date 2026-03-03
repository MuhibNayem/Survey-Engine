package com.bracits.surveyengine.questionbank.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Immutable versioned snapshot of a {@link Question}.
 * Created whenever a question is snapshotted for a published survey.
 * <p>
 * SRS §4.2: "Question bank items shall be versioned. Post-publish edits to
 * bank questions shall create new versions and must not mutate already-tagged
 * survey content."
 */
@Entity
@Table(name = "question_version", uniqueConstraints = {
        @UniqueConstraint(name = "uk_question_version", columnNames = { "question_id", "version_number" })
})
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class QuestionVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "question_id", nullable = false)
    private UUID questionId;

    @Column(name = "version_number", nullable = false)
    private Integer versionNumber;

    @Column(name = "text", nullable = false, columnDefinition = "TEXT")
    private String text;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 30)
    private QuestionType type;

    @Column(name = "max_score", nullable = false, precision = 10, scale = 2)
    private BigDecimal maxScore;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = Instant.now();
        }
    }
}
