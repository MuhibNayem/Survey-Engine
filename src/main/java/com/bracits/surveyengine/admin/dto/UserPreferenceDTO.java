package com.bracits.surveyengine.admin.dto;

import lombok.*;

import java.util.HashMap;
import java.util.Map;

/**
 * DTO for user preference operations.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPreferenceDTO {
    
    /**
     * Map of preference key-value pairs.
     */
    @Builder.Default
    private Map<String, String> preferences = new HashMap<>();
    
    /**
     * Specific feature completion status.
     */
    @Builder.Default
    private Map<String, Boolean> featureCompletion = new HashMap<>();
}
