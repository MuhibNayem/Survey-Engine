package com.bracits.surveyengine.featuremanagement.dto;

import com.bracits.surveyengine.featuremanagement.entity.FeatureCategory;
import com.bracits.surveyengine.featuremanagement.entity.FeatureType;
import com.bracits.surveyengine.subscription.entity.SubscriptionPlan;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.*;

/**
 * Request DTO for updating existing feature definitions.
 * All fields are optional - only provided fields will be updated.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateFeatureRequest {

    /**
     * Human-readable name.
     */
    @Size(max = 255, message = "Name must not exceed 255 characters")
    private String name;

    /**
     * Detailed description.
     */
    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    private String description;

    /**
     * Whether feature should be enabled.
     */
    private Boolean enabled;

    /**
     * Rollout percentage 0-100.
     */
    @Min(value = 0, message = "Rollout percentage must be at least 0")
    @Max(value = 100, message = "Rollout percentage must be at most 100")
    private Integer rolloutPercentage;

    /**
     * Minimum subscription plan required.
     */
    private SubscriptionPlan minPlan;

    /**
     * Roles allowed to access this feature.
     * If empty, all roles can access.
     */
    private List<String> roles;

    /**
     * Platforms where feature is available.
     * If empty, all platforms are supported.
     */
    private List<String> platforms;

    /**
     * Additional metadata.
     * Merged with existing metadata (not replaced).
     */
    private Map<String, Object> metadata;

    /**
     * Whether to completely replace metadata instead of merging.
     */
    @Builder.Default
    private Boolean replaceMetadata = false;

    /**
     * Validate that roles list contains valid role names.
     */
    public boolean hasValidRoles() {
        if (roles == null) {
            return true;
        }
        if (roles.isEmpty()) {
            return true;
        }
        Set<String> validRoles = Set.of("SUPER_ADMIN", "ADMIN", "EDITOR", "VIEWER");
        return roles.stream().allMatch(validRoles::contains);
    }

    /**
     * Validate that platforms list contains valid platform names.
     */
    public boolean hasValidPlatforms() {
        if (platforms == null) {
            return true;
        }
        if (platforms.isEmpty()) {
            return true;
        }
        Set<String> validPlatforms = Set.of("WEB", "MOBILE", "DESKTOP", "API");
        return platforms.stream()
            .allMatch(p -> validPlatforms.contains(p.toUpperCase()));
    }

    /**
     * Check if this update request has any changes.
     */
    public boolean hasChanges() {
        return name != null ||
               description != null ||
               enabled != null ||
               rolloutPercentage != null ||
               minPlan != null ||
               roles != null ||
               platforms != null ||
               metadata != null;
    }
}
