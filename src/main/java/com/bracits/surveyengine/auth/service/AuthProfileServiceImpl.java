package com.bracits.surveyengine.auth.service;

import com.bracits.surveyengine.auth.dto.AuthProfileRequest;
import com.bracits.surveyengine.auth.dto.AuthProfileResponse;
import com.bracits.surveyengine.auth.entity.*;
import com.bracits.surveyengine.auth.repository.AuthConfigAuditRepository;
import com.bracits.surveyengine.auth.repository.AuthProfileRepository;
import com.bracits.surveyengine.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Implementation of {@link AuthProfileService}.
 * Manages auth profile CRUD with config audit logging and key rotation.
 */
@Service
@RequiredArgsConstructor
public class AuthProfileServiceImpl implements AuthProfileService {

    private final AuthProfileRepository authProfileRepository;
    private final AuthConfigAuditRepository auditRepository;

    @Override
    @Transactional
    public AuthProfileResponse create(AuthProfileRequest request) {
        AuthProfile profile = AuthProfile.builder()
                .campaignId(request.getCampaignId())
                .authMode(request.getAuthMode())
                .issuer(request.getIssuer())
                .audience(request.getAudience())
                .jwksEndpoint(request.getJwksEndpoint())
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

        if (request.getClaimMappings() != null) {
            for (AuthProfileRequest.ClaimMappingRequest cm : request.getClaimMappings()) {
                ClaimMapping mapping = ClaimMapping.builder()
                        .authProfile(profile)
                        .externalClaim(cm.getExternalClaim())
                        .internalField(cm.getInternalField())
                        .required(cm.isRequired())
                        .build();
                profile.getClaimMappings().add(mapping);
            }
        }

        profile = authProfileRepository.save(profile);

        logAudit(profile.getId(), "CREATE", null, profile.getAuthMode().name());

        return toResponse(profile);
    }

    @Override
    @Transactional
    public AuthProfileResponse update(UUID id, AuthProfileRequest request) {
        AuthProfile profile = findOrThrow(id);
        String before = profile.getAuthMode().name();

        profile.setAuthMode(request.getAuthMode());
        profile.setIssuer(request.getIssuer());
        profile.setAudience(request.getAudience());
        profile.setJwksEndpoint(request.getJwksEndpoint());
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

        // Update claim mappings
        if (request.getClaimMappings() != null) {
            profile.getClaimMappings().clear();
            for (AuthProfileRequest.ClaimMappingRequest cm : request.getClaimMappings()) {
                ClaimMapping mapping = ClaimMapping.builder()
                        .authProfile(profile)
                        .externalClaim(cm.getExternalClaim())
                        .internalField(cm.getInternalField())
                        .required(cm.isRequired())
                        .build();
                profile.getClaimMappings().add(mapping);
            }
        }

        profile = authProfileRepository.save(profile);

        logAudit(profile.getId(), "UPDATE", before, profile.getAuthMode().name());

        return toResponse(profile);
    }

    @Override
    @Transactional(readOnly = true)
    public AuthProfileResponse getByCampaignId(UUID campaignId) {
        AuthProfile profile = authProfileRepository.findByCampaignId(campaignId)
                .orElseThrow(() -> new ResourceNotFoundException("AuthProfile for campaign", campaignId));
        return toResponse(profile);
    }

    @Override
    @Transactional
    public AuthProfileResponse rotateKey(UUID id) {
        AuthProfile profile = findOrThrow(id);
        int previousVersion = profile.getActiveKeyVersion();
        profile.setActiveKeyVersion(previousVersion + 1);
        profile = authProfileRepository.save(profile);

        logAudit(profile.getId(), "KEY_ROTATION",
                "v" + previousVersion, "v" + profile.getActiveKeyVersion());

        return toResponse(profile);
    }

    private void logAudit(UUID profileId, String action, String before, String after) {
        auditRepository.save(AuthConfigAudit.builder()
                .authProfileId(profileId)
                .action(action)
                .changedBy("SYSTEM_ADMIN")
                .beforeValue(before)
                .afterValue(after)
                .build());
    }

    private AuthProfile findOrThrow(UUID id) {
        return authProfileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AuthProfile", id));
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
                .campaignId(p.getCampaignId())
                .authMode(p.getAuthMode())
                .issuer(p.getIssuer())
                .audience(p.getAudience())
                .jwksEndpoint(p.getJwksEndpoint())
                .clockSkewSeconds(p.getClockSkewSeconds())
                .tokenTtlSeconds(p.getTokenTtlSeconds())
                .activeKeyVersion(p.getActiveKeyVersion())
                .fallbackPolicy(p.getFallbackPolicy())
                .claimMappings(mappings)
                .build();
    }
}
