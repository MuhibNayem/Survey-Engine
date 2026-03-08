package com.bracits.surveyengine.admin.controller;

import com.bracits.surveyengine.admin.dto.AuthResponse;
import com.bracits.surveyengine.admin.dto.AuthUserResponse;
import com.bracits.surveyengine.admin.dto.OverrideSubscriptionRequest;
import com.bracits.surveyengine.admin.dto.TenantOverviewResponse;
import com.bracits.surveyengine.admin.service.TenantAdminService;
import com.bracits.surveyengine.admin.util.CookieUtil;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/superadmin/tenants")
@PreAuthorize("hasRole('SUPER_ADMIN')")
@RequiredArgsConstructor
public class TenantAdminController {

    private final TenantAdminService tenantAdminService;
    private final CookieUtil cookieUtil;

    @GetMapping
    public ResponseEntity<List<TenantOverviewResponse>> getAllTenants() {
        return ResponseEntity.ok(tenantAdminService.getAllTenants());
    }

    @PutMapping("/{tenantId}/suspend")
    public ResponseEntity<Void> suspendTenant(@PathVariable String tenantId) {
        tenantAdminService.suspendTenant(tenantId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{tenantId}/activate")
    public ResponseEntity<Void> activateTenant(@PathVariable String tenantId) {
        tenantAdminService.activateTenant(tenantId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{tenantId}/impersonate")
    public ResponseEntity<AuthUserResponse> impersonateTenant(@PathVariable String tenantId,
            HttpServletResponse response) {
        AuthResponse authResponse = tenantAdminService.impersonateTenant(tenantId);
        cookieUtil.addTokenCookies(response, authResponse);
        return ResponseEntity.ok(AuthUserResponse.builder()
                .userId(authResponse.getUserId())
                .email(authResponse.getEmail())
                .fullName(authResponse.getFullName())
                .tenantId(authResponse.getTenantId())
                .role(authResponse.getRole())
                .build());
    }

    @PostMapping("/{tenantId}/subscriptions/override")
    public ResponseEntity<Void> overrideSubscription(@PathVariable String tenantId,
            @Valid @RequestBody OverrideSubscriptionRequest request) {
        tenantAdminService.overrideSubscription(tenantId, request);
        return ResponseEntity.noContent().build();
    }
}
