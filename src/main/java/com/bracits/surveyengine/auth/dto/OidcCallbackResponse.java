package com.bracits.surveyengine.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OidcCallbackResponse {
    private String accessCode;
    private String tenantId;
    private UUID campaignId;
    private String respondentId;
    private String email;
    private Instant expiresAt;
    private String redirectUrl;
}
