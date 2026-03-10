package com.bracits.surveyengine.featuremanagement.repository;

import com.bracits.surveyengine.featuremanagement.entity.TenantFeatureConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for TenantFeatureConfig entities.
 * Provides query methods for tenant-specific feature configurations.
 */
@Repository
public interface TenantFeatureConfigRepository extends JpaRepository<TenantFeatureConfig, UUID> {

    /**
     * Find tenant configuration by tenant ID and feature ID.
     */
    Optional<TenantFeatureConfig> findByTenantIdAndFeatureId(String tenantId, UUID featureId);

    /**
     * Find tenant configuration by tenant ID and feature key (via join).
     */
    @Query("SELECT t FROM TenantFeatureConfig t JOIN t.feature f WHERE t.tenantId = :tenantId AND f.featureKey = :featureKey")
    Optional<TenantFeatureConfig> findByTenantIdAndFeatureKey(
        @Param("tenantId") String tenantId, 
        @Param("featureKey") String featureKey);

    /**
     * Find all configurations for a tenant.
     */
    List<TenantFeatureConfig> findByTenantId(String tenantId);

    /**
     * Find all enabled configurations for a tenant.
     */
    List<TenantFeatureConfig> findByTenantIdAndEnabledTrue(String tenantId);

    /**
     * Find all configurations for a feature across all tenants.
     */
    List<TenantFeatureConfig> findByFeatureId(UUID featureId);

    /**
     * Count configurations for a tenant.
     */
    long countByTenantId(String tenantId);

    /**
     * Count enabled configurations for a tenant.
     */
    long countByTenantIdAndEnabledTrue(String tenantId);

    /**
     * Check if tenant has configuration for a feature.
     */
    boolean existsByTenantIdAndFeatureId(String tenantId, UUID featureId);

    /**
     * Check if tenant has configuration for a feature by feature key.
     */
    @Query("SELECT COUNT(t) > 0 FROM TenantFeatureConfig t JOIN t.feature f WHERE t.tenantId = :tenantId AND f.featureKey = :featureKey")
    boolean existsByTenantIdAndFeatureKey(
        @Param("tenantId") String tenantId, 
        @Param("featureKey") String featureKey);

    /**
     * Delete all configurations for a tenant.
     */
    void deleteAllByTenantId(String tenantId);

    /**
     * Delete configuration by tenant ID and feature ID.
     */
    void deleteByTenantIdAndFeatureId(String tenantId, UUID featureId);

    /**
     * Find tenant configurations where custom metadata contains a specific key.
     */
    @Query(value = "SELECT * FROM tenant_feature_config WHERE tenant_id = :tenantId AND jsonb_exists(custom_metadata, :key)", nativeQuery = true)
    List<TenantFeatureConfig> findByTenantIdAndCustomMetadataKey(
        @Param("tenantId") String tenantId, 
        @Param("key") String key);
}
