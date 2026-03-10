package com.bracits.surveyengine.featuremanagement.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * DTO for user feature status.
 * Provides feature availability and completion status for a specific user.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FeatureStatusDTO {

    /**
     * Feature identifier.
     */
    private UUID featureId;

    /**
     * Feature key.
     */
    private String featureKey;

    /**
     * Feature name.
     */
    private String featureName;

    /**
     * Feature type.
     */
    private String featureType;

    /**
     * Whether feature is available to the user.
     * Considers: global enabled, plan requirements, role permissions, rollout percentage.
     */
    private Boolean available;

    /**
     * Whether feature is enabled globally.
     */
    private Boolean enabled;

    /**
     * Whether user has accessed/seen this feature.
     */
    private Boolean accessed;

    /**
     * Whether user has completed/dismissed this feature.
     */
    private Boolean completed;

    /**
     * Number of times user has accessed the feature.
     */
    private Integer accessCount;

    /**
     * Timestamp of user's last access.
     */
    private Instant lastAccessedAt;

    /**
     * Timestamp when user completed the feature.
     */
    private Instant completedAt;

    /**
     * Whether feature should be shown to user.
     * True if available AND not completed.
     */
    private Boolean shouldShow;

    /**
     * Reason why feature is not available (if applicable).
     * Possible values: "DISABLED", "PLAN_TOO_LOW", "ROLE_NOT_ALLOWED", "ROLLOUT_EXCLUDED", "COMPLETED"
     */
    private String unavailableReason;

    /**
     * Feature metadata (steps for tours, content for tooltips, etc.).
     */
    private Map<String, Object> metadata;

    /**
     * User-specific metadata (current tour step, etc.).
     */
    private Map<String, Object> userMetadata;

    /**
     * Create a status indicating feature is not available due to being disabled.
     */
    public static FeatureStatusDTO disabled(UUID featureId, String featureKey, String featureName) {
        return FeatureStatusDTO.builder()
            .featureId(featureId)
            .featureKey(featureKey)
            .featureName(featureName)
            .available(false)
            .enabled(false)
            .unavailableReason("DISABLED")
            .shouldShow(false)
            .build();
    }

    /**
     * Create a status indicating feature is not available due to plan requirements.
     */
    public static FeatureStatusDTO planRestricted(
        UUID featureId, String featureKey, String featureName, String requiredPlan) {
        return FeatureStatusDTO.builder()
            .featureId(featureId)
            .featureKey(featureKey)
            .featureName(featureName)
            .available(false)
            .enabled(true)
            .unavailableReason("PLAN_TOO_LOW")
            .shouldShow(false)
            .metadata(Map.of("requiredPlan", requiredPlan))
            .build();
    }

    /**
     * Create a status indicating feature is not available due to role restrictions.
     */
    public static FeatureStatusDTO roleRestricted(
        UUID featureId, String featureKey, String featureName) {
        return FeatureStatusDTO.builder()
            .featureId(featureId)
            .featureKey(featureKey)
            .featureName(featureName)
            .available(false)
            .enabled(true)
            .unavailableReason("ROLE_NOT_ALLOWED")
            .shouldShow(false)
            .build();
    }

    /**
     * Create a status indicating feature is not available due to rollout exclusion.
     */
    public static FeatureStatusDTO rolloutExcluded(
        UUID featureId, String featureKey, String featureName, Integer rolloutPercentage) {
        return FeatureStatusDTO.builder()
            .featureId(featureId)
            .featureKey(featureKey)
            .featureName(featureName)
            .available(false)
            .enabled(true)
            .unavailableReason("ROLLOUT_EXCLUDED")
            .shouldShow(false)
            .metadata(Map.of("rolloutPercentage", rolloutPercentage))
            .build();
    }

    /**
     * Create a status indicating feature is completed.
     */
    public static FeatureStatusDTO completed(
        UUID featureId, String featureKey, String featureName, Instant completedAt) {
        return FeatureStatusDTO.builder()
            .featureId(featureId)
            .featureKey(featureKey)
            .featureName(featureName)
            .available(true)
            .enabled(true)
            .accessed(true)
            .completed(true)
            .completedAt(completedAt)
            .shouldShow(false)
            .build();
    }

    /**
     * Create a status indicating feature is available and should be shown.
     */
    public static FeatureStatusDTO available(
        UUID featureId, String featureKey, String featureName, 
        Map<String, Object> metadata, Boolean accessed, Boolean completed) {
        return FeatureStatusDTO.builder()
            .featureId(featureId)
            .featureKey(featureKey)
            .featureName(featureName)
            .available(true)
            .enabled(true)
            .accessed(accessed)
            .completed(completed)
            .metadata(metadata)
            .shouldShow(!completed)
            .build();
    }
}
