package com.bracits.surveyengine.response.entity;

/**
 * Status of a survey response submission.
 * SRS §4.7, §8
 */
public enum ResponseStatus {
    IN_PROGRESS,
    SUBMITTED,
    LOCKED,
    REOPENED
}
