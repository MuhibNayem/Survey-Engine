package com.bracits.surveyengine.subscription.service;

import com.bracits.surveyengine.admin.context.TenantContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class SubscriptionEnforcementFilter extends OncePerRequestFilter {

    private final SubscriptionService subscriptionService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!requiresSubscription(request) || authentication == null || isSuperAdmin(authentication)) {
            filterChain.doFilter(request, response);
            return;
        }

        String tenantId = TenantContext.getTenantId();
        if (tenantId == null || tenantId.isBlank() || !subscriptionService.hasActiveSubscription(tenantId)) {
            response.sendError(HttpServletResponse.SC_PAYMENT_REQUIRED,
                    "Active subscription is required for tenant operations");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean isSuperAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_SUPER_ADMIN".equals(authority.getAuthority()));
    }

    private boolean requiresSubscription(HttpServletRequest request) {
        String path = request.getRequestURI();
        if (path.startsWith("/api/v1/admin/auth")) {
            return false;
        }
        if (path.startsWith("/api/v1/admin/subscriptions")) {
            return false;
        }
        if (path.startsWith("/api/v1/admin/plans")) {
            return false;
        }
        if (path.startsWith("/api/v1/auth/validate")) {
            return false;
        }
        if (path.equals("/api/v1/responses")) {
            return false;
        }
        if (path.startsWith("/actuator")) {
            return false;
        }
        return path.startsWith("/api/v1/");
    }
}
