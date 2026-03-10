package com.bracits.surveyengine.admin.service;

import com.bracits.surveyengine.admin.entity.UserPreference;
import com.bracits.surveyengine.admin.repository.UserPreferenceRepository;
import com.bracits.surveyengine.admin.context.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Service for managing user preferences.
 * Handles dismissed tooltips, completed tours, and UI preferences.
 */
@Service
@RequiredArgsConstructor
public class UserPreferenceService {

    private final UserPreferenceRepository preferenceRepository;

    /**
     * Get or create preferences for the current user.
     */
    @Transactional(readOnly = true)
    public UserPreference getPreferences() {
        UUID userId = getCurrentUserId();
        return preferenceRepository.findByUserId(userId)
                .orElseGet(() -> createDefaultPreferences(userId));
    }

    /**
     * Get a specific preference value.
     */
    @Transactional(readOnly = true)
    public String getPreference(String key) {
        UserPreference prefs = getPreferences();
        return prefs.getPreference(key);
    }

    /**
     * Check if a feature/tour has been completed.
     */
    @Transactional(readOnly = true)
    public boolean isFeatureCompleted(String featureKey) {
        UserPreference prefs = getPreferences();
        return prefs.isFeatureCompleted(featureKey);
    }

    /**
     * Mark a feature/tour as completed.
     */
    @Transactional
    public void setFeatureCompleted(String featureKey, boolean completed) {
        UserPreference prefs = getPreferences();
        prefs.setFeatureCompleted(featureKey, completed);
        preferenceRepository.save(prefs);
    }

    /**
     * Set a specific preference value.
     */
    @Transactional
    public void setPreference(String key, String value) {
        UserPreference prefs = getPreferences();
        prefs.setPreference(key, value);
        preferenceRepository.save(prefs);
    }

    /**
     * Update multiple preferences at once.
     */
    @Transactional
    public void updatePreferences(Map<String, String> preferences) {
        UserPreference prefs = getPreferences();
        preferences.forEach(prefs::setPreference);
        preferenceRepository.save(prefs);
    }

    /**
     * Reset all preferences for a user (e.g., show all tours again).
     */
    @Transactional
    public void resetPreferences() {
        UUID userId = getCurrentUserId();
        preferenceRepository.deleteByUserId(userId);
    }

    /**
     * Create default preferences for a new user.
     */
    private UserPreference createDefaultPreferences(UUID userId) {
        String tenantId = TenantContext.getTenantId();
        
        UserPreference prefs = UserPreference.builder()
                .userId(userId)
                .tenantId(tenantId)
                .preferences(Map.of(
                    "tour.dashboard.completed", "false",
                    "tour.surveys.completed", "false",
                    "tour.campaigns.completed", "false",
                    "tour.questions.completed", "false"
                ))
                .build();
        
        return preferenceRepository.save(prefs);
    }

    /**
     * Get current user ID from security context.
     */
    private UUID getCurrentUserId() {
        // Extract from JWT or security context
        // This should match your existing auth implementation
        String userIdStr = TenantContext.getUserId();
        if (userIdStr == null || userIdStr.contains("/")) {
            // Fallback or extract from sub claim
            throw new IllegalStateException("User ID not found in context");
        }
        return UUID.fromString(userIdStr);
    }
}
