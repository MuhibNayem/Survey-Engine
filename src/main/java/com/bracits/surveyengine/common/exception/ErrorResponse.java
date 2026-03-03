package com.bracits.surveyengine.common.exception;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.List;

/**
 * Standard error response DTO returned by all API error responses.
 */
@Getter
@Builder
public class ErrorResponse {

    private final Instant timestamp;
    private final int status;
    private final String error;
    private final String message;
    private final String path;
    private final List<FieldError> fieldErrors;

    @Getter
    @Builder
    public static class FieldError {
        private final String field;
        private final String message;
        private final Object rejectedValue;
    }
}
