package com.bracits.surveyengine.featuremanagement.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

/**
 * DTO for feature analytics data.
 * Provides usage statistics and completion metrics for a feature.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FeatureAnalyticsDTO {

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
     * Feature category.
     */
    private String category;

    /**
     * Minimum plan required.
     */
    private String minPlan;

    /**
     * Total number of users who accessed this feature.
     */
    private Long totalAccessed;

    /**
     * Total number of users who completed this feature.
     */
    private Long totalCompleted;

    /**
     * Total number of users who have not accessed this feature.
     */
    private Long totalNotAccessed;

    /**
     * Total number of unique users (accessed + not accessed).
     */
    private Long totalUsers;

    /**
     * Number of unique tenants using this feature.
     */
    private Long uniqueTenants;

    /**
     * Average access count per user.
     */
    private Double avgAccessCount;

    /**
     * Completion rate percentage (completed / accessed * 100).
     */
    private Double completionRate;

    /**
     * First access timestamp.
     */
    private Instant firstAccessedAt;

    /**
     * Last access timestamp.
     */
    private Instant lastAccessedAt;

    /**
     * Whether feature is currently enabled.
     */
    private Boolean enabled;

    /**
     * Rollout percentage.
     */
    private Integer rolloutPercentage;

    /**
     * Access trend data (last 7 days).
     * Format: [{date: "2026-01-01", count: 10}, ...]
     */
    private Object accessTrend;

    /**
     * Completion trend data (last 7 days).
     * Format: [{date: "2026-01-01", count: 5}, ...]
     */
    private Object completionTrend;

    /**
     * Top tenants by usage.
     * Format: [{tenantId: "...", tenantName: "...", accessCount: 100}, ...]
     */
    private Object topTenants;
}
