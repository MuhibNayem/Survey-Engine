package com.bracits.surveyengine.auth.dto;

import lombok.*;

import java.util.Map;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenValidationResult {
    private boolean valid;
    private String respondentId;
    private String email;
    private Map<String, String> mappedClaims;
    private String errorCode;
    private String errorMessage;
}
