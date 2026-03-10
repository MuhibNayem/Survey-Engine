package com.bracits.surveyengine.featuremanagement.dto;

import com.bracits.surveyengine.featuremanagement.entity.FeatureCategory;
import com.bracits.surveyengine.featuremanagement.entity.FeatureType;
import com.bracits.surveyengine.subscription.entity.SubscriptionPlan;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.*;

/**
 * Request DTO for creating new feature definitions.
 * Contains validation annotations for input validation.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateFeatureRequest {

    /**
     * Unique key for the feature (e.g., "tour.dashboard", "tooltip.survey.create").
     * Must be lowercase, alphanumeric with dots and underscores.
     */
    @NotBlank(message = "Feature key is required")
    @Pattern(
        regexp = "^[a-z][a-z0-9._]*$",
        message = "Feature key must start with lowercase letter and contain only lowercase letters, numbers, dots, and underscores"
    )
    @Size(min = 3, max = 100, message = "Feature key must be between 3 and 100 characters")
    private String featureKey;

    /**
     * Type of feature.
     */
    @NotNull(message = "Feature type is required")
    private FeatureType featureType;

    /**
     * Functional category.
     */
    @NotNull(message = "Category is required")
    private FeatureCategory category;

    /**
     * Human-readable name.
     */
    @NotBlank(message = "Name is required")
    @Size(max = 255, message = "Name must not exceed 255 characters")
    private String name;

    /**
     * Detailed description.
     */
    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    private String description;

    /**
     * Whether feature should be enabled (default: true).
     */
    @Builder.Default
    private Boolean enabled = true;

    /**
     * Rollout percentage 0-100 (default: 100).
     */
    @Min(value = 0, message = "Rollout percentage must be at least 0")
    @Max(value = 100, message = "Rollout percentage must be at most 100")
    @Builder.Default
    private Integer rolloutPercentage = 100;

    /**
     * Minimum subscription plan required (default: BASIC).
     */
    @Builder.Default
    private SubscriptionPlan minPlan = SubscriptionPlan.BASIC;

    /**
     * Roles allowed to access this feature.
     * If empty or null, all roles can access.
     */
    @Builder.Default
    private List<String> roles = new ArrayList<>();

    /**
     * Platforms where feature is available.
     * If empty or null, all platforms are supported.
     */
    @Builder.Default
    private List<String> platforms = new ArrayList<>();

    /**
     * Additional metadata.
     * For tours: contains step definitions.
     * For tooltips: contains content and placement.
     * For feature flags: contains configuration options.
     */
    @Builder.Default
    private Map<String, Object> metadata = new HashMap<>();

    /**
     * Validate that roles list contains valid role names.
     */
    public boolean hasValidRoles() {
        if (roles == null || roles.isEmpty()) {
            return true;
        }
        Set<String> validRoles = Set.of("SUPER_ADMIN", "ADMIN", "EDITOR", "VIEWER");
        return roles.stream().allMatch(validRoles::contains);
    }

    /**
     * Validate that platforms list contains valid platform names.
     */
    public boolean hasValidPlatforms() {
        if (platforms == null || platforms.isEmpty()) {
            return true;
        }
        Set<String> validPlatforms = Set.of("WEB", "MOBILE", "DESKTOP", "API");
        return platforms.stream()
            .allMatch(p -> validPlatforms.contains(p.toUpperCase()));
    }
}
