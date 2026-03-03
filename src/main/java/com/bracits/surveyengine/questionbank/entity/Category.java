package com.bracits.surveyengine.questionbank.entity;

import com.bracits.surveyengine.common.audit.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * A logical grouping of questions from the question bank.
 * Categories are assigned to surveys/campaigns.
 * <p>
 * SRS §4.2: "System shall allow creation of categories consisting of bank
 * questions."
 */
@Entity
@Table(name = "category")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Category extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

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
