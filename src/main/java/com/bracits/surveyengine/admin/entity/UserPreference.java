package com.bracits.surveyengine.admin.entity;

import com.bracits.surveyengine.common.audit.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * User preferences for UI/UX features.
 * Stores dismissed tooltips, feature tours, theme preferences, etc.
 */
@Entity
@Table(name = "user_preference")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserPreference extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "tenant_id", nullable = false)
    private String tenantId;

    /**
     * JSON map of preference key-value pairs.
     * Examples:
     * - "tour.dashboard.completed": "true"
     * - "tooltip.survey-builder.dismissed": "true"
     * - "help.campaign-settings.viewed": "false"
     */
    @Column(name = "preferences", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    @Builder.Default
    private Map<String, String> preferences = new HashMap<>();

    /**
     * Check if a specific feature/tour has been completed or dismissed.
     */
    public boolean isFeatureCompleted(String featureKey) {
        return "true".equals(preferences.get(featureKey));
    }

    /**
     * Mark a feature/tour as completed or dismissed.
     */
    public void setFeatureCompleted(String featureKey, boolean completed) {
        if (preferences == null) {
            preferences = new HashMap<>();
        }
        preferences.put(featureKey, String.valueOf(completed));
    }

    /**
     * Get a specific preference value.
     */
    public String getPreference(String key) {
        return preferences != null ? preferences.get(key) : null;
    }

    /**
     * Set a specific preference value.
     */
    public void setPreference(String key, String value) {
        if (preferences == null) {
            preferences = new HashMap<>();
        }
        preferences.put(key, value);
    }
}
