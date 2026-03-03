package com.bracits.surveyengine.auth.dto;

import com.bracits.surveyengine.auth.entity.AuthenticationMode;
import com.bracits.surveyengine.auth.entity.FallbackPolicy;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthProfileRequest {

    @NotNull(message = "Tenant ID is required")
    private String tenantId;

    @NotNull(message = "Auth mode is required")
    private AuthenticationMode authMode;

    private String issuer;
    private String audience;
    private String jwksEndpoint;
    private String oidcDiscoveryUrl;
    private String oidcClientId;
    private String oidcClientSecret;
    private String oidcRedirectUri;
    private String oidcScopes;
    private Integer clockSkewSeconds;
    private Integer tokenTtlSeconds;
    private String signingSecret;
    private FallbackPolicy fallbackPolicy;

    private List<ClaimMappingRequest> claimMappings;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClaimMappingRequest {
        private String externalClaim;
        private String internalField;
        @Builder.Default
        private boolean required = false;
    }
}
