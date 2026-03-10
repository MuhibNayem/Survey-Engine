package com.bracits.surveyengine.featuremanagement.service;

import com.bracits.surveyengine.admin.entity.AdminRole;
import com.bracits.surveyengine.admin.entity.AdminUser;
import com.bracits.surveyengine.admin.repository.AdminUserRepository;
import com.bracits.surveyengine.admin.context.TenantContext;
import com.bracits.surveyengine.common.exception.BusinessException;
import com.bracits.surveyengine.common.exception.ErrorCode;
import com.bracits.surveyengine.featuremanagement.dto.*;
import com.bracits.surveyengine.featuremanagement.entity.*;
import com.bracits.surveyengine.featuremanagement.repository.*;
import com.bracits.surveyengine.subscription.entity.Subscription;
import com.bracits.surveyengine.subscription.entity.SubscriptionPlan;
import com.bracits.surveyengine.subscription.repository.SubscriptionRepository;
import com.bracits.surveyengine.subscription.service.PlanCatalogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Core service for feature management operations.
 * Handles feature availability checks, completion tracking, tenant configuration, and analytics.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class FeatureManagementService {

    private final FeatureDefinitionRepository featureDefinitionRepository;
    private final TenantFeatureConfigRepository tenantFeatureConfigRepository;
    private final UserFeatureAccessRepository userFeatureAccessRepository;
    private final AdminUserRepository adminUserRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final PlanCatalogService planCatalogService;
    private final ModelMapper modelMapper;

    // ------------------------------------------------------------------------
    // Feature Availability Checks
    // ------------------------------------------------------------------------

    /**
     * Check if a feature is available to a specific user.
     * Considers: global enabled status, tenant config, plan requirements, role permissions, rollout percentage.
     *
     * @param userId    User ID to check
     * @param featureKey Feature key to check
     * @return FeatureStatusDTO with availability information
     */
    @Transactional(readOnly = true)
    public FeatureStatusDTO isFeatureAvailable(UUID userId, String featureKey) {
        log.debug("Checking feature availability for user {} and feature {}", userId, featureKey);

        // Get feature definition
        FeatureDefinition feature = featureDefinitionRepository.findByFeatureKey(featureKey)
            .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                "Feature not found: " + featureKey));

        // Check global enabled status
        if (!feature.isEnabled()) {
            log.debug("Feature {} is globally disabled", featureKey);
            return FeatureStatusDTO.disabled(feature.getId(), featureKey, feature.getName());
        }

        // Get user details
        AdminUser user = adminUserRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                "User not found: " + userId));

        // Check role permissions
        if (!feature.isRoleAllowed(user.getRole())) {
            log.debug("Feature {} not allowed for role {}", featureKey, user.getRole());
            return FeatureStatusDTO.roleRestricted(feature.getId(), featureKey, feature.getName());
        }

        // Check plan requirements
        SubscriptionPlan userPlan = getUserPlan(user.getTenantId());
        if (!isPlanSufficient(userPlan, feature.getMinPlan())) {
            log.debug("Feature {} requires plan {} but user has {}", featureKey, feature.getMinPlan(), userPlan);
            return FeatureStatusDTO.planRestricted(feature.getId(), featureKey, feature.getName(), feature.getMinPlan().name());
        }

        // Check tenant-specific configuration
        Optional<TenantFeatureConfig> tenantConfig = tenantFeatureConfigRepository.findByTenantIdAndFeatureId(
            user.getTenantId(), feature.getId());
        if (tenantConfig.isPresent() && !tenantConfig.get().isEnabled()) {
            log.debug("Feature {} is disabled for tenant {}", featureKey, user.getTenantId());
            return FeatureStatusDTO.disabled(feature.getId(), featureKey, feature.getName());
        }

        // Check rollout percentage
        Integer effectiveRollout = tenantConfig
            .map(TenantFeatureConfig::getEffectiveRolloutPercentage)
            .orElse(feature.getRolloutPercentage());
        if (!isInRollout(userId, effectiveRollout)) {
            log.debug("User {} excluded from rollout for feature {} ({}%)", userId, featureKey, effectiveRollout);
            return FeatureStatusDTO.rolloutExcluded(feature.getId(), featureKey, feature.getName(), effectiveRollout);
        }

        // Get user's access status
        Optional<UserFeatureAccess> accessOpt = userFeatureAccessRepository.findByUserIdAndFeatureId(userId, feature.getId());
        boolean accessed = accessOpt.map(UserFeatureAccess::isAccessed).orElse(false);
        boolean completed = accessOpt.map(UserFeatureAccess::isCompleted).orElse(false);

        log.debug("Feature {} is available to user {} (accessed={}, completed={})", featureKey, userId, accessed, completed);
        return FeatureStatusDTO.available(feature.getId(), featureKey, feature.getName(), 
            feature.getMetadata(), accessed, completed);
    }

    /**
     * Check if a user has completed a feature.
     *
     * @param userId    User ID to check
     * @param featureKey Feature key to check
     * @return true if feature is completed
     */
    @Transactional(readOnly = true)
    public boolean isFeatureCompleted(UUID userId, String featureKey) {
        return userFeatureAccessRepository.findByUserIdAndFeatureKey(userId, featureKey)
            .map(UserFeatureAccess::isCompleted)
            .orElse(false);
    }

    /**
     * Get all available (incomplete) features for a user.
     * Optionally filtered by category.
     *
     * @param userId   User ID
     * @param category Optional category filter
     * @return List of available features
     */
    @Transactional(readOnly = true)
    public List<FeatureDefinitionDTO> getAvailableFeatures(UUID userId, FeatureCategory category) {
        AdminUser user = adminUserRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                "User not found: " + userId));

        SubscriptionPlan userPlan = getUserPlan(user.getTenantId());
        String tenantId = user.getTenantId();
        String planString = userPlan != null ? userPlan.name() : "BASIC";

        // Get features accessible by user's plan
        List<FeatureDefinition> features = category != null
            ? featureDefinitionRepository.findByCategoryAndMinPlanLevelAndEnabledTrue(category, planString)
            : featureDefinitionRepository.findByMinPlanLevelAndEnabledTrue(planString);

        // Get user's completed features
        List<UserFeatureAccess> completedAccess = userFeatureAccessRepository.findByUserIdAndCompletedTrue(userId);
        Set<UUID> completedFeatureIds = completedAccess.stream()
            .map(a -> a.getFeature().getId())
            .collect(Collectors.toSet());

        // Filter features
        return features.stream()
            .filter(f -> f.isRoleAllowed(user.getRole()))
            .filter(f -> !completedFeatureIds.contains(f.getId()))
            .filter(f -> {
                // Check tenant config
                Optional<TenantFeatureConfig> tenantConfig = tenantFeatureConfigRepository.findByTenantIdAndFeatureId(tenantId, f.getId());
                return tenantConfig.map(TenantFeatureConfig::isEnabled).orElse(true);
            })
            .filter(f -> isInRollout(userId, tenantFeatureConfigRepository.findByTenantIdAndFeatureId(tenantId, f.getId())
                .map(TenantFeatureConfig::getEffectiveRolloutPercentage)
                .orElse(f.getRolloutPercentage())))
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    // ------------------------------------------------------------------------
    // Feature Completion Tracking
    // ------------------------------------------------------------------------

    /**
     * Mark a feature as completed for a user.
     *
     * @param userId    User ID
     * @param featureKey Feature key
     * @return Updated FeatureStatusDTO
     */
    @Transactional
    public FeatureStatusDTO completeFeature(UUID userId, String featureKey) {
        log.info("Marking feature {} as completed for user {}", featureKey, userId);

        FeatureDefinition feature = featureDefinitionRepository.findByFeatureKey(featureKey)
            .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                "Feature not found: " + featureKey));

        UserFeatureAccess access = userFeatureAccessRepository.findByUserIdAndFeatureId(userId, feature.getId())
            .orElseGet(() -> {
                // Create new access record
                AdminUser user = adminUserRepository.findById(userId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                        "User not found: " + userId));
                return UserFeatureAccess.builder()
                    .userId(userId)
                    .tenantId(user.getTenantId())
                    .feature(feature)
                    .accessed(true)
                    .completed(false)
                    .accessCount(1)
                    .lastAccessedAt(Instant.now())
                    .build();
            });

        access.markCompleted();
        userFeatureAccessRepository.save(access);

        return toStatusDTO(feature, access);
    }

    /**
     * Reset a feature's completion status for a user.
     *
     * @param userId    User ID
     * @param featureKey Feature key
     */
    @Transactional
    public void resetFeature(UUID userId, String featureKey) {
        log.info("Resetting feature {} for user {}", featureKey, userId);

        FeatureDefinition feature = featureDefinitionRepository.findByFeatureKey(featureKey)
            .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                "Feature not found: " + featureKey));

        userFeatureAccessRepository.findByUserIdAndFeatureId(userId, feature.getId())
            .ifPresent(access -> {
                access.reset();
                userFeatureAccessRepository.save(access);
            });
    }

    /**
     * Record feature access (without marking as completed).
     *
     * @param userId    User ID
     * @param featureKey Feature key
     */
    @Transactional
    public void recordFeatureAccess(UUID userId, String featureKey) {
        FeatureDefinition feature = featureDefinitionRepository.findByFeatureKey(featureKey)
            .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                "Feature not found: " + featureKey));

        AdminUser user = adminUserRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                "User not found: " + userId));

        UserFeatureAccess access = userFeatureAccessRepository.findByUserIdAndFeatureId(userId, feature.getId())
            .orElseGet(() -> UserFeatureAccess.builder()
                .userId(userId)
                .tenantId(user.getTenantId())
                .feature(feature)
                .accessed(false)
                .completed(false)
                .accessCount(0)
                .build());

        access.recordAccess();
        userFeatureAccessRepository.save(access);
    }

    /**
     * Runtime delivery API for frontend onboarding orchestration.
     * Applies scheduling, targeting, platform/page filters, priority ordering, and
     * impression throttling.
     */
    @Transactional(readOnly = true)
    public RuntimeFeaturesResponse getRuntimeFeatures(
        UUID userId,
        String pagePath,
        String platform,
        Integer maxItems
    ) {
        AdminUser user = adminUserRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                "User not found: " + userId));

        String tenantId = user.getTenantId();
        String planString = getUserPlan(tenantId).name();
        Instant now = Instant.now();
        int cap = Math.min(Math.max(maxItems != null ? maxItems : 20, 1), 100);
        String normalizedPlatform = StringUtils.hasText(platform) ? platform.toUpperCase(Locale.ROOT) : "WEB";
        String normalizedPath = normalizePath(pagePath);

        List<FeatureDefinition> features = featureDefinitionRepository.findByMinPlanLevelAndEnabledTrue(planString);
        Map<UUID, UserFeatureAccess> accessByFeatureId = userFeatureAccessRepository.findByUserId(userId).stream()
            .collect(Collectors.toMap(a -> a.getFeature().getId(), a -> a, (a, b) -> a));

        List<RuntimeFeatureItemDTO> candidates = new ArrayList<>();
        for (FeatureDefinition feature : features) {
            if (!feature.isRoleAllowed(user.getRole())) {
                continue;
            }
            if (!feature.isPlatformSupported(normalizedPlatform)) {
                continue;
            }
            Optional<TenantFeatureConfig> tenantConfig = tenantFeatureConfigRepository.findByTenantIdAndFeatureId(
                tenantId, feature.getId());
            if (tenantConfig.isPresent() && !tenantConfig.get().isEnabled()) {
                continue;
            }
            if (!isInRollout(userId, tenantConfig.map(TenantFeatureConfig::getEffectiveRolloutPercentage)
                .orElse(feature.getRolloutPercentage()))) {
                continue;
            }

            UserFeatureAccess access = accessByFeatureId.get(feature.getId());
            if (access != null && access.isCompleted()) {
                continue;
            }

            Map<String, Object> metadata = safeMetadata(feature.getMetadata());
            if (!isPublished(metadata)) {
                continue;
            }
            if (!isWithinSchedule(metadata, now)) {
                continue;
            }
            if (!matchesPath(metadata, normalizedPath)) {
                continue;
            }
            if (!matchesTargeting(metadata, user, tenantId)) {
                continue;
            }
            if (!passesFrequencyControls(metadata, access, now)) {
                continue;
            }

            candidates.add(RuntimeFeatureItemDTO.builder()
                .featureId(feature.getId())
                .featureKey(feature.getFeatureKey())
                .name(feature.getName())
                .description(feature.getDescription())
                .featureType(feature.getFeatureType())
                .surface(resolveSurface(feature.getFeatureType(), metadata))
                .priority(parseInt(metadata.get("priority"), 1000))
                .blocking(parseBoolean(metadata.get("blocking"), feature.getFeatureType() == FeatureType.TOUR))
                .shouldShow(true)
                .metadata(metadata)
                .build());
        }

        candidates.sort(Comparator
            .comparing((RuntimeFeatureItemDTO i) -> i.getPriority() != null ? i.getPriority() : 1000)
            .thenComparing(RuntimeFeatureItemDTO::getFeatureKey));

        List<RuntimeFeatureItemDTO> orchestrated = orchestrateSurfaces(candidates, cap);

        return RuntimeFeaturesResponse.builder()
            .items(orchestrated)
            .generatedAt(now)
            .build();
    }

    @Transactional
    public void recordFeatureEvent(UUID userId, String featureKey, FeatureEventType eventType, Map<String, Object> payload) {
        FeatureDefinition feature = featureDefinitionRepository.findByFeatureKey(featureKey)
            .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                "Feature not found: " + featureKey));

        AdminUser user = adminUserRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                "User not found: " + userId));

        UserFeatureAccess access = userFeatureAccessRepository.findByUserIdAndFeatureId(userId, feature.getId())
            .orElseGet(() -> UserFeatureAccess.builder()
                .userId(userId)
                .tenantId(user.getTenantId())
                .feature(feature)
                .accessed(false)
                .completed(false)
                .accessCount(0)
                .metadata(new HashMap<>())
                .build());

        access.recordAccess();
        Map<String, Object> metadata = access.getMetadata() != null ? access.getMetadata() : new HashMap<>();
        metadata.put("lastEventAt", Instant.now().toString());
        metadata.put("lastEventType", eventType.name());
        if (payload != null && !payload.isEmpty()) {
            metadata.put("lastEventPayload", payload);
            if (payload.get("stepIndex") instanceof Number stepIndex) {
                metadata.put("currentStep", stepIndex.intValue());
            }
        }
        incrementEventCounter(metadata, eventType.name());
        access.setMetadata(metadata);

        if (eventType == FeatureEventType.COMPLETE || eventType == FeatureEventType.DISMISS) {
            access.markCompleted();
        } else if (eventType == FeatureEventType.RESET) {
            access.reset();
        }

        userFeatureAccessRepository.save(access);
    }

    // ------------------------------------------------------------------------
    // Tenant Configuration
    // ------------------------------------------------------------------------

    /**
     * Configure a feature for a specific tenant.
     *
     * @param tenantId  Tenant ID
     * @param featureKey Feature key
     * @param config    Configuration DTO
     * @return Updated TenantFeatureConfigDTO
     */
    @Transactional
    public TenantFeatureConfigDTO configureFeatureForTenant(String tenantId, String featureKey, TenantFeatureConfigDTO config) {
        log.info("Configuring feature {} for tenant {}", featureKey, tenantId);

        FeatureDefinition feature = featureDefinitionRepository.findByFeatureKey(featureKey)
            .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                "Feature not found: " + featureKey));

        TenantFeatureConfig tenantConfig = tenantFeatureConfigRepository.findByTenantIdAndFeatureId(tenantId, feature.getId())
            .orElseGet(() -> TenantFeatureConfig.builder()
                .tenantId(tenantId)
                .feature(feature)
                .enabled(config.getEnabled() != null ? config.getEnabled() : true)
                .rolloutPercentage(config.getRolloutPercentage())
                .customMetadata(config.getCustomMetadata() != null ? config.getCustomMetadata() : new HashMap<>())
                .build());

        // Update fields if provided
        if (config.getEnabled() != null) {
            tenantConfig.setEnabled(config.getEnabled());
        }
        if (config.getRolloutPercentage() != null) {
            tenantConfig.setRolloutPercentage(config.getRolloutPercentage());
        }
        if (config.getCustomMetadata() != null) {
            tenantConfig.setCustomMetadata(config.getCustomMetadata());
        }

        tenantFeatureConfigRepository.save(tenantConfig);
        return toTenantConfigDTO(tenantConfig);
    }

    /**
     * Get all tenant configurations for a feature.
     *
     * @param featureKey Feature key
     * @return List of tenant configurations
     */
    @Transactional(readOnly = true)
    public List<TenantFeatureConfigDTO> getTenantConfigurations(String featureKey) {
        FeatureDefinition feature = featureDefinitionRepository.findByFeatureKey(featureKey)
            .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                "Feature not found: " + featureKey));

        return tenantFeatureConfigRepository.findByFeatureId(feature.getId()).stream()
            .map(this::toTenantConfigDTO)
            .collect(Collectors.toList());
    }

    /**
     * Get tenant configuration for a specific feature.
     *
     * @param tenantId  Tenant ID
     * @param featureKey Feature key
     * @return Tenant configuration DTO
     */
    @Transactional(readOnly = true)
    public Optional<TenantFeatureConfigDTO> getTenantConfiguration(String tenantId, String featureKey) {
        return tenantFeatureConfigRepository.findByTenantIdAndFeatureKey(tenantId, featureKey)
            .map(this::toTenantConfigDTO);
    }

    /**
     * Remove tenant configuration for a feature (revert to global settings).
     *
     * @param tenantId  Tenant ID
     * @param featureKey Feature key
     */
    @Transactional
    public void removeTenantConfiguration(String tenantId, String featureKey) {
        FeatureDefinition feature = featureDefinitionRepository.findByFeatureKey(featureKey)
            .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                "Feature not found: " + featureKey));

        tenantFeatureConfigRepository.deleteByTenantIdAndFeatureId(tenantId, feature.getId());
    }

    // ------------------------------------------------------------------------
    // Analytics
    // ------------------------------------------------------------------------

    /**
     * Get analytics for a specific feature.
     *
     * @param featureKey Feature key
     * @return Feature analytics DTO
     */
    @Transactional(readOnly = true)
    public FeatureAnalyticsDTO getFeatureAnalytics(String featureKey) {
        FeatureDefinition feature = featureDefinitionRepository.findByFeatureKey(featureKey)
            .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                "Feature not found: " + featureKey));

        // Get access statistics
        List<UserFeatureAccess> allAccess = userFeatureAccessRepository.findByFeatureId(feature.getId());
        long totalAccessed = allAccess.stream().filter(UserFeatureAccess::isAccessed).count();
        long totalCompleted = allAccess.stream().filter(UserFeatureAccess::isCompleted).count();
        long totalNotAccessed = allAccess.stream().filter(a -> !a.isAccessed()).count();
        long uniqueTenants = allAccess.stream().map(UserFeatureAccess::getTenantId).distinct().count();

        double avgAccessCount = allAccess.stream()
            .mapToInt(a -> a.getAccessCount() != null ? a.getAccessCount() : 0)
            .average()
            .orElse(0.0);

        double completionRate = totalAccessed > 0 ? (double) totalCompleted / totalAccessed * 100 : 0.0;

        Instant firstAccessedAt = allAccess.stream()
            .map(UserFeatureAccess::getLastAccessedAt)
            .filter(Objects::nonNull)
            .min(Instant::compareTo)
            .orElse(null);

        Instant lastAccessedAt = allAccess.stream()
            .map(UserFeatureAccess::getLastAccessedAt)
            .filter(Objects::nonNull)
            .max(Instant::compareTo)
            .orElse(null);

        return FeatureAnalyticsDTO.builder()
            .featureId(feature.getId())
            .featureKey(feature.getFeatureKey())
            .featureName(feature.getName())
            .featureType(feature.getFeatureType().name())
            .category(feature.getCategory().name())
            .minPlan(feature.getMinPlan().name())
            .totalAccessed(totalAccessed)
            .totalCompleted(totalCompleted)
            .totalNotAccessed(totalNotAccessed)
            .totalUsers(totalAccessed + totalNotAccessed)
            .uniqueTenants(uniqueTenants)
            .avgAccessCount(avgAccessCount)
            .completionRate(completionRate)
            .firstAccessedAt(firstAccessedAt)
            .lastAccessedAt(lastAccessedAt)
            .enabled(feature.isEnabled())
            .rolloutPercentage(feature.getRolloutPercentage())
            .build();
    }

    // ------------------------------------------------------------------------
    // CRUD Operations for Features
    // ------------------------------------------------------------------------

    /**
     * Create a new feature definition.
     *
     * @param request Create feature request
     * @param actor   User creating the feature
     * @return Created feature DTO
     */
    @Transactional
    public FeatureDefinitionDTO createFeature(CreateFeatureRequest request, String actor) {
        log.info("Creating feature {} by {}", request.getFeatureKey(), actor);

        // Validate input
        validateCreateRequest(request);

        // Check for duplicates
        if (featureDefinitionRepository.existsByFeatureKey(request.getFeatureKey())) {
            throw new BusinessException(ErrorCode.RESOURCE_ALREADY_EXISTS,
                "Feature already exists: " + request.getFeatureKey());
        }

        FeatureDefinition feature = FeatureDefinition.builder()
            .featureKey(request.getFeatureKey())
            .featureType(request.getFeatureType())
            .category(request.getCategory())
            .name(request.getName())
            .description(request.getDescription())
            .enabled(request.getEnabled())
            .rolloutPercentage(request.getRolloutPercentage())
            .minPlan(request.getMinPlan())
            .roles(request.getRoles())
            .platforms(request.getPlatforms())
            .metadata(request.getMetadata())
            .createdBy(actor)
            .updatedBy(actor)
            .build();

        featureDefinitionRepository.save(feature);
        log.info("Created feature {} with ID {}", request.getFeatureKey(), feature.getId());
        return toDTO(feature);
    }

    /**
     * Update an existing feature definition.
     *
     * @param featureKey Feature key
     * @param request    Update request
     * @param actor      User updating the feature
     * @return Updated feature DTO
     */
    @Transactional
    public FeatureDefinitionDTO updateFeature(String featureKey, UpdateFeatureRequest request, String actor) {
        log.info("Updating feature {} by {}", featureKey, actor);

        FeatureDefinition feature = featureDefinitionRepository.findByFeatureKey(featureKey)
            .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                "Feature not found: " + featureKey));

        // Update fields if provided
        if (request.getName() != null) {
            feature.setName(request.getName());
        }
        if (request.getDescription() != null) {
            feature.setDescription(request.getDescription());
        }
        if (request.getEnabled() != null) {
            feature.setEnabled(request.getEnabled());
        }
        if (request.getRolloutPercentage() != null) {
            feature.setRolloutPercentage(request.getRolloutPercentage());
        }
        if (request.getMinPlan() != null) {
            feature.setMinPlan(request.getMinPlan());
        }
        if (request.getRoles() != null) {
            feature.setRoles(request.getRoles());
        }
        if (request.getPlatforms() != null) {
            feature.setPlatforms(request.getPlatforms());
        }
        if (request.getMetadata() != null) {
            if (request.getReplaceMetadata() != null && request.getReplaceMetadata()) {
                feature.setMetadata(request.getMetadata());
            } else {
                // Merge metadata
                if (feature.getMetadata() == null) {
                    feature.setMetadata(new HashMap<>());
                }
                feature.getMetadata().putAll(request.getMetadata());
            }
        }

        feature.setUpdatedBy(actor);
        featureDefinitionRepository.save(feature);

        log.info("Updated feature {} by {}", featureKey, actor);
        return toDTO(feature);
    }

    /**
     * Delete a feature definition.
     *
     * @param featureKey Feature key
     */
    @Transactional
    public void deleteFeature(String featureKey) {
        log.info("Deleting feature {}", featureKey);
        featureDefinitionRepository.deleteByFeatureKey(featureKey);
    }

    /**
     * Get a feature definition by key.
     *
     * @param featureKey Feature key
     * @return Feature DTO
     */
    @Transactional(readOnly = true)
    public FeatureDefinitionDTO getFeature(String featureKey) {
        FeatureDefinition feature = featureDefinitionRepository.findByFeatureKey(featureKey)
            .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                "Feature not found: " + featureKey));
        return toDTO(feature);
    }

    /**
     * List all features with pagination and optional filters.
     *
     * @param category Optional category filter
     * @param type     Optional type filter
     * @param pageable Pagination
     * @return Page of feature DTOs
     */
    @Transactional(readOnly = true)
    public Page<FeatureDefinitionDTO> listFeatures(FeatureCategory category, FeatureType type, Pageable pageable) {
        Page<FeatureDefinition> features;

        if (category != null && type != null) {
            features = featureDefinitionRepository.findAll(
                (root, query, cb) -> cb.and(
                    cb.equal(root.get("category"), category),
                    cb.equal(root.get("featureType"), type)
                ),
                pageable
            );
        } else if (category != null) {
            features = featureDefinitionRepository.findAll(
                (root, query, cb) -> cb.equal(root.get("category"), category),
                pageable
            );
        } else if (type != null) {
            features = featureDefinitionRepository.findAll(
                (root, query, cb) -> cb.equal(root.get("featureType"), type),
                pageable
            );
        } else {
            features = featureDefinitionRepository.findAll(pageable);
        }

        return features.map(this::toDTO);
    }

    /**
     * Search features by name or description.
     *
     * @param searchTerm Search term
     * @param pageable   Pagination
     * @return Page of feature DTOs
     */
    @Transactional(readOnly = true)
    public Page<FeatureDefinitionDTO> searchFeatures(String searchTerm, Pageable pageable) {
        if (!StringUtils.hasText(searchTerm)) {
            return featureDefinitionRepository.findAll(pageable).map(this::toDTO);
        }
        return featureDefinitionRepository.searchByNameOrDescription(searchTerm, pageable).map(this::toDTO);
    }

    /**
     * Bulk create/update features.
     *
     * @param requests List of create requests
     * @param actor    User performing the operation
     * @return Bulk operation response
     */
    @Transactional
    public BulkFeatureResponse bulkUpdateFeatures(List<CreateFeatureRequest> requests, String actor) {
        log.info("Bulk updating {} features by {}", requests.size(), actor);

        List<String> successfulKeys = new ArrayList<>();
        List<BulkFeatureResponse.FailedOperation> failures = new ArrayList<>();

        for (CreateFeatureRequest request : requests) {
            try {
                if (featureDefinitionRepository.existsByFeatureKey(request.getFeatureKey())) {
                    // Update existing
                    UpdateFeatureRequest updateRequest = UpdateFeatureRequest.builder()
                        .name(request.getName())
                        .description(request.getDescription())
                        .enabled(request.getEnabled())
                        .rolloutPercentage(request.getRolloutPercentage())
                        .minPlan(request.getMinPlan())
                        .roles(request.getRoles())
                        .platforms(request.getPlatforms())
                        .metadata(request.getMetadata())
                        .build();
                    updateFeature(request.getFeatureKey(), updateRequest, actor);
                } else {
                    // Create new
                    createFeature(request, actor);
                }
                successfulKeys.add(request.getFeatureKey());
            } catch (Exception e) {
                log.error("Failed to process feature {}: {}", request.getFeatureKey(), e.getMessage());
                failures.add(BulkFeatureResponse.FailedOperation.builder()
                    .featureKey(request.getFeatureKey())
                    .error(e.getMessage())
                    .errorCode(e instanceof BusinessException ? ((BusinessException) e).getErrorCode().name() : "UNKNOWN_ERROR")
                    .build());
            }
        }

        return BulkFeatureResponse.builder()
            .totalProcessed(requests.size())
            .successCount(successfulKeys.size())
            .failureCount(failures.size())
            .successfulKeys(successfulKeys)
            .failures(failures)
            .build();
    }

    // ------------------------------------------------------------------------
    // Helper Methods
    // ------------------------------------------------------------------------

    /**
     * Get user's subscription plan.
     */
    private SubscriptionPlan getUserPlan(String tenantId) {
        return subscriptionRepository.findByTenantId(tenantId)
            .map(Subscription::getPlan)
            .orElse(SubscriptionPlan.BASIC);
    }

    /**
     * Check if user's plan is sufficient for feature access.
     */
    private boolean isPlanSufficient(SubscriptionPlan userPlan, SubscriptionPlan requiredPlan) {
        int userLevel = getPlanLevel(userPlan);
        int requiredLevel = getPlanLevel(requiredPlan);
        return userLevel >= requiredLevel;
    }

    /**
     * Get numeric level for plan comparison.
     */
    private int getPlanLevel(SubscriptionPlan plan) {
        return switch (plan) {
            case BASIC -> 1;
            case PRO -> 2;
            case ENTERPRISE -> 3;
        };
    }

    /**
     * Check if user is within rollout percentage.
     */
    private boolean isInRollout(UUID userId, Integer rolloutPercentage) {
        if (rolloutPercentage == null || rolloutPercentage >= 100) {
            return true;
        }
        if (userId == null) {
            return false;
        }
        int hash = Math.abs(userId.hashCode() % 100);
        return hash < rolloutPercentage;
    }

    private String normalizePath(String pagePath) {
        if (!StringUtils.hasText(pagePath)) {
            return "/";
        }
        String normalized = pagePath.trim();
        return normalized.startsWith("/") ? normalized : "/" + normalized;
    }

    private Map<String, Object> safeMetadata(Map<String, Object> metadata) {
        return metadata != null ? metadata : new HashMap<>();
    }

    private boolean isPublished(Map<String, Object> metadata) {
        String state = String.valueOf(metadata.getOrDefault("state", "LIVE")).toUpperCase(Locale.ROOT);
        return !"DRAFT".equals(state) && !"PAUSED".equals(state);
    }

    private boolean isWithinSchedule(Map<String, Object> metadata, Instant now) {
        Instant startAt = parseInstant(metadata.get("startAt"));
        Instant endAt = parseInstant(metadata.get("endAt"));
        if (startAt != null && now.isBefore(startAt)) {
            return false;
        }
        if (endAt != null && now.isAfter(endAt)) {
            return false;
        }
        return true;
    }

    private boolean matchesPath(Map<String, Object> metadata, String pagePath) {
        Object rawPagePaths = metadata.get("pagePaths");
        if (rawPagePaths instanceof List<?> list && !list.isEmpty()) {
            boolean matched = list.stream()
                .map(Object::toString)
                .map(this::normalizePath)
                .anyMatch(path -> path.equals(pagePath) || pagePath.startsWith(path + "/"));
            if (!matched) {
                return false;
            }
        }
        Object pathPrefix = metadata.get("pathPrefix");
        if (pathPrefix != null) {
            String prefix = normalizePath(String.valueOf(pathPrefix));
            return pagePath.startsWith(prefix);
        }
        return true;
    }

    private boolean matchesTargeting(Map<String, Object> metadata, AdminUser user, String tenantId) {
        Object rawTargeting = metadata.get("targeting");
        if (!(rawTargeting instanceof Map<?, ?> targeting)) {
            return true;
        }

        if (!matchesStringList(targeting.get("tenantIds"), tenantId)) {
            return false;
        }
        if (!matchesStringList(targeting.get("userIds"), user.getId().toString())) {
            return false;
        }
        if (!matchesStringList(targeting.get("roles"), user.getRole().name())) {
            return false;
        }
        if (targeting.get("emailDomains") instanceof List<?> domains && !domains.isEmpty()) {
            String email = user.getEmail() != null ? user.getEmail().toLowerCase(Locale.ROOT) : "";
            boolean domainMatch = domains.stream()
                .map(Object::toString)
                .map(String::toLowerCase)
                .anyMatch(domain -> email.endsWith("@" + domain));
            if (!domainMatch) {
                return false;
            }
        }
        return true;
    }

    private boolean matchesStringList(Object rawList, String value) {
        if (!(rawList instanceof List<?> list) || list.isEmpty()) {
            return true;
        }
        return list.stream().map(Object::toString).anyMatch(v -> Objects.equals(v, value));
    }

    private boolean passesFrequencyControls(Map<String, Object> metadata, UserFeatureAccess access, Instant now) {
        if (access == null) {
            return true;
        }
        int maxImpressions = parseInt(metadata.get("maxImpressions"), Integer.MAX_VALUE);
        if (access.getAccessCount() != null && access.getAccessCount() >= maxImpressions) {
            return false;
        }
        int cooldownSeconds = parseInt(metadata.get("cooldownSeconds"), 0);
        if (cooldownSeconds > 0 && access.getLastAccessedAt() != null) {
            if (access.getLastAccessedAt().plusSeconds(cooldownSeconds).isAfter(now)) {
                return false;
            }
        }
        return true;
    }

    private String resolveSurface(FeatureType featureType, Map<String, Object> metadata) {
        Object explicit = metadata.get("surface");
        if (explicit != null) {
            return explicit.toString();
        }
        return switch (featureType) {
            case TOUR -> "modal";
            case BANNER, ANNOUNCEMENT -> "banner";
            case TOOLTIP -> "inline";
            case FEATURE_FLAG -> "flag";
        };
    }

    private List<RuntimeFeatureItemDTO> orchestrateSurfaces(List<RuntimeFeatureItemDTO> candidates, int cap) {
        List<RuntimeFeatureItemDTO> modal = candidates.stream()
            .filter(i -> "modal".equalsIgnoreCase(i.getSurface()))
            .toList();
        List<RuntimeFeatureItemDTO> banner = candidates.stream()
            .filter(i -> "banner".equalsIgnoreCase(i.getSurface()))
            .toList();
        List<RuntimeFeatureItemDTO> inline = candidates.stream()
            .filter(i -> "inline".equalsIgnoreCase(i.getSurface()))
            .toList();
        List<RuntimeFeatureItemDTO> flags = candidates.stream()
            .filter(i -> "flag".equalsIgnoreCase(i.getSurface()))
            .toList();

        List<RuntimeFeatureItemDTO> result = new ArrayList<>();
        if (!modal.isEmpty()) {
            result.add(modal.get(0));
        }
        if (!banner.isEmpty()) {
            result.add(banner.get(0));
        }
        result.addAll(inline);
        result.addAll(flags);
        if (result.size() > cap) {
            return result.subList(0, cap);
        }
        return result;
    }

    private Instant parseInstant(Object raw) {
        if (raw == null) {
            return null;
        }
        try {
            return Instant.parse(raw.toString());
        } catch (Exception ignored) {
            return null;
        }
    }

    private int parseInt(Object raw, int defaultValue) {
        if (raw instanceof Number n) {
            return n.intValue();
        }
        if (raw == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(raw.toString());
        } catch (NumberFormatException ex) {
            return defaultValue;
        }
    }

    private boolean parseBoolean(Object raw, boolean defaultValue) {
        if (raw instanceof Boolean b) {
            return b;
        }
        if (raw == null) {
            return defaultValue;
        }
        return Boolean.parseBoolean(raw.toString());
    }

    @SuppressWarnings("unchecked")
    private void incrementEventCounter(Map<String, Object> metadata, String eventType) {
        Map<String, Object> counters = metadata.get("eventCounts") instanceof Map<?, ?> existing
            ? new HashMap<>((Map<String, Object>) existing)
            : new HashMap<>();
        int current = parseInt(counters.get(eventType), 0);
        counters.put(eventType, current + 1);
        metadata.put("eventCounts", counters);
    }

    /**
     * Validate create feature request.
     */
    private void validateCreateRequest(CreateFeatureRequest request) {
        if (!request.hasValidRoles()) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR,
                "Invalid roles. Valid roles are: SUPER_ADMIN, ADMIN, EDITOR, VIEWER");
        }
        if (!request.hasValidPlatforms()) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR,
                "Invalid platforms. Valid platforms are: WEB, MOBILE, DESKTOP, API");
        }
    }

    /**
     * Convert entity to DTO.
     */
    private FeatureDefinitionDTO toDTO(FeatureDefinition feature) {
        FeatureDefinitionDTO dto = modelMapper.map(feature, FeatureDefinitionDTO.class);
        
        // Calculate completion rate if analytics data is available
        if (feature.getId() != null) {
            List<UserFeatureAccess> accessList = userFeatureAccessRepository.findByFeatureId(feature.getId());
            long totalAccessed = accessList.stream().filter(UserFeatureAccess::isAccessed).count();
            long totalCompleted = accessList.stream().filter(UserFeatureAccess::isCompleted).count();
            
            dto.setTotalAccessed(totalAccessed);
            dto.setTotalCompleted(totalCompleted);
            dto.setCompletionRate(totalAccessed > 0 ? (double) totalCompleted / totalAccessed * 100 : 0.0);
            dto.setUniqueTenants(accessList.stream().map(UserFeatureAccess::getTenantId).distinct().count());
        }
        
        return dto;
    }

    /**
     * Convert entity to status DTO.
     */
    private FeatureStatusDTO toStatusDTO(FeatureDefinition feature, UserFeatureAccess access) {
        return FeatureStatusDTO.builder()
            .featureId(feature.getId())
            .featureKey(feature.getFeatureKey())
            .featureName(feature.getName())
            .featureType(feature.getFeatureType().name())
            .available(feature.isEnabled())
            .enabled(feature.isEnabled())
            .accessed(access.isAccessed())
            .completed(access.isCompleted())
            .accessCount(access.getAccessCount())
            .lastAccessedAt(access.getLastAccessedAt())
            .completedAt(access.getCompletedAt())
            .shouldShow(!access.isCompleted())
            .metadata(feature.getMetadata())
            .userMetadata(access.getMetadata())
            .build();
    }

    /**
     * Convert entity to tenant config DTO.
     */
    private TenantFeatureConfigDTO toTenantConfigDTO(TenantFeatureConfig config) {
        return TenantFeatureConfigDTO.builder()
            .id(config.getId())
            .tenantId(config.getTenantId())
            .featureId(config.getFeature().getId())
            .featureKey(config.getFeature().getFeatureKey())
            .featureName(config.getFeature().getName())
            .enabled(config.isEnabled())
            .rolloutPercentage(config.getRolloutPercentage())
            .customMetadata(config.getCustomMetadata())
            .effectiveEnabled(config.getEffectiveEnabled())
            .effectiveRolloutPercentage(config.getEffectiveRolloutPercentage())
            .createdBy(config.getCreatedBy())
            .createdAt(config.getCreatedAt())
            .updatedBy(config.getUpdatedBy())
            .updatedAt(config.getUpdatedAt())
            .build();
    }
}
