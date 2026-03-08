package com.bracits.surveyengine.campaign.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * A dynamic, admin-configurable data collection field linked to campaign settings.
 * Replaces the old hardcoded boolean flags (collectName, collectEmail, etc.).
 */
@Entity
@Table(name = "data_collection_field")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DataCollectionField {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campaign_settings_id", nullable = false)
    private CampaignSettings campaignSettings;

    @Column(name = "field_key", nullable = false, length = 50)
    private String fieldKey;

    @Column(name = "label", nullable = false, length = 100)
    private String label;

    @Enumerated(EnumType.STRING)
    @Column(name = "field_type", nullable = false, length = 20)
    @Builder.Default
    private DataCollectionFieldType fieldType = DataCollectionFieldType.TEXT;

    @Column(name = "required", nullable = false)
    @Builder.Default
    private boolean required = false;

    @Column(name = "sort_order", nullable = false)
    @Builder.Default
    private int sortOrder = 0;

    @Column(name = "enabled", nullable = false)
    @Builder.Default
    private boolean enabled = true;
}
