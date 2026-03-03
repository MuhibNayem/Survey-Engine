package com.bracits.surveyengine.scoring.entity;

import com.bracits.surveyengine.common.audit.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * A weight profile defines how category scores are weighted for a campaign.
 * <p>
 * SRS §5.1: "A campaign shall have one or more weight profiles.
 * Each profile assigns a percentage weight to each category."
 */
@Entity
@Table(name = "weight_profile")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class WeightProfile extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "campaign_id", nullable = false)
    private UUID campaignId;

    @Column(name = "tenant_id", nullable = false)
    private String tenantId;

    @Column(name = "active", nullable = false)
    @Builder.Default
    private boolean active = true;

    @OneToMany(mappedBy = "weightProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CategoryWeight> categoryWeights = new ArrayList<>();
}
