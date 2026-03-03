package com.bracits.surveyengine.survey.entity;

/**
 * Survey lifecycle states per SRS §4.6.
 * <p>
 * Valid transitions:
 * Draft → Published → Closed → Results_Published → Archived
 * Closed → Published (reopen with audited reason)
 */
public enum SurveyLifecycleState {
    DRAFT,
    PUBLISHED,
    CLOSED,
    RESULTS_PUBLISHED,
    ARCHIVED
}
