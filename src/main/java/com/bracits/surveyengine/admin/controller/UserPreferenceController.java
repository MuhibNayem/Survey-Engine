package com.bracits.surveyengine.admin.controller;

import com.bracits.surveyengine.admin.dto.UserPreferenceDTO;
import com.bracits.surveyengine.admin.service.UserPreferenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller for managing user preferences.
 * Handles dismissed tooltips, completed tours, and UI preferences.
 */
@RestController
@RequestMapping("/api/v1/admin/preferences")
@RequiredArgsConstructor
public class UserPreferenceController {

    private final UserPreferenceService preferenceService;

    /**
     * Get all preferences for the current user.
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR', 'VIEWER')")
    public ResponseEntity<UserPreferenceDTO> getPreferences() {
        var prefs = preferenceService.getPreferences();
        
        UserPreferenceDTO dto = UserPreferenceDTO.builder()
                .preferences(prefs.getPreferences())
                .build();
        
        return ResponseEntity.ok(dto);
    }

    /**
     * Check if a specific feature/tour is completed.
     */
    @GetMapping("/{featureKey}/completed")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR', 'VIEWER')")
    public ResponseEntity<Map<String, Boolean>> isFeatureCompleted(
            @PathVariable String featureKey) {
        boolean completed = preferenceService.isFeatureCompleted(featureKey);
        return ResponseEntity.ok(Map.of("completed", completed));
    }

    /**
     * Mark a feature/tour as completed.
     */
    @PostMapping("/{featureKey}/complete")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR', 'VIEWER')")
    public ResponseEntity<Void> setFeatureCompleted(
            @PathVariable String featureKey,
            @RequestParam(defaultValue = "true") boolean completed) {
        preferenceService.setFeatureCompleted(featureKey, completed);
        return ResponseEntity.ok().build();
    }

    /**
     * Update a specific preference.
     */
    @PatchMapping("/{key}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR', 'VIEWER')")
    public ResponseEntity<Void> setPreference(
            @PathVariable String key,
            @RequestBody String value) {
        preferenceService.setPreference(key, value);
        return ResponseEntity.ok().build();
    }

    /**
     * Update multiple preferences at once.
     */
    @PatchMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR', 'VIEWER')")
    public ResponseEntity<Void> updatePreferences(
            @RequestBody Map<String, String> preferences) {
        preferenceService.updatePreferences(preferences);
        return ResponseEntity.ok().build();
    }

    /**
     * Reset all preferences (show all tours/tooltips again).
     */
    @DeleteMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR', 'VIEWER')")
    public ResponseEntity<Void> resetPreferences() {
        preferenceService.resetPreferences();
        return ResponseEntity.ok().build();
    }
}
