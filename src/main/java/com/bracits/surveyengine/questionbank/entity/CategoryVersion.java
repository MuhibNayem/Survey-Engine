package com.bracits.surveyengine.questionbank.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Immutable versioned snapshot of a {@link Category} and its question mappings.
 * <p>
 * SRS §4.2: "Category definitions shall be versioned. When an admin publishes a
 * survey,
 * the survey shall reference immutable tagged versions (snapshot) of categories
 * and questions."
 */
@Entity
@Table(name = "category_version", uniqueConstraints = {
        @UniqueConstraint(name = "uk_category_version", columnNames = { "category_id", "version_number" })
})
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CategoryVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "category_id", nullable = false)
    private UUID categoryId;

    @Column(name = "version_number", nullable = false)
    private Integer versionNumber;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @OneToMany(mappedBy = "categoryVersion", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CategoryQuestionMapping> questionMappings = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = Instant.now();
        }
    }
}
