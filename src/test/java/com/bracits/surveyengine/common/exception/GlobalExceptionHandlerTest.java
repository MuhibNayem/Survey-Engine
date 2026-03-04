package com.bracits.surveyengine.common.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.http.MockHttpInputMessage;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void returnsBadRequestForUnreadableRequestBody() {
        MockHttpServletRequest request = new MockHttpServletRequest("PUT", "/api/v1/campaigns/id/settings");
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException(
                "Malformed JSON",
                new IllegalArgumentException("Invalid date value"),
                new MockHttpInputMessage(new byte[0]));

        ResponseEntity<ErrorResponse> response = handler.handleUnreadableBody(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getBody().getPath()).isEqualTo("/api/v1/campaigns/id/settings");
        assertThat(response.getBody().getMessage()).isEqualTo("Invalid date value");
    }
}
