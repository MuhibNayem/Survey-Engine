package com.bracits.surveyengine.featuremanagement.entity;

/**
 * Functional categories for organizing features.
 */
public enum FeatureCategory {
    /** General features not tied to specific module */
    GENERAL,

    /** Onboarding tours, tooltips, and guidance */
    ONBOARDING,
    
    /** Dashboard-related features */
    DASHBOARD,
    
    /** Survey management features */
    SURVEYS,
    
    /** Campaign management features */
    CAMPAIGNS,
    
    /** Question bank features */
    QUESTIONS,
    
    /** Analytics and reporting features */
    ANALYTICS,
    
    /** Response management features */
    RESPONSES,
    
    /** Settings and configuration features */
    SETTINGS,
    
    /** Admin-only features */
    ADMIN
}
