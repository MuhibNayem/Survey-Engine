package com.bracits.surveyengine.featuremanagement.entity;

import com.bracits.surveyengine.common.audit.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Tracks user-level feature access and completion status.
 * Used for tour progress, tooltip dismissals, and feature usage analytics.
 */
@Entity
@Table(name = "user_feature_access", indexes = {
    @Index(name = "idx_user_feature_access_user_id", columnList = "user_id"),
    @Index(name = "idx_user_feature_access_tenant_id", columnList = "tenant_id"),
    @Index(name = "idx_user_feature_access_feature_id", columnList = "feature_id"),
    @Index(name = "idx_user_feature_access_completed", columnList = "completed"),
    @Index(name = "idx_user_feature_access_accessed", columnList = "accessed"),
    @Index(name = "idx_user_feature_access_last_accessed", columnList = "last_accessed_at")
}, uniqueConstraints = {
    @UniqueConstraint(name = "uk_user_feature_access_user_feature", columnNames = {"user_id", "feature_id"})
})
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserFeatureAccess extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Reference to admin user.
     */
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    /**
     * Tenant identifier for scoping.
     */
    @Column(name = "tenant_id", nullable = false, length = 255)
    private String tenantId;

    /**
     * Reference to the feature definition.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "feature_id", nullable = false, foreignKey = @ForeignKey(name = "fk_user_feature_access_feature"))
    private FeatureDefinition feature;

    /**
     * Whether user has accessed/seen the feature.
     */
    @Column(name = "accessed", nullable = false)
    @Builder.Default
    private boolean accessed = false;

    /**
     * Whether user has completed/dismissed the feature.
     */
    @Column(name = "completed", nullable = false)
    @Builder.Default
    private boolean completed = false;

    /**
     * Number of times user has accessed the feature.
     */
    @Column(name = "access_count", nullable = false)
    @Builder.Default
    private Integer accessCount = 0;

    /**
     * Timestamp of last access.
     */
    @Column(name = "last_accessed_at")
    private Instant lastAccessedAt;

    /**
     * Timestamp when feature was completed.
     */
    @Column(name = "completed_at")
    private Instant completedAt;

    /**
     * Additional access-specific metadata.
     * Can store step progress for tours, dismissal reason for tooltips, etc.
     */
    @Column(name = "metadata", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    @Builder.Default
    private Map<String, Object> metadata = new HashMap<>();

    // ------------------------------------------------------------------------
    // Helper Methods
    // ------------------------------------------------------------------------

    /**
     * Record feature access.
     * Increments access count and updates last accessed timestamp.
     */
    public void recordAccess() {
        this.accessed = true;
        this.accessCount = (this.accessCount != null ? this.accessCount : 0) + 1;
        this.lastAccessedAt = Instant.now();
    }

    /**
     * Mark feature as completed.
     */
    public void markCompleted() {
        this.completed = true;
        this.completedAt = Instant.now();
    }

    /**
     * Reset feature completion status.
     */
    public void reset() {
        this.accessed = false;
        this.completed = false;
        this.accessCount = 0;
        this.lastAccessedAt = null;
        this.completedAt = null;
        this.metadata = new HashMap<>();
    }

    /**
     * Get a metadata value by key.
     */
    @SuppressWarnings("unchecked")
    public <T> T getMetadataValue(String key) {
        return metadata != null ? (T) metadata.get(key) : null;
    }

    /**
     * Set a metadata value.
     */
    public void setMetadataValue(String key, Object value) {
        if (metadata == null) {
            metadata = new HashMap<>();
        }
        metadata.put(key, value);
    }

    /**
     * Get current step for tour features.
     */
    public Integer getCurrentTourStep() {
        if (metadata == null) {
            return 0;
        }
        Object step = metadata.get("currentStep");
        return step instanceof Number ? ((Number) step).intValue() : 0;
    }

    /**
     * Set current step for tour features.
     */
    public void setCurrentTourStep(Integer step) {
        if (metadata == null) {
            metadata = new HashMap<>();
        }
        metadata.put("currentStep", step);
    }

    /**
     * Check if feature should be shown to user.
     */
    public boolean shouldShow() {
        // Don't show if completed
        if (completed) {
            return false;
        }
        
        // Show if never accessed
        if (!accessed) {
            return true;
        }
        
        // Show if accessed but not completed (e.g., user skipped tour)
        return !completed;
    }
}
