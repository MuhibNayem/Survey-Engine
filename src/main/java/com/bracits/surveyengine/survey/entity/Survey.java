package com.bracits.surveyengine.survey.entity;

import com.bracits.surveyengine.common.audit.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Root survey entity — the authoring container for pages and questions.
 * <p>
 * SRS §4.1: "System shall allow admin to design surveys with pages, questions,
 * mandatory flags, and skip logic."
 * <p>
 * SRS §4.6: Survey follows lifecycle: Draft → Published → Closed →
 * Results Published → Archived.
 */
@Entity
@Table(name = "survey")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Survey extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "title", nullable = false, length = 500)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "lifecycle_state", nullable = false, length = 30)
    @Builder.Default
    private SurveyLifecycleState lifecycleState = SurveyLifecycleState.DRAFT;

    @Column(name = "current_version", nullable = false)
    @Builder.Default
    private Integer currentVersion = 1;

    @Column(name = "tenant_id", nullable = false)
    private String tenantId;

    @OneToMany(mappedBy = "survey", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sortOrder ASC")
    @Builder.Default
    private List<SurveyPage> pages = new ArrayList<>();

    @Column(name = "active", nullable = false)
    @Builder.Default
    private boolean active = true;
}
