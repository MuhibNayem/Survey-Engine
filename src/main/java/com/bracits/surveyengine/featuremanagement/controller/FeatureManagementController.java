package com.bracits.surveyengine.featuremanagement.controller;

import com.bracits.surveyengine.admin.context.TenantContext;
import com.bracits.surveyengine.admin.entity.AdminRole;
import com.bracits.surveyengine.featuremanagement.dto.*;
import com.bracits.surveyengine.featuremanagement.entity.FeatureCategory;
import com.bracits.surveyengine.featuremanagement.entity.FeatureEventType;
import com.bracits.surveyengine.featuremanagement.entity.FeatureType;
import com.bracits.surveyengine.featuremanagement.service.FeatureManagementService;
import com.bracits.surveyengine.tenant.dto.TenantDTO;
import com.bracits.surveyengine.tenant.entity.Tenant;
import com.bracits.surveyengine.tenant.service.TenantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Controller for feature management operations.
 * Provides both admin APIs for feature administration and user APIs for feature interaction.
 */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Feature Management", description = "Enterprise feature flag, tour, and tooltip management")
@SecurityRequirement(name = "bearerAuth")
public class FeatureManagementController {

    private final FeatureManagementService featureService;
    private final TenantService tenantService;

    // ========================================================================
    // Admin APIs (SUPER_ADMIN only)
    // ========================================================================

    /**
     * List all features with pagination and optional filters.
     */
    @GetMapping("/admin/features")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "List all features", description = "Get paginated list of all feature definitions. SUPER_ADMIN only.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful response",
            content = @Content(schema = @Schema(implementation = FeatureDefinitionDTO.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - SUPER_ADMIN role required")
    })
    public ResponseEntity<Page<FeatureDefinitionDTO>> listFeatures(
            @Parameter(description = "Filter by category")
            @RequestParam(required = false) FeatureCategory category,
            @Parameter(description = "Filter by type")
            @RequestParam(required = false) FeatureType type,
            @Parameter(description = "Pagination parameters")
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        
        log.debug("Listing features with category={}, type={}, pageable={}", category, type, pageable);
        Page<FeatureDefinitionDTO> features = featureService.listFeatures(category, type, pageable);
        return ResponseEntity.ok(features);
    }

    /**
     * Search features by name or description.
     */
    @GetMapping("/admin/features/search")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Search features", description = "Search features by name, description, or key. SUPER_ADMIN only.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful response"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<Page<FeatureDefinitionDTO>> searchFeatures(
            @Parameter(description = "Search term", required = true)
            @RequestParam String q,
            @Parameter(description = "Pagination parameters")
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        
        log.debug("Searching features with query={}", q);
        Page<FeatureDefinitionDTO> features = featureService.searchFeatures(q, pageable);
        return ResponseEntity.ok(features);
    }

    /**
     * Get a specific feature by key.
     */
    @GetMapping("/admin/features/{featureKey}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Get feature", description = "Get detailed information about a specific feature. SUPER_ADMIN only.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful response"),
        @ApiResponse(responseCode = "404", description = "Feature not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<FeatureDefinitionDTO> getFeature(
            @Parameter(description = "Feature key", required = true)
            @PathVariable String featureKey) {
        
        log.debug("Getting feature {}", featureKey);
        FeatureDefinitionDTO feature = featureService.getFeature(featureKey);
        return ResponseEntity.ok(feature);
    }

    /**
     * Create a new feature definition.
     */
    @PostMapping("/admin/features")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Create feature", description = "Create a new feature definition. SUPER_ADMIN only.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Feature created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input",
            content = @Content(schema = @Schema(example = "{\"error\": \"VALIDATION_ERROR\", \"message\": \"...\"}"))),
        @ApiResponse(responseCode = "409", description = "Feature already exists"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<FeatureDefinitionDTO> createFeature(
            @Parameter(description = "Feature creation request", required = true)
            @Valid @RequestBody CreateFeatureRequest request) {
        
        log.info("Creating feature {} by {}", request.getFeatureKey(), getCurrentUser());
        FeatureDefinitionDTO feature = featureService.createFeature(request, getCurrentUser());
        return ResponseEntity.status(HttpStatus.CREATED).body(feature);
    }

    /**
     * Update an existing feature definition.
     */
    @PutMapping("/admin/features/{featureKey}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Update feature", description = "Update an existing feature definition. SUPER_ADMIN only.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Feature updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "404", description = "Feature not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<FeatureDefinitionDTO> updateFeature(
            @Parameter(description = "Feature key", required = true)
            @PathVariable String featureKey,
            @Parameter(description = "Feature update request", required = true)
            @Valid @RequestBody UpdateFeatureRequest request) {
        
        log.info("Updating feature {} by {}", featureKey, getCurrentUser());
        FeatureDefinitionDTO feature = featureService.updateFeature(featureKey, request, getCurrentUser());
        return ResponseEntity.ok(feature);
    }

    /**
     * Delete a feature definition.
     */
    @DeleteMapping("/admin/features/{featureKey}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Delete feature", description = "Delete a feature definition. SUPER_ADMIN only.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Feature deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Feature not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<Void> deleteFeature(
            @Parameter(description = "Feature key", required = true)
            @PathVariable String featureKey) {
        
        log.info("Deleting feature {} by {}", featureKey, getCurrentUser());
        featureService.deleteFeature(featureKey);
        return ResponseEntity.noContent().build();
    }

    /**
     * Bulk create/update features.
     */
    @PostMapping("/admin/features/bulk")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Bulk update features", description = "Bulk create or update multiple features. SUPER_ADMIN only.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Bulk operation completed",
            content = @Content(schema = @Schema(implementation = BulkFeatureResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<BulkFeatureResponse> bulkUpdateFeatures(
            @Parameter(description = "List of feature creation requests", required = true)
            @Valid @RequestBody List<CreateFeatureRequest> requests) {
        
        log.info("Bulk updating {} features by {}", requests.size(), getCurrentUser());
        BulkFeatureResponse response = featureService.bulkUpdateFeatures(requests, getCurrentUser());
        return ResponseEntity.ok(response);
    }

    /**
     * Configure feature for a specific tenant.
     */
    @PostMapping("/admin/features/{featureKey}/tenants/{tenantId}/configure")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Configure feature for tenant", description = "Set tenant-specific feature configuration. SUPER_ADMIN only.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Configuration saved successfully"),
        @ApiResponse(responseCode = "404", description = "Feature or tenant not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<TenantFeatureConfigDTO> configureFeatureForTenant(
            @Parameter(description = "Feature key", required = true)
            @PathVariable String featureKey,
            @Parameter(description = "Tenant ID", required = true)
            @PathVariable String tenantId,
            @Parameter(description = "Configuration settings", required = true)
            @Valid @RequestBody TenantFeatureConfigDTO config) {
        
        log.info("Configuring feature {} for tenant {} by {}", featureKey, tenantId, getCurrentUser());
        TenantFeatureConfigDTO result = featureService.configureFeatureForTenant(tenantId, featureKey, config);
        return ResponseEntity.ok(result);
    }

    /**
     * Get all tenant configurations for a feature.
     */
    @GetMapping("/admin/features/{featureKey}/tenants")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "List tenant configurations", description = "Get all tenant-specific configurations for a feature. SUPER_ADMIN only.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful response"),
        @ApiResponse(responseCode = "404", description = "Feature not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<List<TenantFeatureConfigDTO>> getTenantConfigurations(
            @Parameter(description = "Feature key", required = true)
            @PathVariable String featureKey) {
        
        log.debug("Getting tenant configurations for feature {}", featureKey);
        List<TenantFeatureConfigDTO> configs = featureService.getTenantConfigurations(featureKey);
        return ResponseEntity.ok(configs);
    }

    /**
     * Get tenant configuration for a specific feature.
     */
    @GetMapping("/admin/features/{featureKey}/tenants/{tenantId}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Get tenant configuration", description = "Get tenant-specific configuration for a feature. SUPER_ADMIN only.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful response"),
        @ApiResponse(responseCode = "404", description = "Configuration not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<TenantFeatureConfigDTO> getTenantConfiguration(
            @Parameter(description = "Feature key", required = true)
            @PathVariable String featureKey,
            @Parameter(description = "Tenant ID", required = true)
            @PathVariable String tenantId) {
        
        log.debug("Getting tenant configuration for feature {} and tenant {}", featureKey, tenantId);
        return featureService.getTenantConfiguration(tenantId, featureKey)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Remove tenant configuration for a feature.
     */
    @DeleteMapping("/admin/features/{featureKey}/tenants/{tenantId}/configure")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Remove tenant configuration", description = "Remove tenant-specific configuration (revert to global). SUPER_ADMIN only.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Configuration removed successfully"),
        @ApiResponse(responseCode = "404", description = "Feature not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<Void> removeTenantConfiguration(
            @Parameter(description = "Feature key", required = true)
            @PathVariable String featureKey,
            @Parameter(description = "Tenant ID", required = true)
            @PathVariable String tenantId) {
        
        log.info("Removing tenant configuration for feature {} and tenant {} by {}", featureKey, tenantId, getCurrentUser());
        featureService.removeTenantConfiguration(tenantId, featureKey);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get analytics for a specific feature.
     */
    @GetMapping("/admin/features/{featureKey}/analytics")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Get feature analytics", description = "Get usage statistics and completion metrics for a feature. SUPER_ADMIN only.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful response",
            content = @Content(schema = @Schema(implementation = FeatureAnalyticsDTO.class))),
        @ApiResponse(responseCode = "404", description = "Feature not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<FeatureAnalyticsDTO> getFeatureAnalytics(
            @Parameter(description = "Feature key", required = true)
            @PathVariable String featureKey) {
        
        log.debug("Getting analytics for feature {}", featureKey);
        FeatureAnalyticsDTO analytics = featureService.getFeatureAnalytics(featureKey);
        return ResponseEntity.ok(analytics);
    }

    // ========================================================================
    // User APIs (Any authenticated user)
    // ========================================================================

    /**
     * Get available features for the current user.
     */
    @GetMapping("/features/available")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR', 'VIEWER')")
    @Operation(summary = "Get available features", description = "Get all available (incomplete) features for the current user.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful response"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<List<FeatureDefinitionDTO>> getAvailableFeatures(
            @Parameter(description = "Filter by category")
            @RequestParam(required = false) FeatureCategory category) {
        
        UUID userId = getCurrentUserId();
        log.debug("Getting available features for user {} with category={}", userId, category);
        List<FeatureDefinitionDTO> features = featureService.getAvailableFeatures(userId, category);
        return ResponseEntity.ok(features);
    }

    /**
     * Check feature availability status for the current user.
     */
    @GetMapping("/features/{featureKey}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR', 'VIEWER')")
    @Operation(summary = "Get feature status", description = "Check feature availability and completion status for the current user.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful response",
            content = @Content(schema = @Schema(implementation = FeatureStatusDTO.class))),
        @ApiResponse(responseCode = "404", description = "Feature not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<FeatureStatusDTO> getFeatureStatus(
            @Parameter(description = "Feature key", required = true)
            @PathVariable String featureKey) {
        
        UUID userId = getCurrentUserId();
        log.debug("Getting feature status for user {} and feature {}", userId, featureKey);
        FeatureStatusDTO status = featureService.isFeatureAvailable(userId, featureKey);
        return ResponseEntity.ok(status);
    }

    /**
     * Check if a feature is completed for the current user.
     */
    @GetMapping("/features/{featureKey}/completed")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR', 'VIEWER')")
    @Operation(summary = "Check feature completion", description = "Check if the current user has completed a feature.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful response"),
        @ApiResponse(responseCode = "404", description = "Feature not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<Map<String, Boolean>> isFeatureCompleted(
            @Parameter(description = "Feature key", required = true)
            @PathVariable String featureKey) {
        
        UUID userId = getCurrentUserId();
        log.debug("Checking feature completion for user {} and feature {}", userId, featureKey);
        boolean completed = featureService.isFeatureCompleted(userId, featureKey);
        return ResponseEntity.ok(Map.of("completed", completed));
    }

    /**
     * Mark a feature as completed for the current user.
     */
    @PostMapping("/features/{featureKey}/complete")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR', 'VIEWER')")
    @Operation(summary = "Complete feature", description = "Mark a feature as completed for the current user.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Feature marked as completed"),
        @ApiResponse(responseCode = "404", description = "Feature not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<FeatureStatusDTO> completeFeature(
            @Parameter(description = "Feature key", required = true)
            @PathVariable String featureKey,
            @Parameter(description = "Completion status")
            @RequestParam(required = false) Boolean completed,
            @RequestBody(required = false) Map<String, Object> body) {
        
        UUID userId = getCurrentUserId();
        boolean completionValue = resolveCompleted(completed, body);
        log.info("Marking feature {} as completed={} for user {}", featureKey, completionValue, userId);
        
        if (completionValue) {
            FeatureStatusDTO status = featureService.completeFeature(userId, featureKey);
            return ResponseEntity.ok(status);
        } else {
            featureService.resetFeature(userId, featureKey);
            return ResponseEntity.ok(FeatureStatusDTO.builder()
                .featureKey(featureKey)
                .completed(false)
                .shouldShow(true)
                .build());
        }
    }

    /**
     * Record feature access (without marking as completed).
     */
    @PostMapping("/features/{featureKey}/access")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR', 'VIEWER')")
    @Operation(summary = "Record feature access", description = "Record that the current user has accessed a feature.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Access recorded successfully"),
        @ApiResponse(responseCode = "404", description = "Feature not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<Void> recordFeatureAccess(
            @Parameter(description = "Feature key", required = true)
            @PathVariable String featureKey) {
        
        UUID userId = getCurrentUserId();
        log.debug("Recording feature access for user {} and feature {}", userId, featureKey);
        featureService.recordFeatureAccess(userId, featureKey);
        return ResponseEntity.ok().build();
    }

    /**
     * Reset a feature's completion status for the current user.
     */
    @PostMapping("/features/{featureKey}/reset")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR', 'VIEWER')")
    @Operation(summary = "Reset feature completion", description = "Reset a feature's completion status for the current user.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Feature reset successfully"),
        @ApiResponse(responseCode = "404", description = "Feature not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<Void> resetFeature(
            @Parameter(description = "Feature key", required = true)
            @PathVariable String featureKey) {
        
        UUID userId = getCurrentUserId();
        log.info("Resetting feature {} for user {}", featureKey, userId);
        featureService.resetFeature(userId, featureKey);
        return ResponseEntity.ok().build();
    }

    /**
     * Get runtime-resolved features for frontend orchestration.
     */
    @GetMapping("/features/runtime")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR', 'VIEWER')")
    public ResponseEntity<RuntimeFeaturesResponse> getRuntimeFeatures(
            @RequestParam(value = "pagePath", required = false) String pagePath,
            @RequestParam(value = "platform", defaultValue = "WEB") String platform,
            @RequestParam(value = "max", defaultValue = "20") Integer maxItems) {
        UUID userId = getCurrentUserId();
        return ResponseEntity.ok(featureService.getRuntimeFeatures(userId, pagePath, platform, maxItems));
    }

    /**
     * Record frontend runtime events for orchestration and analytics.
     */
    @PostMapping("/features/{featureKey}/events")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR', 'VIEWER')")
    public ResponseEntity<Void> recordFeatureEvent(
            @PathVariable String featureKey,
            @Valid @RequestBody FeatureEventRequest request) {
        UUID userId = getCurrentUserId();
        FeatureEventType eventType;
        try {
            eventType = FeatureEventType.valueOf(request.getEventType().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new com.bracits.surveyengine.common.exception.BusinessException(
                com.bracits.surveyengine.common.exception.ErrorCode.VALIDATION_FAILED,
                "Invalid eventType: " + request.getEventType());
        }
        featureService.recordFeatureEvent(userId, featureKey, eventType, request.getMetadata());
        return ResponseEntity.accepted().build();
    }

    // ========================================================================
    // Helper Methods
    // ========================================================================

    /**
     * Get current user ID from security context.
     */
    private UUID getCurrentUserId() {
        TenantContext.TenantInfo context = TenantContext.get();
        if (context == null || context.userId() == null) {
            throw new IllegalStateException("User ID not found in security context");
        }
        try {
            return UUID.fromString(context.userId());
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("Invalid user ID format: " + context.userId(), e);
        }
    }

    /**
     * Get current user email from security context.
     */
    private String getCurrentUser() {
        TenantContext.TenantInfo context = TenantContext.get();
        if (context == null || context.email() == null) {
            return "UNKNOWN";
        }
        return context.email();
    }

    private boolean resolveCompleted(Boolean completed, Map<String, Object> body) {
        if (completed != null) {
            return completed;
        }
        if (body != null && body.containsKey("completed")) {
            Object raw = body.get("completed");
            if (raw instanceof Boolean b) {
                return b;
            }
            return Boolean.parseBoolean(String.valueOf(raw));
        }
        return true;
    }

    // ========================================================================
    // Tenant APIs (SUPER_ADMIN only)
    // ========================================================================

    /**
     * List all tenants for feature configuration.
     */
    @GetMapping("/admin/tenants")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "List tenants", description = "Get list of all tenants for feature configuration. Supports search by name. SUPER_ADMIN only.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful response",
            content = @Content(schema = @Schema(implementation = TenantDTO.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - SUPER_ADMIN role required")
    })
    public ResponseEntity<org.springframework.data.domain.Page<TenantDTO>> listTenants(
            @Parameter(description = "Search term for tenant name")
            @RequestParam(required = false) String search,
            @Parameter(description = "Page number (0-indexed)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "20") int size) {

        log.debug("Listing tenants with search={}, page={}, size={}", search, page, size);
        
        org.springframework.data.domain.Pageable pageable = 
            org.springframework.data.domain.PageRequest.of(page, size);
        
        org.springframework.data.domain.Page<Tenant> tenantPage;
        
        if (search != null && !search.isBlank()) {
            List<Tenant> tenants = tenantService.searchByName(search);
            // Apply pagination manually
            int start = page * size;
            int end = Math.min(start + size, tenants.size());
            
            List<Tenant> pagedTenants = tenants.subList(start, end);
            tenantPage = new org.springframework.data.domain.PageImpl<>(
                pagedTenants, 
                pageable, 
                tenants.size()
            );
        } else {
            tenantPage = new org.springframework.data.domain.PageImpl<>(
                tenantService.findAll(), 
                pageable, 
                tenantService.findAll().size()
            );
        }

        org.springframework.data.domain.Page<TenantDTO> tenantDTOs = tenantPage.map(tenant -> 
            TenantDTO.builder()
                    .id(tenant.getId())
                    .name(tenant.getName())
                    .active(tenant.isActive())
                    .build()
        );

        return ResponseEntity.ok(tenantDTOs);
    }
}
