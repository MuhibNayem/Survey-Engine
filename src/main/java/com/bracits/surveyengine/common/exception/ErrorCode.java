package com.bracits.surveyengine.common.exception;

/**
 * Business error codes used across the Survey Engine.
 * Maps to SRS §5.4 validation errors and general application error codes.
 */
public enum ErrorCode {

    // --- Scoring & Validation (SRS §5.4) ---
    INVALID_WEIGHT_SUM("Category weights must total exactly 100%"),
    CATEGORY_MAX_SCORE_ZERO("Category max score cannot be zero"),
    QUESTION_MAX_SCORE_INVALID("Question max score must be greater than 0"),
    SURVEY_IMMUTABLE_AFTER_PUBLISH("Survey is immutable after publish"),
    INVALID_LIFECYCLE_TRANSITION("Lifecycle transition is not allowed"),
    // --- Response Collection & Locking (SRS §4.7, §8) ---
    RESPONSE_LOCKED("Response is locked and cannot be modified"),
    RESPONSE_NOT_LOCKED("Response is not in locked state"),
    CAMPAIGN_NOT_ACTIVE("Campaign is not in active state"),
    RESPONSE_QUOTA_EXCEEDED("Response quota for this campaign has been reached"),
    DUPLICATE_RESPONSE("A response from this device/IP already exists"),
    SUBSCRIPTION_INACTIVE("Active subscription is required"),
    PAYMENT_FAILED("Payment processing failed"),
    QUOTA_EXCEEDED("Plan quota exceeded"),

    // --- Feature Management ---
    FEATURE_DISABLED("Feature is currently disabled"),
    RESOURCE_ALREADY_EXISTS("Resource already exists"),

    // --- General ---
    RESOURCE_NOT_FOUND("Requested resource not found"),
    VALIDATION_FAILED("Request validation failed"),
    VALIDATION_ERROR("Request validation failed"),
    ACCESS_DENIED("Access denied"),
    INTERNAL_ERROR("An unexpected error occurred");

    private final String defaultMessage;

    ErrorCode(String defaultMessage) {
        this.defaultMessage = defaultMessage;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }
}
