package com.bracits.surveyengine.admin.service;

import com.bracits.surveyengine.admin.dto.AuthResponse;
import com.bracits.surveyengine.admin.dto.LoginRequest;
import com.bracits.surveyengine.admin.dto.RegisterRequest;
import com.bracits.surveyengine.admin.entity.AdminRole;
import com.bracits.surveyengine.admin.entity.AdminUser;
import com.bracits.surveyengine.admin.entity.RefreshToken;
import com.bracits.surveyengine.admin.repository.AdminUserRepository;
import com.bracits.surveyengine.admin.repository.RefreshTokenRepository;
import com.bracits.surveyengine.common.exception.BusinessException;
import com.bracits.surveyengine.common.exception.ErrorCode;
import com.bracits.surveyengine.subscription.service.PlanQuotaService;
import com.bracits.surveyengine.subscription.service.SubscriptionService;
import com.bracits.surveyengine.tenant.service.TenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

/**
 * Engine-owned admin authentication service.
 * Handles registration, login, and refresh token rotation
 * with bcrypt password hashing and engine-issued JWT tokens.
 */
@Service
@RequiredArgsConstructor
public class AdminAuthService {

    private final AdminUserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final TenantService tenantService;
    private final SubscriptionService subscriptionService;
    private final PlanQuotaService planQuotaService;

    @Value("${survey-engine.jwt.refresh-ttl-seconds:604800}")
    private long refreshTtlSeconds; // 7 days default

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException(ErrorCode.DUPLICATE_RESPONSE,
                    "Email already registered: " + request.getEmail());
        }
        tenantService.ensureProvisioned(request.getTenantId());
        subscriptionService.ensureTrial(request.getTenantId());
        planQuotaService.enforceAdminUserQuota(request.getTenantId());

        AdminUser user = AdminUser.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .tenantId(request.getTenantId())
                .role(AdminRole.ADMIN)
                .build();

        user = userRepository.save(user);
        return issueTokens(user);
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        AdminUser user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                        "Invalid email or password"));

        if (!user.isActive()) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                    "Account is disabled");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                    "Invalid email or password");
        }

        // Revoke existing refresh tokens on new login
        refreshTokenRepository.revokeAllByUserId(user.getId());

        return issueTokens(user);
    }

    @Transactional
    public AuthResponse refresh(String refreshTokenValue) {
        RefreshToken refreshToken = refreshTokenRepository
                .findByTokenAndRevokedFalse(refreshTokenValue)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                        "Invalid or revoked refresh token"));

        if (refreshToken.isExpired()) {
            refreshToken.setRevoked(true);
            refreshTokenRepository.save(refreshToken);
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                    "Refresh token has expired");
        }

        // Revoke the used refresh token (rotation)
        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);

        AdminUser user = userRepository.findById(refreshToken.getUserId())
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                        "User not found"));

        return issueTokens(user);
    }

    private AuthResponse issueTokens(AdminUser user) {
        String accessToken = jwtService.generateToken(user);
        String refreshTokenValue = UUID.randomUUID().toString();

        RefreshToken refreshToken = RefreshToken.builder()
                .token(refreshTokenValue)
                .userId(user.getId())
                .expiresAt(Instant.now().plusSeconds(refreshTtlSeconds))
                .build();
        refreshTokenRepository.save(refreshToken);

        return AuthResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .tenantId(user.getTenantId())
                .role(user.getRole())
                .accessToken(accessToken)
                .refreshToken(refreshTokenValue)
                .tokenType("Bearer")
                .expiresIn(jwtService.getTokenTtlSeconds())
                .build();
    }
}
