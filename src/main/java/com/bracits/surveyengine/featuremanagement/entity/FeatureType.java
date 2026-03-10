package com.bracits.surveyengine.featuremanagement.entity;

/**
 * Types of features in the feature management system.
 */
public enum FeatureType {
    /** Interactive guided tour through UI features */
    TOUR,
    
    /** Contextual help tooltip */
    TOOLTIP,
    
    /** Prominent announcement banner */
    BANNER,
    
    /** Binary feature flag for functionality toggling */
    FEATURE_FLAG,
    
    /** System-wide announcement */
    ANNOUNCEMENT
}
