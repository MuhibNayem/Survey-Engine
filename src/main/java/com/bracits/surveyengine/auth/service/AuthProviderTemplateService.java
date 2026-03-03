package com.bracits.surveyengine.auth.service;

import com.bracits.surveyengine.auth.dto.AuthProfileRequest;
import com.bracits.surveyengine.auth.dto.ProviderTemplateResponse;
import com.bracits.surveyengine.common.exception.BusinessException;
import com.bracits.surveyengine.common.exception.ErrorCode;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class AuthProviderTemplateService {

    private static final List<String> DEFAULT_SCOPES = List.of("openid", "email", "profile");

    private static final List<AuthProfileRequest.ClaimMappingRequest> DEFAULT_MAPPINGS = List.of(
            AuthProfileRequest.ClaimMappingRequest.builder()
                    .externalClaim("sub")
                    .internalField("respondentId")
                    .required(true)
                    .build(),
            AuthProfileRequest.ClaimMappingRequest.builder()
                    .externalClaim("email")
                    .internalField("email")
                    .required(false)
                    .build(),
            AuthProfileRequest.ClaimMappingRequest.builder()
                    .externalClaim("name")
                    .internalField("displayName")
                    .required(false)
                    .build());

    private static final Map<String, ProviderTemplateResponse> TEMPLATES = templates();

    public List<ProviderTemplateResponse> list() {
        return List.copyOf(TEMPLATES.values());
    }

    public ProviderTemplateResponse get(String providerCode) {
        if (providerCode == null || providerCode.isBlank()) {
            throw new BusinessException(ErrorCode.VALIDATION_FAILED, "Provider code is required");
        }
        ProviderTemplateResponse template = TEMPLATES.get(providerCode.trim().toUpperCase(Locale.ROOT));
        if (template == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Provider template not found");
        }
        return template;
    }

    private static Map<String, ProviderTemplateResponse> templates() {
        Map<String, ProviderTemplateResponse> map = new LinkedHashMap<>();
        map.put("OKTA", template("OKTA", "Okta",
                "OIDC app integration for Okta workforce identity."));
        map.put("AUTH0", template("AUTH0", "Auth0",
                "OIDC application setup for Auth0 tenants."));
        map.put("AZURE_AD", template("AZURE_AD", "Microsoft Entra ID (Azure AD)",
                "OIDC application setup for Microsoft Entra ID."));
        map.put("KEYCLOAK", template("KEYCLOAK", "Keycloak",
                "OIDC client setup for self-managed or hosted Keycloak."));
        return map;
    }

    private static ProviderTemplateResponse template(String code, String displayName, String description) {
        return ProviderTemplateResponse.builder()
                .providerCode(code)
                .displayName(displayName)
                .description(description)
                .defaultScopes(DEFAULT_SCOPES)
                .defaultClaimMappings(DEFAULT_MAPPINGS)
                .requiredConfigFields(List.of(
                        "issuer",
                        "audience",
                        "oidcDiscoveryUrl",
                        "oidcClientId",
                        "oidcClientSecret",
                        "oidcRedirectUri"))
                .build();
    }
}
