package com.bracits.surveyengine.featuremanagement.entity;

import com.bracits.surveyengine.admin.entity.AdminRole;
import com.bracits.surveyengine.common.audit.AuditableEntity;
import com.bracits.surveyengine.subscription.entity.SubscriptionPlan;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.*;

/**
 * Master entity for feature definitions.
 * Stores complete metadata about features including tours, tooltips, banners, and feature flags.
 */
@Entity
@Table(name = "feature_definition", indexes = {
    @Index(name = "idx_feature_definition_category", columnList = "category"),
    @Index(name = "idx_feature_definition_enabled", columnList = "enabled"),
    @Index(name = "idx_feature_definition_min_plan", columnList = "min_plan"),
    @Index(name = "idx_feature_definition_type", columnList = "feature_type")
})
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class FeatureDefinition extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Unique identifier for the feature (e.g., "tour.dashboard", "tooltip.survey.create").
     * Used as the primary key for API operations.
     */
    @Column(name = "feature_key", nullable = false, unique = true, length = 100)
    private String featureKey;

    /**
     * Type of feature: TOUR, TOOLTIP, BANNER, FEATURE_FLAG, ANNOUNCEMENT.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "feature_type", nullable = false, length = 50)
    @Builder.Default
    private FeatureType featureType = FeatureType.FEATURE_FLAG;

    /**
     * Functional category for grouping and filtering.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 100)
    @Builder.Default
    private FeatureCategory category = FeatureCategory.GENERAL;

    /**
     * Human-readable name for the feature.
     */
    @Column(name = "name", nullable = false, length = 255)
    private String name;

    /**
     * Detailed description of the feature.
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * Global enable/disable flag.
     * When false, feature is unavailable to all users regardless of other settings.
     */
    @Column(name = "enabled", nullable = false)
    @Builder.Default
    private boolean enabled = true;

    /**
     * Percentage of users who should see this feature (0-100).
     * Used for gradual rollouts and A/B testing.
     */
    @Column(name = "rollout_percentage", nullable = false)
    @Builder.Default
    private Integer rolloutPercentage = 100;

    /**
     * Minimum subscription plan required to access this feature.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "min_plan", nullable = false, length = 50)
    @Builder.Default
    private SubscriptionPlan minPlan = SubscriptionPlan.BASIC;

    /**
     * Roles that can access this feature.
     * Stored as JSONB array of role names.
     */
    @Column(name = "roles", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    @Builder.Default
    private List<String> roles = new ArrayList<>();

    /**
     * Platforms where feature is available (WEB, MOBILE, etc.).
     * Stored as JSONB array of platform names.
     */
    @Column(name = "platforms", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    @Builder.Default
    private List<String> platforms = new ArrayList<>();

    /**
     * Additional feature-specific metadata.
     * For tours: contains step definitions.
     * For tooltips: contains content and placement.
     * For feature flags: contains configuration options.
     */
    @Column(name = "metadata", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    @Builder.Default
    private Map<String, Object> metadata = new HashMap<>();

    // ------------------------------------------------------------------------
    // Helper Methods
    // ------------------------------------------------------------------------

    /**
     * Check if a role can access this feature.
     */
    public boolean isRoleAllowed(AdminRole role) {
        if (roles == null || roles.isEmpty()) {
            return true;
        }
        return roles.contains(role.name());
    }

    /**
     * Check if a platform is supported.
     */
    public boolean isPlatformSupported(String platform) {
        if (platforms == null || platforms.isEmpty()) {
            return true;
        }
        return platforms.contains(platform.toUpperCase());
    }

    /**
     * Check if user is within rollout percentage based on user ID hash.
     */
    public boolean isInRollout(UUID userId) {
        if (rolloutPercentage == null || rolloutPercentage >= 100) {
            return true;
        }
        if (userId == null) {
            return false;
        }
        // Use hash code for deterministic rollout
        int hash = Math.abs(userId.hashCode() % 100);
        return hash < rolloutPercentage;
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
     * Add a role to the allowed roles list.
     */
    public void addRole(AdminRole role) {
        if (roles == null) {
            roles = new ArrayList<>();
        }
        if (!roles.contains(role.name())) {
            roles.add(role.name());
        }
    }

    /**
     * Remove a role from the allowed roles list.
     */
    public void removeRole(AdminRole role) {
        if (roles != null) {
            roles.remove(role.name());
        }
    }

    /**
     * Add a platform to the supported platforms list.
     */
    public void addPlatform(String platform) {
        if (platforms == null) {
            platforms = new ArrayList<>();
        }
        String upperPlatform = platform.toUpperCase();
        if (!platforms.contains(upperPlatform)) {
            platforms.add(upperPlatform);
        }
    }
}
