package com.bracits.surveyengine.admin.controller;

import com.bracits.surveyengine.admin.context.TenantContext;
import com.bracits.surveyengine.admin.dto.AuthResponse;
import com.bracits.surveyengine.admin.dto.AuthUserResponse;
import com.bracits.surveyengine.admin.dto.LoginRequest;
import com.bracits.surveyengine.admin.dto.RefreshTokenRequest;
import com.bracits.surveyengine.admin.dto.RegisterRequest;
import com.bracits.surveyengine.admin.entity.AdminRole;
import com.bracits.surveyengine.admin.service.AdminAuthService;
import com.bracits.surveyengine.admin.util.CookieUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/auth")
@RequiredArgsConstructor
public class AdminAuthController {

    private final AdminAuthService adminAuthService;
    private final CookieUtil cookieUtil;

    @PostMapping("/register")
    public ResponseEntity<AuthUserResponse> register(
            @Valid @RequestBody RegisterRequest request,
            HttpServletResponse response) {
        AuthResponse authResponse = adminAuthService.registerPublic(request);
        cookieUtil.addTokenCookies(response, authResponse);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(toUserResponse(authResponse));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthUserResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response) {
        AuthResponse authResponse = adminAuthService.login(request);
        cookieUtil.addTokenCookies(response, authResponse);
        return ResponseEntity.ok(toUserResponse(authResponse));
    }

    @PostMapping("/token/register")
    public ResponseEntity<AuthResponse> registerTokenMode(
            @Valid @RequestBody RegisterRequest request) {
        AuthResponse authResponse = adminAuthService.registerPublic(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(authResponse);
    }

    @PostMapping("/token/login")
    public ResponseEntity<AuthResponse> loginTokenMode(
            @Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(adminAuthService.login(request));
    }

    @PostMapping("/token/refresh")
    public ResponseEntity<AuthResponse> refreshTokenMode(
            @Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(adminAuthService.refresh(request.getRefreshToken()));
    }

    @GetMapping("/csrf")
    public ResponseEntity<Map<String, String>> csrf(CsrfToken token) {
        return ResponseEntity.ok(Map.of("token", token.getToken()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthUserResponse> refresh(
            HttpServletRequest request,
            HttpServletResponse response) {
        String refreshToken = extractCookie(request, CookieUtil.REFRESH_TOKEN_COOKIE);
        if (refreshToken == null || refreshToken.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        AuthResponse authResponse = adminAuthService.refresh(refreshToken);
        cookieUtil.addTokenCookies(response, authResponse);
        return ResponseEntity.ok(toUserResponse(authResponse));
    }

    @PostMapping("/revert-impersonation")
    public ResponseEntity<Void> revertImpersonation(
            HttpServletRequest request,
            HttpServletResponse response) {
        cookieUtil.restoreImpersonatorCookies(request, response);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        cookieUtil.clearTokenCookies(response);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<AuthUserResponse> me() {
        TenantContext.TenantInfo info = TenantContext.get();
        if (info == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String roleStr = info.roles().stream().findFirst().orElse(null);
        AdminRole role = roleStr != null ? AdminRole.valueOf(roleStr) : null;
        return ResponseEntity.ok(AuthUserResponse.builder()
                .email(info.email())
                .tenantId(info.tenantId())
                .role(role)
                .build());
    }

    private AuthUserResponse toUserResponse(AuthResponse authResponse) {
        return AuthUserResponse.builder()
                .userId(authResponse.getUserId())
                .email(authResponse.getEmail())
                .fullName(authResponse.getFullName())
                .tenantId(authResponse.getTenantId())
                .role(authResponse.getRole())
                .build();
    }

    private String extractCookie(HttpServletRequest request, String name) {
        if (request.getCookies() == null)
            return null;
        return Arrays.stream(request.getCookies())
                .filter(c -> name.equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }
}
