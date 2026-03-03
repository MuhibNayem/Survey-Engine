package com.bracits.surveyengine.admin;

import com.bracits.surveyengine.TestcontainersConfiguration;
import com.bracits.surveyengine.admin.context.TenantContext;
import com.bracits.surveyengine.admin.dto.AuthResponse;
import com.bracits.surveyengine.admin.dto.LoginRequest;
import com.bracits.surveyengine.admin.dto.RegisterRequest;
import com.bracits.surveyengine.admin.service.AdminAuthService;
import com.bracits.surveyengine.admin.service.JwtService;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.AuditorAware;

import java.util.Set;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class AdminAuthIntegrationTest {

    @Autowired
    private AdminAuthService adminAuthService;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private AuditorAware<String> auditorProvider;

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Test
    void shouldRegisterAndReturnBothTokens() {
        AuthResponse response = adminAuthService.register(RegisterRequest.builder()
                .fullName("Test Admin")
                .email("admin-reg@example.com")
                .password("securepass123")
                .tenantId("tenant-test")
                .build());

        assertThat(response.getUserId()).isNotNull();
        assertThat(response.getAccessToken()).isNotBlank();
        assertThat(response.getRefreshToken()).isNotBlank();
        assertThat(response.getTokenType()).isEqualTo("Bearer");
    }

    @Test
    void shouldLoginAndReturnBothTokens() {
        adminAuthService.register(RegisterRequest.builder()
                .fullName("Login Admin").email("admin-login@example.com")
                .password("securepass123").tenantId("tenant-login").build());

        AuthResponse response = adminAuthService.login(LoginRequest.builder()
                .email("admin-login@example.com").password("securepass123").build());

        assertThat(response.getAccessToken()).isNotBlank();
        assertThat(response.getRefreshToken()).isNotBlank();
    }

    @Test
    void shouldRefreshTokenAndGetNewPair() {
        AuthResponse initial = adminAuthService.register(RegisterRequest.builder()
                .fullName("Refresh Admin").email("admin-refresh@example.com")
                .password("securepass123").tenantId("tenant-refresh").build());

        AuthResponse refreshed = adminAuthService.refresh(initial.getRefreshToken());

        assertThat(refreshed.getAccessToken()).isNotBlank();
        assertThat(refreshed.getRefreshToken()).isNotBlank();
        // New refresh token should be different (rotation)
        assertThat(refreshed.getRefreshToken()).isNotEqualTo(initial.getRefreshToken());
    }

    @Test
    void shouldRejectUsedRefreshToken() {
        AuthResponse initial = adminAuthService.register(RegisterRequest.builder()
                .fullName("Revoke Admin").email("admin-revoke@example.com")
                .password("securepass123").tenantId("tenant-revoke").build());

        // Use refresh token once
        adminAuthService.refresh(initial.getRefreshToken());

        // Using same token again should fail (revoked)
        assertThatThrownBy(() -> adminAuthService.refresh(initial.getRefreshToken()))
                .hasMessageContaining("Invalid or revoked refresh token");
    }

    @Test
    void shouldRejectInvalidPassword() {
        adminAuthService.register(RegisterRequest.builder()
                .fullName("Bad PW").email("admin-badpw@example.com")
                .password("securepass123").tenantId("tenant-badpw").build());

        assertThatThrownBy(() -> adminAuthService.login(LoginRequest.builder()
                .email("admin-badpw@example.com").password("wrongpassword").build()))
                .hasMessageContaining("Invalid email or password");
    }

    @Test
    void shouldRejectDuplicateEmail() {
        adminAuthService.register(RegisterRequest.builder()
                .fullName("Dup").email("admin-dup@example.com")
                .password("securepass123").tenantId("tenant-dup").build());

        assertThatThrownBy(() -> adminAuthService.register(RegisterRequest.builder()
                .fullName("Dup2").email("admin-dup@example.com")
                .password("securepass456").tenantId("tenant-dup2").build()))
                .hasMessageContaining("Email already registered");
    }

    @Test
    void shouldGenerateJwtWithCorrectClaims() {
        AuthResponse response = adminAuthService.register(RegisterRequest.builder()
                .fullName("JWT Admin").email("admin-jwt@example.com")
                .password("securepass123").tenantId("tenant-jwt").build());

        Claims claims = jwtService.parseToken(response.getAccessToken());
        assertThat(claims.get("tenant_id", String.class)).isEqualTo("tenant-jwt");
        assertThat(claims.get("email", String.class)).isEqualTo("admin-jwt@example.com");
        assertThat(claims.get("role", String.class)).isEqualTo("ADMIN");
    }

    @Test
    void shouldReturnSystemAuditorWhenNoContext() {
        assertThat(auditorProvider.getCurrentAuditor())
                .isPresent().contains("system");
    }

    @Test
    void shouldReturnTenantAuditorWhenContextSet() {
        TenantContext.set(new TenantContext.TenantInfo(
                "tenant-acme", "user-123", "a@acme.com", Set.of("ADMIN")));
        assertThat(auditorProvider.getCurrentAuditor())
                .isPresent().contains("tenant-acme/user-123");
    }
}
