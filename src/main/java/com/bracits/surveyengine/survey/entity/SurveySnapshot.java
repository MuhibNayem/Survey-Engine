package com.bracits.surveyengine.survey.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

/**
 * Immutable frozen copy of a survey at publish time.
 * <p>
 * SRS §4.2, §4.6: "When an admin publishes a survey, the survey shall reference
 * immutable tagged versions (snapshot) of categories and questions."
 * <p>
 * The snapshotData JSON contains the full survey structure (pages, questions,
 * categories, skip logic) at the moment of publishing.
 */
@Entity
@Table(name = "survey_snapshot", uniqueConstraints = {
        @UniqueConstraint(name = "uk_survey_snapshot", columnNames = { "survey_id", "version_number" })
})
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SurveySnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "survey_id", nullable = false)
    private UUID surveyId;

    @Column(name = "version_number", nullable = false)
    private Integer versionNumber;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "snapshot_data", nullable = false, columnDefinition = "jsonb")
    private String snapshotData;

    @Column(name = "published_by", nullable = false)
    private String publishedBy;

    @Column(name = "published_at", nullable = false, updatable = false)
    private Instant publishedAt;

    @PrePersist
    void prePersist() {
        if (this.publishedAt == null) {
            this.publishedAt = Instant.now();
        }
    }
}
