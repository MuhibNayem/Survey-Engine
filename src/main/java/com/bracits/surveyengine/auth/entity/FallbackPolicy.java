package com.bracits.surveyengine.auth.entity;

/**
 * Fallback policy when primary auth mode fails.
 * SRS §4.9.3
 */
public enum FallbackPolicy {
    /** Require SSO — deny access on failure */
    SSO_REQUIRED,
    /** Allow anonymous fallback when SSO fails */
    ANONYMOUS_FALLBACK,
    /** Disable survey entirely on auth failure */
    DISABLE_ON_FAILURE
}
