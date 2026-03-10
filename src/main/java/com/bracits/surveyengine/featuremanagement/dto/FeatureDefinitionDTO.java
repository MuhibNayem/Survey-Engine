package com.bracits.surveyengine.featuremanagement.dto;

import com.bracits.surveyengine.featuremanagement.entity.FeatureCategory;
import com.bracits.surveyengine.featuremanagement.entity.FeatureType;
import com.bracits.surveyengine.subscription.entity.SubscriptionPlan;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * DTO for FeatureDefinition entity.
 * Used for API responses when returning feature information.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FeatureDefinitionDTO {

    /**
     * Unique identifier for the feature.
     */
    private UUID id;

    /**
     * Unique key for the feature (e.g., "tour.dashboard").
     */
    private String featureKey;

    /**
     * Type of feature: TOUR, TOOLTIP, BANNER, FEATURE_FLAG, ANNOUNCEMENT.
     */
    private FeatureType featureType;

    /**
     * Functional category for grouping.
     */
    private FeatureCategory category;

    /**
     * Human-readable name.
     */
    private String name;

    /**
     * Detailed description.
     */
    private String description;

    /**
     * Whether feature is globally enabled.
     */
    private Boolean enabled;

    /**
     * Rollout percentage (0-100).
     */
    private Integer rolloutPercentage;

    /**
     * Minimum subscription plan required.
     */
    private SubscriptionPlan minPlan;

    /**
     * Roles allowed to access this feature.
     */
    private List<String> roles;

    /**
     * Platforms where feature is available.
     */
    private List<String> platforms;

    /**
     * Additional metadata (steps for tours, content for tooltips, etc.).
     */
    private Map<String, Object> metadata;

    /**
     * Creator of the feature definition.
     */
    private String createdBy;

    /**
     * Creation timestamp.
     */
    private Instant createdAt;

    /**
     * Last modifier of the feature definition.
     */
    private String updatedBy;

    /**
     * Last modification timestamp.
     */
    private Instant updatedAt;

    /**
     * Total number of users who accessed this feature.
     * Populated in analytics queries.
     */
    private Long totalAccessed;

    /**
     * Total number of users who completed this feature.
     * Populated in analytics queries.
     */
    private Long totalCompleted;

    /**
     * Number of unique tenants using this feature.
     * Populated in analytics queries.
     */
    private Long uniqueTenants;

    /**
     * Completion rate percentage.
     * Populated in analytics queries.
     */
    private Double completionRate;
}
