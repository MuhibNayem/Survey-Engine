package com.bracits.surveyengine.featuremanagement.entity;

import com.bracits.surveyengine.common.audit.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Tenant-level configuration overrides for features.
 * Allows tenants to customize feature behavior, enable/disable features,
 * and override rollout percentages.
 */
@Entity
@Table(name = "tenant_feature_config", indexes = {
    @Index(name = "idx_tenant_feature_config_tenant_id", columnList = "tenant_id"),
    @Index(name = "idx_tenant_feature_config_feature_id", columnList = "feature_id"),
    @Index(name = "idx_tenant_feature_config_enabled", columnList = "enabled")
}, uniqueConstraints = {
    @UniqueConstraint(name = "uk_tenant_feature_config_tenant_feature", columnNames = {"tenant_id", "feature_id"})
})
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TenantFeatureConfig extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Tenant identifier.
     */
    @Column(name = "tenant_id", nullable = false, length = 255)
    private String tenantId;

    /**
     * Reference to the feature definition.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "feature_id", nullable = false, foreignKey = @ForeignKey(name = "fk_tenant_feature_config_feature"))
    private FeatureDefinition feature;

    /**
     * Tenant-specific enable/disable override.
     * If null, uses the global feature enabled status.
     */
    @Column(name = "enabled", nullable = false)
    @Builder.Default
    private boolean enabled = true;

    /**
     * Tenant-specific rollout percentage override.
     * If null, uses the global rollout percentage.
     */
    @Column(name = "rollout_percentage")
    private Integer rolloutPercentage;

    /**
     * Tenant-specific metadata overrides.
     * Can be used to customize feature content, URLs, branding, etc.
     */
    @Column(name = "custom_metadata", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    @Builder.Default
    private Map<String, Object> customMetadata = new HashMap<>();

    // ------------------------------------------------------------------------
    // Helper Methods
    // ------------------------------------------------------------------------

    /**
     * Get effective enabled status.
     * Returns tenant override if set, otherwise global feature status.
     */
    public boolean getEffectiveEnabled() {
        return enabled;
    }

    /**
     * Get effective rollout percentage.
     * Returns tenant override if set, otherwise global feature percentage.
     */
    public Integer getEffectiveRolloutPercentage() {
        return rolloutPercentage != null ? rolloutPercentage : 
               getFeature() != null ? getFeature().getRolloutPercentage() : 100;
    }

    /**
     * Get a custom metadata value by key.
     */
    @SuppressWarnings("unchecked")
    public <T> T getCustomMetadataValue(String key) {
        return customMetadata != null ? (T) customMetadata.get(key) : null;
    }

    /**
     * Set a custom metadata value.
     */
    public void setCustomMetadataValue(String key, Object value) {
        if (customMetadata == null) {
            customMetadata = new HashMap<>();
        }
        customMetadata.put(key, value);
    }

    /**
     * Merge custom metadata with global metadata.
     * Custom metadata takes precedence.
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getMergedMetadata() {
        Map<String, Object> merged = new HashMap<>();
        
        // Start with global metadata
        if (getFeature() != null && getFeature().getMetadata() != null) {
            merged.putAll(getFeature().getMetadata());
        }
        
        // Override with custom metadata
        if (customMetadata != null) {
            merged.putAll(customMetadata);
        }
        
        return merged;
    }
}
