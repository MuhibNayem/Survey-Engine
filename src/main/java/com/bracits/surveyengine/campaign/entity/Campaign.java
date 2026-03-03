package com.bracits.surveyengine.campaign.entity;

import com.bracits.surveyengine.common.audit.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * A campaign ties a published survey to distribution channels, settings, and
 * respondents.
 * <p>
 * SRS §4.5, §4.6: Campaigns contain survey settings, auth mode, and
 * distribution config.
 */
@Entity
@Table(name = "campaign")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Campaign extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "name", nullable = false, length = 500)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "survey_id", nullable = false)
    private UUID surveyId;

    @Column(name = "survey_snapshot_id")
    private UUID surveySnapshotId;

    @Enumerated(EnumType.STRING)
    @Column(name = "auth_mode", nullable = false, length = 30)
    @Builder.Default
    private AuthMode authMode = AuthMode.PUBLIC;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    @Builder.Default
    private CampaignStatus status = CampaignStatus.DRAFT;

    @Column(name = "active", nullable = false)
    @Builder.Default
    private boolean active = true;
}
