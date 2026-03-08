package com.bracits.surveyengine.admin.controller;

import com.bracits.surveyengine.admin.dto.SuperAdminMetricsResponse;
import com.bracits.surveyengine.admin.service.TenantAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Backward-compatible metrics endpoint for older super-admin UI bundles.
 */
@RestController
@RequestMapping("/api/v1/admin/superadmin")
@PreAuthorize("hasRole('SUPER_ADMIN')")
@RequiredArgsConstructor
public class SuperAdminMetricsController {

    private final TenantAdminService tenantAdminService;

    @GetMapping("/metrics")
    public ResponseEntity<SuperAdminMetricsResponse> getPlatformMetrics() {
        return ResponseEntity.ok(tenantAdminService.getPlatformMetrics());
    }
}
