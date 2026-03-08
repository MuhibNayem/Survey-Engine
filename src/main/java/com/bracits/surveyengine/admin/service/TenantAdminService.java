package com.bracits.surveyengine.admin.service;

import com.bracits.surveyengine.admin.dto.AuthResponse;
import com.bracits.surveyengine.admin.dto.OverrideSubscriptionRequest;
import com.bracits.surveyengine.admin.dto.TenantOverviewResponse;
import com.bracits.surveyengine.admin.entity.AdminRole;
import com.bracits.surveyengine.admin.entity.AdminUser;
import com.bracits.surveyengine.admin.entity.RefreshToken;
import com.bracits.surveyengine.admin.repository.AdminUserRepository;
import com.bracits.surveyengine.admin.repository.RefreshTokenRepository;
import com.bracits.surveyengine.common.exception.BusinessException;
import com.bracits.surveyengine.common.exception.ErrorCode;
import com.bracits.surveyengine.subscription.entity.Subscription;
import com.bracits.surveyengine.subscription.entity.SubscriptionStatus;
import com.bracits.surveyengine.subscription.repository.SubscriptionRepository;
import com.bracits.surveyengine.tenant.entity.Tenant;
import com.bracits.surveyengine.tenant.repository.TenantRepository;
import com.bracits.surveyengine.response.repository.SurveyResponseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TenantAdminService {

        private final TenantRepository tenantRepository;
        private final AdminUserRepository adminUserRepository;
        private final SubscriptionRepository subscriptionRepository;
        private final SurveyResponseRepository surveyResponseRepository;
        private final JwtService jwtService;
        private final RefreshTokenRepository refreshTokenRepository;

        @Value("${survey-engine.jwt.refresh-ttl-seconds:604800}")
        private long refreshTtlSeconds;

        @Transactional(readOnly = true)
        public List<TenantOverviewResponse> getAllTenants() {
                List<Tenant> tenants = tenantRepository.findAll();

                List<AdminUser> primaryUsers = adminUserRepository.findAll().stream()
                                .filter(u -> u.getRole() == AdminRole.ADMIN)
                                .toList();

                Map<String, String> tenantPrimaryEmails = primaryUsers.stream()
                                .collect(Collectors.toMap(
                                                AdminUser::getTenantId,
                                                AdminUser::getEmail,
                                                (existing, replacement) -> existing));

                Map<String, Subscription> subscriptions = subscriptionRepository.findAll().stream()
                                .collect(Collectors.toMap(
                                                Subscription::getTenantId,
                                                s -> s,
                                                (existing, replacement) -> existing));

                return tenants.stream().map(tenant -> {
                        Subscription sub = subscriptions.get(tenant.getId());
                        return TenantOverviewResponse.builder()
                                        .tenantId(tenant.getId())
                                        .name(tenant.getName())
                                        .primaryEmail(tenantPrimaryEmails.getOrDefault(tenant.getId(), "Unknown"))
                                        .plan(sub != null ? sub.getPlan() : null)
                                        .subscriptionStatus(sub != null ? sub.getStatus() : null)
                                        .active(tenant.isActive())
                                        .createdAt(tenant.getCreatedAt() != null ? tenant.getCreatedAt()
                                                        : Instant.now())
                                        .build();
                }).toList();
        }

        @Transactional
        public void suspendTenant(String tenantId) {
                Tenant tenant = tenantRepository.findById(tenantId)
                                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                                                "Tenant not found"));
                tenant.setActive(false);
                tenantRepository.save(tenant);
        }

        @Transactional
        public void activateTenant(String tenantId) {
                Tenant tenant = tenantRepository.findById(tenantId)
                                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                                                "Tenant not found"));
                tenant.setActive(true);
                tenantRepository.save(tenant);
        }

        @Transactional
        public AuthResponse impersonateTenant(String tenantId) {
                AdminUser user = adminUserRepository.findAll().stream()
                                .filter(u -> u.getTenantId().equals(tenantId) && u.getRole() == AdminRole.ADMIN)
                                .findFirst()
                                .orElseThrow(
                                                () -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                                                                "No admin user found for tenant"));

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

        @Transactional
        public void overrideSubscription(String tenantId, OverrideSubscriptionRequest request) {
                Subscription subscription = subscriptionRepository.findByTenantId(tenantId)
                                .orElseThrow(
                                                () -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                                                                "Subscription not found for tenant"));

                subscription.setPlan(request.getPlan());
                subscription.setStatus(SubscriptionStatus.ACTIVE);
                subscription.setCurrentPeriodStart(Instant.now());
                subscription.setCurrentPeriodEnd(Instant.now().plus(365 * 10, ChronoUnit.DAYS)); // 10 years

                subscriptionRepository.save(subscription);
        }

        @Transactional(readOnly = true)
        public com.bracits.surveyengine.admin.dto.SuperAdminMetricsResponse getPlatformMetrics() {
                long totalTenants = tenantRepository.count();
                long activeSubscriptions = subscriptionRepository.countByStatus(SubscriptionStatus.ACTIVE);
                long totalPlatformUsage = surveyResponseRepository.count();

                return com.bracits.surveyengine.admin.dto.SuperAdminMetricsResponse.builder()
                                .totalTenants(totalTenants)
                                .activeSubscriptions(activeSubscriptions)
                                .totalPlatformUsage(totalPlatformUsage)
                                .build();
        }
}
