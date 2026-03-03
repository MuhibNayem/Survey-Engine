package com.bracits.surveyengine.campaign.entity;

/**
 * Authentication mode for survey access per SRS §4.7.
 */
public enum AuthMode {
    PUBLIC,
    SIGNED_TOKEN,
    SSO
}
