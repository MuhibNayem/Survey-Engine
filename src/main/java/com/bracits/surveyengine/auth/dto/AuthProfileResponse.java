package com.bracits.surveyengine.auth.dto;

import com.bracits.surveyengine.auth.entity.AuthenticationMode;
import com.bracits.surveyengine.auth.entity.FallbackPolicy;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthProfileResponse {
    private UUID id;
    private String tenantId;
    private AuthenticationMode authMode;
    private String issuer;
    private String audience;
    private String jwksEndpoint;
    private String oidcDiscoveryUrl;
    private String oidcClientId;
    private String oidcRedirectUri;
    private String oidcScopes;
    private Integer clockSkewSeconds;
    private Integer tokenTtlSeconds;
    private Integer activeKeyVersion;
    private FallbackPolicy fallbackPolicy;
    private List<ClaimMappingResponse> claimMappings;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClaimMappingResponse {
        private UUID id;
        private String externalClaim;
        private String internalField;
        private boolean required;
    }
}
