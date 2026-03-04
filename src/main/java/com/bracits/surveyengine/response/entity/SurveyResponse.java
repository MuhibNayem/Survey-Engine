package com.bracits.surveyengine.response.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * A respondent's submission for a campaign.
 * <p>
 * SRS §4.7: "System shall collect and store responses for each campaign."
 * SRS §8: "Responses shall be locked upon submission."
 */
@Entity
@Table(name = "survey_response")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SurveyResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "campaign_id", nullable = false)
    private UUID campaignId;

    @Column(name = "survey_snapshot_id", nullable = false)
    private UUID surveySnapshotId;

    @Column(name = "tenant_id", nullable = false)
    private String tenantId;

    @Column(name = "respondent_identifier")
    private String respondentIdentifier;

    @Column(name = "respondent_ip", length = 45)
    private String respondentIp;

    @Column(name = "respondent_device_fingerprint")
    private String respondentDeviceFingerprint;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    @Builder.Default
    private ResponseStatus status = ResponseStatus.IN_PROGRESS;

    @Column(name = "started_at", nullable = false, updatable = false)
    private Instant startedAt;

    @Column(name = "submitted_at")
    private Instant submittedAt;

    @Column(name = "locked_at")
    private Instant lockedAt;

    @Column(name = "weight_profile_id")
    private UUID weightProfileId;

    @Column(name = "weighted_total_score", precision = 10, scale = 4)
    private BigDecimal weightedTotalScore;

    @Column(name = "scored_at")
    private Instant scoredAt;

    @OneToMany(mappedBy = "surveyResponse", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Answer> answers = new ArrayList<>();

    @PrePersist
    void prePersist() {
        if (this.startedAt == null) {
            this.startedAt = Instant.now();
        }
    }
}
