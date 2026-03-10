package com.bracits.surveyengine.auth.service;

import com.bracits.surveyengine.auth.dto.AuthProfileRequest;
import com.bracits.surveyengine.auth.dto.AuthProfileResponse;
import com.bracits.surveyengine.auth.dto.ProviderTemplateResponse;
import com.bracits.surveyengine.auth.entity.*;
import com.bracits.surveyengine.auth.repository.AuthConfigAuditRepository;
import com.bracits.surveyengine.auth.repository.AuthProfileRepository;
import com.bracits.surveyengine.admin.context.TenantContext;
import com.bracits.surveyengine.common.exception.BusinessException;
import com.bracits.surveyengine.common.exception.ErrorCode;
import com.bracits.surveyengine.common.exception.ResourceNotFoundException;
import com.bracits.surveyengine.common.tenant.TenantSupport;
import com.bracits.surveyengine.subscription.service.PlanQuotaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.bracits.surveyengine.tenant.service.TenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Implementation of {@link AuthProfileService}.
 * Manages auth profile CRUD with config audit logging and key rotation.
 */
@Service
@RequiredArgsConstructor
public class AuthProfileServiceImpl implements AuthProfileService {

    private final AuthProfileRepository authProfileRepository;
    private final AuthConfigAuditRepository auditRepository;
    private final TenantService tenantService;
    private final AuthProviderTemplateService authProviderTemplateService;
    private final TokenValidationService tokenValidationService;
    private final OidcResponderAuthService oidcResponderAuthService;
    private final PlanQuotaService planQuotaService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String DEFAULT_OIDC_SCOPES = "openid email profile";
    private static final Pattern CLAIM_NAME_PATTERN = Pattern.compile("^[A-Za-z0-9_.-]{1,120}$");
    private static final Pattern INTERNAL_FIELD_PATTERN = Pattern.compile("^[A-Za-z][A-Za-z0-9_]{1,63}$");

    @Override
    @Transactional
    @com.bracits.surveyengine.common.audit.annotation.Auditable(action = "AUTH_PROFILE_CREATED")
    public AuthProfileResponse create(AuthProfileRequest request) {
        String tenantId = resolveRequestTenant(request.getTenantId());
        tenantService.ensureProvisioned(tenantId);
        List<AuthProfileRequest.ClaimMappingRequest> claimMappings = sanitizeAndValidateMappings(
                request.getClaimMappings());
        enforceAuthModeAccess(tenantId, request.getAuthMode());
        AuthProfile profile = AuthProfile.builder()
                .tenantId(tenantId)
                .authMode(request.getAuthMode())
                .issuer(request.getIssuer())
                .audience(request.getAudience())
                .jwksEndpoint(request.getJwksEndpoint())
                .oidcDiscoveryUrl(request.getOidcDiscoveryUrl())
                .oidcClientId(request.getOidcClientId())
                .oidcClientSecret(request.getOidcClientSecret())
                .oidcRedirectUri(request.getOidcRedirectUri())
                .oidcScopes(normalizeOidcScopes(request.getOidcScopes()))
                .clockSkewSeconds(request.getClockSkewSeconds() != null
                        ? request.getClockSkewSeconds()
                        : 30)
                .tokenTtlSeconds(request.getTokenTtlSeconds() != null
                        ? request.getTokenTtlSeconds()
                        : 3600)
                .signingSecret(request.getSigningSecret())
                .fallbackPolicy(request.getFallbackPolicy() != null
                        ? request.getFallbackPolicy()
                        : FallbackPolicy.SSO_REQUIRED)
                .build();

        applyClaimMappings(profile, claimMappings);

        profile = authProfileRepository.save(profile);

        logAudit(profile.getId(), "CREATE", null, snapshot(profile));

        return toResponse(profile);
    }

    @Override
    @Transactional
    @com.bracits.surveyengine.common.audit.annotation.Auditable(action = "AUTH_PROFILE_UPDATED")
    public AuthProfileResponse update(UUID id, AuthProfileRequest request) {
        AuthProfile profile = findOrThrow(id);
        String before = snapshot(profile);
        String beforeJwksEndpoint = profile.getJwksEndpoint();
        String beforeDiscoveryUrl = profile.getOidcDiscoveryUrl();
        List<AuthProfileRequest.ClaimMappingRequest> claimMappings = request.getClaimMappings() != null
                ? sanitizeAndValidateMappings(request.getClaimMappings())
                : null;

        profile.setAuthMode(request.getAuthMode());
        enforceAuthModeAccess(profile.getTenantId(), request.getAuthMode());
        profile.setIssuer(request.getIssuer());
        profile.setAudience(request.getAudience());
        profile.setJwksEndpoint(request.getJwksEndpoint());
        profile.setOidcDiscoveryUrl(request.getOidcDiscoveryUrl());
        profile.setOidcClientId(request.getOidcClientId());
        if (request.getOidcClientSecret() != null && !request.getOidcClientSecret().isBlank()) {
            profile.setOidcClientSecret(request.getOidcClientSecret());
        }
        profile.setOidcRedirectUri(request.getOidcRedirectUri());
        if (request.getOidcScopes() != null) {
            profile.setOidcScopes(normalizeOidcScopes(request.getOidcScopes()));
        } else if (profile.getOidcScopes() == null || profile.getOidcScopes().isBlank()) {
            profile.setOidcScopes(DEFAULT_OIDC_SCOPES);
        }
        if (request.getClockSkewSeconds() != null) {
            profile.setClockSkewSeconds(request.getClockSkewSeconds());
        }
        if (request.getTokenTtlSeconds() != null) {
            profile.setTokenTtlSeconds(request.getTokenTtlSeconds());
        }
        if (request.getSigningSecret() != null) {
            profile.setSigningSecret(request.getSigningSecret());
        }
        if (request.getFallbackPolicy() != null) {
            profile.setFallbackPolicy(request.getFallbackPolicy());
        }

        if (claimMappings != null) {
            profile.getClaimMappings().clear();
            // Flush orphan-removal deletes before inserting replacement mappings.
            // This avoids transient unique-key collisions on (auth_profile_id, internal_field).
            authProfileRepository.flush();
            applyClaimMappings(profile, claimMappings);
        }

        profile = authProfileRepository.save(profile);

        invalidateAuthCaches(
                beforeJwksEndpoint,
                profile.getJwksEndpoint(),
                beforeDiscoveryUrl,
                profile.getOidcDiscoveryUrl());

        String after = snapshot(profile);
        logAudit(profile.getId(), "UPDATE", before, after);
        if (!before.equals(after)) {
            logAudit(profile.getId(), "CLAIM_MAPPING_UPDATE", before, after);
        }

        return toResponse(profile);
    }

    @Override
    @Transactional(readOnly = true)
    public AuthProfileResponse getByTenantId(String tenantId) {
        String scopedTenantId = resolveRequestTenant(tenantId);
        AuthProfile profile = authProfileRepository.findByTenantId(scopedTenantId)
                .orElseThrow(() -> new ResourceNotFoundException("AuthProfile", scopedTenantId));
        return toResponse(profile);
    }

    @Override
    @Transactional
    @com.bracits.surveyengine.common.audit.annotation.Auditable(action = "AUTH_PROFILE_KEY_ROTATION")
    public AuthProfileResponse rotateKey(UUID id) {
        AuthProfile profile = findOrThrow(id);
        int previousVersion = profile.getActiveKeyVersion();
        profile.setActiveKeyVersion(previousVersion + 1);
        profile = authProfileRepository.save(profile);

        logAudit(profile.getId(), "KEY_ROTATION",
                "v" + previousVersion, "v" + profile.getActiveKeyVersion());

        return toResponse(profile);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProviderTemplateResponse> listProviderTemplates() {
        return authProviderTemplateService.list();
    }

    @Override
    @Transactional(readOnly = true)
    public ProviderTemplateResponse getProviderTemplate(String providerCode) {
        return authProviderTemplateService.get(providerCode);
    }

    private void logAudit(UUID profileId, String action, String before, String after) {
        auditRepository.save(AuthConfigAudit.builder()
                .authProfileId(profileId)
                .action(action)
                .changedBy(resolveActor())
                .beforeValue(before)
                .afterValue(after)
                .build());
    }

    private String resolveActor() {
        TenantContext.TenantInfo info = TenantContext.get();
        if (info == null) {
            return "SYSTEM_ADMIN";
        }
        String email = info.email();
        if (email != null && !email.isBlank()) {
            return email;
        }
        String userId = info.userId();
        return (userId == null || userId.isBlank()) ? "SYSTEM_ADMIN" : userId;
    }

    private AuthProfile findOrThrow(UUID id) {
        AuthProfile profile = authProfileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AuthProfile", id));
        resolveRequestTenant(profile.getTenantId());
        return profile;
    }

    private String resolveRequestTenant(String requestTenantId) {
        String contextTenantId = TenantSupport.currentTenantOrDefault();
        if ("default".equals(contextTenantId) && requestTenantId != null && !requestTenantId.isBlank()) {
            return requestTenantId;
        }
        if (requestTenantId != null && !requestTenantId.isBlank() && !contextTenantId.equals(requestTenantId)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED, "Auth profile tenant does not match current tenant");
        }
        return contextTenantId;
    }

    private List<AuthProfileRequest.ClaimMappingRequest> sanitizeAndValidateMappings(
            List<AuthProfileRequest.ClaimMappingRequest> requestedMappings) {
        List<AuthProfileRequest.ClaimMappingRequest> mappings = requestedMappings == null || requestedMappings.isEmpty()
                ? defaultMappings()
                : requestedMappings;
        List<AuthProfileRequest.ClaimMappingRequest> normalized = new ArrayList<>();
        Set<String> usedInternalFields = new LinkedHashSet<>();
        boolean hasRequiredRespondentId = false;

        for (AuthProfileRequest.ClaimMappingRequest cm : mappings) {
            String externalClaim = trimToNull(cm.getExternalClaim());
            String internalField = trimToNull(cm.getInternalField());
            if (externalClaim == null || internalField == null) {
                throw new BusinessException(ErrorCode.VALIDATION_FAILED,
                        "Claim mapping requires both externalClaim and internalField");
            }
            if (!CLAIM_NAME_PATTERN.matcher(externalClaim).matches()) {
                throw new BusinessException(ErrorCode.VALIDATION_FAILED,
                        "Invalid external claim name: " + externalClaim);
            }
            if (!INTERNAL_FIELD_PATTERN.matcher(internalField).matches()) {
                throw new BusinessException(ErrorCode.VALIDATION_FAILED,
                        "Invalid internal field name: " + internalField);
            }
            String key = internalField.toLowerCase();
            if (!usedInternalFields.add(key)) {
                throw new BusinessException(ErrorCode.VALIDATION_FAILED,
                        "Duplicate internal claim mapping: " + internalField);
            }
            if ("respondentId".equals(internalField) && cm.isRequired()) {
                hasRequiredRespondentId = true;
            }
            normalized.add(AuthProfileRequest.ClaimMappingRequest.builder()
                    .externalClaim(externalClaim)
                    .internalField(internalField)
                    .required(cm.isRequired())
                    .build());
        }

        if (!hasRequiredRespondentId) {
            throw new BusinessException(ErrorCode.VALIDATION_FAILED,
                    "A required respondentId claim mapping is mandatory");
        }
        return normalized;
    }

    private void applyClaimMappings(AuthProfile profile, List<AuthProfileRequest.ClaimMappingRequest> claimMappings) {
        for (AuthProfileRequest.ClaimMappingRequest cm : claimMappings) {
            profile.getClaimMappings().add(ClaimMapping.builder()
                    .authProfile(profile)
                    .externalClaim(cm.getExternalClaim())
                    .internalField(cm.getInternalField())
                    .required(cm.isRequired())
                    .build());
        }
    }

    private void invalidateAuthCaches(
            String beforeJwksEndpoint,
            String afterJwksEndpoint,
            String beforeDiscoveryUrl,
            String afterDiscoveryUrl) {
        // Evict both before/after keys so updates apply immediately even when endpoint
        // values stay unchanged.
        tokenValidationService.evictJwksCache(beforeJwksEndpoint);
        tokenValidationService.evictJwksCache(afterJwksEndpoint);
        oidcResponderAuthService.evictMetadataCache(beforeDiscoveryUrl);
        oidcResponderAuthService.evictMetadataCache(afterDiscoveryUrl);
    }

    private String normalizeOidcScopes(String rawScopes) {
        if (rawScopes == null || rawScopes.isBlank()) {
            return DEFAULT_OIDC_SCOPES;
        }
        LinkedHashSet<String> scopes = new LinkedHashSet<>();
        for (String part : rawScopes.split("[,\\s]+")) {
            if (!part.isBlank()) {
                scopes.add(part.trim());
            }
        }
        scopes.add("openid");
        if (scopes.isEmpty()) {
            return DEFAULT_OIDC_SCOPES;
        }
        return String.join(" ", scopes);
    }

    private List<AuthProfileRequest.ClaimMappingRequest> defaultMappings() {
        return List.of(
                AuthProfileRequest.ClaimMappingRequest.builder()
                        .externalClaim("sub")
                        .internalField("respondentId")
                        .required(true)
                        .build(),
                AuthProfileRequest.ClaimMappingRequest.builder()
                        .externalClaim("email")
                        .internalField("email")
                        .required(false)
                        .build());
    }

    private String snapshot(AuthProfile profile) {
        try {
            List<Map<String, Object>> claimMappings = profile.getClaimMappings().stream()
                    .map(cm -> {
                        Map<String, Object> mapping = new LinkedHashMap<>();
                        mapping.put("externalClaim", cm.getExternalClaim());
                        mapping.put("internalField", cm.getInternalField());
                        mapping.put("required", cm.isRequired());
                        return mapping;
                    })
                    .toList();
            Map<String, Object> snapshot = new LinkedHashMap<>();
            snapshot.put("tenantId", profile.getTenantId());
            snapshot.put("authMode", profile.getAuthMode().name());
            snapshot.put("issuer", profile.getIssuer());
            snapshot.put("audience", profile.getAudience());
            snapshot.put("jwksEndpoint", profile.getJwksEndpoint());
            snapshot.put("oidcDiscoveryUrl", profile.getOidcDiscoveryUrl());
            snapshot.put("oidcClientId", profile.getOidcClientId());
            snapshot.put("oidcRedirectUri", profile.getOidcRedirectUri());
            snapshot.put("oidcScopes", profile.getOidcScopes());
            snapshot.put("clockSkewSeconds", profile.getClockSkewSeconds());
            snapshot.put("tokenTtlSeconds", profile.getTokenTtlSeconds());
            snapshot.put("fallbackPolicy", profile.getFallbackPolicy().name());
            snapshot.put("active", profile.isActive());
            snapshot.put("claimMappings", claimMappings);
            return objectMapper.writeValueAsString(snapshot);
        } catch (Exception e) {
            return "{ \"authMode\": \"" + profile.getAuthMode().name() + "\" }";
        }
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private void enforceAuthModeAccess(String tenantId, AuthenticationMode authMode) {
        if (authMode == AuthenticationMode.SIGNED_LAUNCH_TOKEN) {
            planQuotaService.enforceSignedTokenAccess(tenantId);
        } else if (authMode == AuthenticationMode.EXTERNAL_SSO_TRUST) {
            planQuotaService.enforceSsoAccess(tenantId);
        }
    }

    private AuthProfileResponse toResponse(AuthProfile p) {
        List<AuthProfileResponse.ClaimMappingResponse> mappings = p.getClaimMappings().stream()
                .map(cm -> AuthProfileResponse.ClaimMappingResponse.builder()
                        .id(cm.getId())
                        .externalClaim(cm.getExternalClaim())
                        .internalField(cm.getInternalField())
                        .required(cm.isRequired())
                        .build())
                .toList();

        return AuthProfileResponse.builder()
                .id(p.getId())
                .tenantId(p.getTenantId())
                .authMode(p.getAuthMode())
                .issuer(p.getIssuer())
                .audience(p.getAudience())
                .jwksEndpoint(p.getJwksEndpoint())
                .oidcDiscoveryUrl(p.getOidcDiscoveryUrl())
                .oidcClientId(p.getOidcClientId())
                .oidcRedirectUri(p.getOidcRedirectUri())
                .oidcScopes(normalizeOidcScopes(p.getOidcScopes()))
                .clockSkewSeconds(p.getClockSkewSeconds())
                .tokenTtlSeconds(p.getTokenTtlSeconds())
                .activeKeyVersion(p.getActiveKeyVersion())
                .fallbackPolicy(p.getFallbackPolicy())
                .claimMappings(mappings)
                .build();
    }
}
