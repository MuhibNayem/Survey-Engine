package com.bracits.surveyengine.auth;

import com.bracits.surveyengine.TestcontainersConfiguration;
import com.bracits.surveyengine.auth.dto.AuthProfileRequest;
import com.bracits.surveyengine.auth.dto.AuthProfileResponse;
import com.bracits.surveyengine.auth.dto.TokenValidationResult;
import com.bracits.surveyengine.auth.entity.AuthConfigAudit;
import com.bracits.surveyengine.auth.entity.AuthenticationMode;
import com.bracits.surveyengine.auth.entity.FallbackPolicy;
import com.bracits.surveyengine.auth.repository.AuthConfigAuditRepository;
import com.bracits.surveyengine.auth.service.AuthProfileService;
import com.bracits.surveyengine.auth.service.TokenValidationService;
import com.bracits.surveyengine.common.exception.BusinessException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class AuthIntegrationTest {

        @Autowired
        private AuthProfileService authProfileService;
        @Autowired
        private TokenValidationService tokenValidationService;
        @Autowired
        private AuthConfigAuditRepository auditRepository;

        // ===== Public Anonymous =====

        @Test
        void shouldCreatePublicAnonymousProfile() {
                AuthProfileResponse response = authProfileService.create(AuthProfileRequest.builder()
                                .tenantId("tenant-anon-1")
                                .authMode(AuthenticationMode.PUBLIC_ANONYMOUS)
                                .build());

                assertThat(response.getId()).isNotNull();
                assertThat(response.getTenantId()).isEqualTo("tenant-anon-1");
                assertThat(response.getAuthMode()).isEqualTo(AuthenticationMode.PUBLIC_ANONYMOUS);
        }

        @Test
        void shouldAllowPublicAnonymousWithoutToken() {
                authProfileService.create(AuthProfileRequest.builder()
                                .tenantId("tenant-anon-2")
                                .authMode(AuthenticationMode.PUBLIC_ANONYMOUS)
                                .build());

                TokenValidationResult result = tokenValidationService.validateToken("tenant-anon-2", null);
                assertThat(result.isValid()).isTrue();
                assertThat(result.getRespondentId()).startsWith("anon-");
        }

        // ===== Signed Launch Token =====

        @Test
        void shouldValidateSignedLaunchToken() throws Exception {
                String secret = "test-secret-key-for-hmac-12345";

                authProfileService.create(AuthProfileRequest.builder()
                                .tenantId("tenant-signed-1")
                                .authMode(AuthenticationMode.SIGNED_LAUNCH_TOKEN)
                                .signingSecret(secret)
                                .issuer("test-issuer")
                                .audience("test-audience")
                                .claimMappings(List.of(
                                                AuthProfileRequest.ClaimMappingRequest.builder()
                                                                .externalClaim("sub")
                                                                .internalField("respondentId")
                                                                .required(true)
                                                                .build(),
                                                AuthProfileRequest.ClaimMappingRequest.builder()
                                                                .externalClaim("email")
                                                                .internalField("email")
                                                                .build()))
                                .build());

                String token = createSignedToken(secret,
                                "{\"sub\":\"user-123\",\"iss\":\"test-issuer\",\"aud\":\"test-audience\",\"email\":\"user@test.com\",\"jti\":\"%s\",\"iat\":%d,\"exp\":%d}"
                                                .formatted(
                                                                UUID.randomUUID(),
                                                                Instant.now().getEpochSecond(),
                                                                Instant.now().plusSeconds(3600).getEpochSecond()));

                TokenValidationResult result = tokenValidationService.validateToken("tenant-signed-1", token);
                assertThat(result.isValid()).isTrue();
                assertThat(result.getRespondentId()).isEqualTo("user-123");
        }

        @Test
        void shouldApplyDefaultClaimMappingsWhenNotProvided() throws Exception {
                String secret = "test-secret-default-mapping";

                authProfileService.create(AuthProfileRequest.builder()
                                .tenantId("tenant-default-mapping-1")
                                .authMode(AuthenticationMode.SIGNED_LAUNCH_TOKEN)
                                .signingSecret(secret)
                                .build());

                String token = createSignedToken(secret,
                                "{\"sub\":\"default-user-1\",\"jti\":\"%s\",\"iat\":%d,\"exp\":%d}"
                                                .formatted(
                                                                UUID.randomUUID(),
                                                                Instant.now().getEpochSecond(),
                                                                Instant.now().plusSeconds(3600).getEpochSecond()));

                TokenValidationResult result = tokenValidationService.validateToken("tenant-default-mapping-1", token);
                assertThat(result.isValid()).isTrue();
                assertThat(result.getRespondentId()).isEqualTo("default-user-1");
        }

        @Test
        void shouldRejectClaimMappingsWithoutRequiredRespondentId() {
                assertThatThrownBy(() -> authProfileService.create(AuthProfileRequest.builder()
                                .tenantId("tenant-invalid-mapping-1")
                                .authMode(AuthenticationMode.EXTERNAL_SSO_TRUST)
                                .claimMappings(List.of(
                                                AuthProfileRequest.ClaimMappingRequest.builder()
                                                                .externalClaim("email")
                                                                .internalField("email")
                                                                .required(true)
                                                                .build()))
                                .build()))
                                .isInstanceOf(BusinessException.class)
                                .hasMessageContaining("respondentId");
        }

        @Test
        void shouldUseSecureDefaultOidcScopesWhenNotProvided() {
                AuthProfileResponse created = authProfileService.create(AuthProfileRequest.builder()
                                .tenantId("tenant-oidc-scopes-1")
                                .authMode(AuthenticationMode.EXTERNAL_SSO_TRUST)
                                .build());
                assertThat(created.getOidcScopes()).isEqualTo("openid email profile");

                AuthProfileResponse updated = authProfileService.update(created.getId(), AuthProfileRequest.builder()
                                .tenantId("tenant-oidc-scopes-1")
                                .authMode(AuthenticationMode.EXTERNAL_SSO_TRUST)
                                .oidcScopes("profile")
                                .build());
                assertThat(updated.getOidcScopes()).contains("profile");
                assertThat(updated.getOidcScopes()).contains("openid");
        }

        @Test
        void shouldRejectExpiredToken() throws Exception {
                String secret = "test-secret-expired";

                authProfileService.create(AuthProfileRequest.builder()
                                .tenantId("tenant-expired-1")
                                .authMode(AuthenticationMode.SIGNED_LAUNCH_TOKEN)
                                .signingSecret(secret)
                                .build());

                String token = createSignedToken(secret,
                                "{\"sub\":\"user-1\",\"jti\":\"%s\",\"iat\":%d,\"exp\":%d}"
                                                .formatted(
                                                                UUID.randomUUID(),
                                                                Instant.now().minusSeconds(7200).getEpochSecond(),
                                                                Instant.now().minusSeconds(3600).getEpochSecond()));

                TokenValidationResult result = tokenValidationService.validateToken("tenant-expired-1", token);
                assertThat(result.isValid()).isFalse();
                assertThat(result.getErrorCode()).isEqualTo("AUTH_SSO_REQUIRED");
        }

        @Test
        void shouldRejectInvalidSignature() throws Exception {
                authProfileService.create(AuthProfileRequest.builder()
                                .tenantId("tenant-badsig-1")
                                .authMode(AuthenticationMode.SIGNED_LAUNCH_TOKEN)
                                .signingSecret("correct-secret")
                                .build());

                String token = createSignedToken("wrong-secret",
                                "{\"sub\":\"user-1\",\"jti\":\"%s\",\"iat\":%d,\"exp\":%d}"
                                                .formatted(
                                                                UUID.randomUUID(),
                                                                Instant.now().getEpochSecond(),
                                                                Instant.now().plusSeconds(3600).getEpochSecond()));

                TokenValidationResult result = tokenValidationService.validateToken("tenant-badsig-1", token);
                assertThat(result.isValid()).isFalse();
                assertThat(result.getErrorCode()).isEqualTo("AUTH_SSO_REQUIRED");
        }

        @Test
        void shouldRejectReplayTokenByJti() throws Exception {
                String secret = "test-secret-replay";

                authProfileService.create(AuthProfileRequest.builder()
                                .tenantId("tenant-replay-1")
                                .authMode(AuthenticationMode.SIGNED_LAUNCH_TOKEN)
                                .signingSecret(secret)
                                .build());

                String token = createSignedToken(secret,
                                "{\"sub\":\"user-1\",\"jti\":\"replay-jti-1\",\"iat\":%d,\"exp\":%d}"
                                                .formatted(
                                                                Instant.now().getEpochSecond(),
                                                                Instant.now().plusSeconds(3600).getEpochSecond()));

                TokenValidationResult first = tokenValidationService.validateToken("tenant-replay-1", token);
                assertThat(first.isValid()).isTrue();

                TokenValidationResult second = tokenValidationService.validateToken("tenant-replay-1", token);
                assertThat(second.isValid()).isFalse();
                assertThat(second.getErrorCode()).isEqualTo("AUTH_SSO_REQUIRED");
        }

        // ===== Key Rotation + Audit =====

        @Test
        void shouldRotateKeyAndAudit() {
                AuthProfileResponse profile = authProfileService.create(AuthProfileRequest.builder()
                                .tenantId("tenant-rotate-1")
                                .authMode(AuthenticationMode.SIGNED_LAUNCH_TOKEN)
                                .signingSecret("rotate-secret")
                                .build());

                assertThat(profile.getActiveKeyVersion()).isEqualTo(1);

                AuthProfileResponse rotated = authProfileService.rotateKey(profile.getId());
                assertThat(rotated.getActiveKeyVersion()).isEqualTo(2);

                List<AuthConfigAudit> audits = auditRepository
                                .findByAuthProfileIdOrderByChangedAtDesc(profile.getId());
                assertThat(audits).hasSizeGreaterThanOrEqualTo(2);
                assertThat(audits.get(0).getAction()).isEqualTo("KEY_ROTATION");
        }

        // ===== Fallback Policies =====

        @Test
        void shouldFallbackToAnonymousOnError() throws Exception {
                authProfileService.create(AuthProfileRequest.builder()
                                .tenantId("tenant-fallback-1")
                                .authMode(AuthenticationMode.SIGNED_LAUNCH_TOKEN)
                                .signingSecret("fb-secret")
                                .fallbackPolicy(FallbackPolicy.ANONYMOUS_FALLBACK)
                                .build());

                TokenValidationResult result = tokenValidationService
                                .validateToken("tenant-fallback-1", "invalid.token");

                assertThat(result.isValid()).isTrue();
                assertThat(result.getRespondentId()).startsWith("anon-");
        }

        @Test
        void shouldRejectWithSsoRequiredOnError() throws Exception {
                authProfileService.create(AuthProfileRequest.builder()
                                .tenantId("tenant-sso-1")
                                .authMode(AuthenticationMode.SIGNED_LAUNCH_TOKEN)
                                .signingSecret("sso-secret")
                                .fallbackPolicy(FallbackPolicy.SSO_REQUIRED)
                                .build());

                TokenValidationResult result = tokenValidationService
                                .validateToken("tenant-sso-1", "invalid.token");

                assertThat(result.isValid()).isFalse();
                assertThat(result.getErrorCode()).isEqualTo("AUTH_SSO_REQUIRED");
        }

        // ===== Helper =====

        private String createSignedToken(String secret, String payloadJson) throws Exception {
                String headerJson = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
                String headerBase64 = Base64.getUrlEncoder().withoutPadding()
                                .encodeToString(headerJson.getBytes(StandardCharsets.UTF_8));
                String payloadBase64 = Base64.getUrlEncoder().withoutPadding()
                                .encodeToString(payloadJson.getBytes(StandardCharsets.UTF_8));
                String signingInput = headerBase64 + "." + payloadBase64;

                Mac mac = Mac.getInstance("HmacSHA256");
                mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
                String sig = Base64.getUrlEncoder().withoutPadding()
                                .encodeToString(mac.doFinal(signingInput.getBytes(StandardCharsets.UTF_8)));

                return signingInput + "." + sig;
        }
}
