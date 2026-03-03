package com.bracits.surveyengine.common.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BusinessExceptionTest {

    @Test
    void shouldCarryErrorCodeAndDefaultMessage() {
        BusinessException ex = new BusinessException(ErrorCode.INVALID_WEIGHT_SUM);

        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.INVALID_WEIGHT_SUM);
        assertThat(ex.getMessage()).isEqualTo("Category weights must total exactly 100%");
    }

    @Test
    void shouldCarryCustomMessage() {
        BusinessException ex = new BusinessException(
                ErrorCode.SURVEY_IMMUTABLE_AFTER_PUBLISH,
                "Cannot add questions after publish");

        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.SURVEY_IMMUTABLE_AFTER_PUBLISH);
        assertThat(ex.getMessage()).isEqualTo("Cannot add questions after publish");
    }

    @Test
    void shouldCarryCustomMessageAndCause() {
        RuntimeException cause = new RuntimeException("root cause");
        BusinessException ex = new BusinessException(
                ErrorCode.INTERNAL_ERROR, "Something went wrong", cause);

        assertThat(ex.getCause()).isEqualTo(cause);
    }
}
