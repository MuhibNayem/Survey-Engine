package com.bracits.surveyengine.auth.service;

import com.bracits.surveyengine.auth.dto.AuthProfileRequest;
import com.bracits.surveyengine.auth.entity.AuthProfile;
import com.bracits.surveyengine.auth.entity.AuthenticationMode;
import com.bracits.surveyengine.auth.entity.FallbackPolicy;
import com.bracits.surveyengine.auth.repository.AuthConfigAuditRepository;
import com.bracits.surveyengine.auth.repository.AuthProfileRepository;
import com.bracits.surveyengine.tenant.service.TenantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthProfileServiceImplTest {

    @Mock
    private AuthProfileRepository authProfileRepository;
    @Mock
    private AuthConfigAuditRepository auditRepository;
    @Mock
    private TenantService tenantService;
    @Mock
    private AuthProviderTemplateService authProviderTemplateService;
    @Mock
    private TokenValidationService tokenValidationService;
    @Mock
    private OidcResponderAuthService oidcResponderAuthService;

    private AuthProfileServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new AuthProfileServiceImpl(
                authProfileRepository,
                auditRepository,
                tenantService,
                authProviderTemplateService,
                tokenValidationService,
                oidcResponderAuthService);
    }

    @Test
    void shouldEvictOidcAndJwksCachesAfterProfileUpdate() {
        UUID profileId = UUID.randomUUID();
        AuthProfile existing = AuthProfile.builder()
                .id(profileId)
                .tenantId("tenant-cache-1")
                .authMode(AuthenticationMode.EXTERNAL_SSO_TRUST)
                .fallbackPolicy(FallbackPolicy.SSO_REQUIRED)
                .jwksEndpoint("https://old.example.com/jwks")
                .oidcDiscoveryUrl("https://old.example.com/.well-known/openid-configuration")
                .build();

        when(authProfileRepository.findById(profileId)).thenReturn(Optional.of(existing));
        when(authProfileRepository.save(any(AuthProfile.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AuthProfileRequest request = AuthProfileRequest.builder()
                .tenantId("tenant-cache-1")
                .authMode(AuthenticationMode.EXTERNAL_SSO_TRUST)
                .fallbackPolicy(FallbackPolicy.SSO_REQUIRED)
                .jwksEndpoint("https://new.example.com/jwks")
                .oidcDiscoveryUrl("https://new.example.com/.well-known/openid-configuration")
                .build();

        var response = service.update(profileId, request);

        assertThat(response.getJwksEndpoint()).isEqualTo("https://new.example.com/jwks");
        assertThat(response.getOidcDiscoveryUrl()).isEqualTo("https://new.example.com/.well-known/openid-configuration");

        verify(tokenValidationService).evictJwksCache("https://old.example.com/jwks");
        verify(tokenValidationService).evictJwksCache("https://new.example.com/jwks");
        verify(oidcResponderAuthService)
                .evictMetadataCache("https://old.example.com/.well-known/openid-configuration");
        verify(oidcResponderAuthService)
                .evictMetadataCache("https://new.example.com/.well-known/openid-configuration");
    }
}
