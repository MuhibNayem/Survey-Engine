package com.bracits.surveyengine.featuremanagement.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * DTO for TenantFeatureConfig entity.
 * Used for API responses when returning tenant-specific feature configurations.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TenantFeatureConfigDTO {

    /**
     * Unique identifier for the configuration.
     */
    private UUID id;

    /**
     * Tenant identifier.
     */
    private String tenantId;

    /**
     * Feature identifier.
     */
    private UUID featureId;

    /**
     * Feature key (for convenience).
     */
    private String featureKey;

    /**
     * Feature name (for convenience).
     */
    private String featureName;

    /**
     * Whether feature is enabled for this tenant.
     */
    private Boolean enabled;

    /**
     * Tenant-specific rollout percentage.
     * Null means use global percentage.
     */
    private Integer rolloutPercentage;

    /**
     * Tenant-specific custom metadata.
     */
    private Map<String, Object> customMetadata;

    /**
     * Effective enabled status (considering global and tenant settings).
     */
    private Boolean effectiveEnabled;

    /**
     * Effective rollout percentage.
     */
    private Integer effectiveRolloutPercentage;

    /**
     * Creator of the configuration.
     */
    private String createdBy;

    /**
     * Creation timestamp.
     */
    private Instant createdAt;

    /**
     * Last modifier of the configuration.
     */
    private String updatedBy;

    /**
     * Last modification timestamp.
     */
    private Instant updatedAt;
}
