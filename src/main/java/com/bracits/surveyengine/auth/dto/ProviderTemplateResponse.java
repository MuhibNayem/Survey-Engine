package com.bracits.surveyengine.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProviderTemplateResponse {

    private String providerCode;
    private String displayName;
    private String description;
    private List<String> defaultScopes;
    private List<AuthProfileRequest.ClaimMappingRequest> defaultClaimMappings;
    private List<String> requiredConfigFields;
}
