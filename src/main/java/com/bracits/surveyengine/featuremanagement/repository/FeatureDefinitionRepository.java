package com.bracits.surveyengine.featuremanagement.repository;

import com.bracits.surveyengine.featuremanagement.entity.FeatureCategory;
import com.bracits.surveyengine.featuremanagement.entity.FeatureDefinition;
import com.bracits.surveyengine.featuremanagement.entity.FeatureType;
import com.bracits.surveyengine.subscription.entity.SubscriptionPlan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for FeatureDefinition entities.
 * Provides query methods for feature lookup and filtering.
 */
@Repository
public interface FeatureDefinitionRepository extends JpaRepository<FeatureDefinition, UUID>, JpaSpecificationExecutor<FeatureDefinition> {

    /**
     * Find feature by unique key.
     */
    Optional<FeatureDefinition> findByFeatureKey(String featureKey);

    /**
     * Check if feature exists by key.
     */
    boolean existsByFeatureKey(String featureKey);

    /**
     * Find all enabled features.
     */
    List<FeatureDefinition> findByEnabledTrue();

    /**
     * Find all features by type.
     */
    List<FeatureDefinition> findByFeatureType(FeatureType type);

    /**
     * Find all features by category.
     */
    List<FeatureDefinition> findByCategory(FeatureCategory category);

    /**
     * Find all enabled features by category.
     */
    List<FeatureDefinition> findByCategoryAndEnabledTrue(FeatureCategory category);

    /**
     * Find all features by minimum plan.
     */
    List<FeatureDefinition> findByMinPlan(SubscriptionPlan minPlan);

    /**
     * Find all enabled features by minimum plan.
     */
    List<FeatureDefinition> findByMinPlanAndEnabledTrue(SubscriptionPlan minPlan);

    /**
     * Find features by category and minimum plan.
     */
    List<FeatureDefinition> findByCategoryAndMinPlan(FeatureCategory category, SubscriptionPlan minPlan);

    /**
     * Find all enabled features by category and minimum plan.
     */
    List<FeatureDefinition> findByCategoryAndMinPlanAndEnabledTrue(FeatureCategory category, SubscriptionPlan minPlan);

    /**
     * Find features by type and category.
     */
    List<FeatureDefinition> findByFeatureTypeAndCategory(FeatureType type, FeatureCategory category);

    /**
     * Find enabled features by type and category.
     */
    List<FeatureDefinition> findByFeatureTypeAndCategoryAndEnabledTrue(FeatureType type, FeatureCategory category);

    /**
     * Search features by name or description (case-insensitive).
     */
    @Query("SELECT f FROM FeatureDefinition f WHERE " +
           "LOWER(f.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(f.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(f.featureKey) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<FeatureDefinition> searchByNameOrDescription(@Param("searchTerm") String searchTerm);

    /**
     * Search features by name or description with pagination.
     */
    @Query("SELECT f FROM FeatureDefinition f WHERE " +
           "LOWER(f.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(f.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(f.featureKey) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<FeatureDefinition> searchByNameOrDescription(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * Find features accessible by a minimum plan level.
     * Returns features where min_plan is at or below the given plan.
     */
    @Query("SELECT f FROM FeatureDefinition f WHERE " +
           "CASE f.minPlan " +
           "  WHEN 'BASIC' THEN 1 " +
           "  WHEN 'PRO' THEN 2 " +
           "  WHEN 'ENTERPRISE' THEN 3 " +
           "END <= " +
           "CASE :planString " +
           "  WHEN 'BASIC' THEN 1 " +
           "  WHEN 'PRO' THEN 2 " +
           "  WHEN 'ENTERPRISE' THEN 3 " +
           "END")
    List<FeatureDefinition> findByMinPlanLevel(@Param("planString") String planString);

    /**
     * Find enabled features accessible by a minimum plan level.
     */
    @Query("SELECT f FROM FeatureDefinition f WHERE f.enabled = true AND " +
           "CASE f.minPlan " +
           "  WHEN 'BASIC' THEN 1 " +
           "  WHEN 'PRO' THEN 2 " +
           "  WHEN 'ENTERPRISE' THEN 3 " +
           "END <= " +
           "CASE :planString " +
           "  WHEN 'BASIC' THEN 1 " +
           "  WHEN 'PRO' THEN 2 " +
           "  WHEN 'ENTERPRISE' THEN 3 " +
           "END")
    List<FeatureDefinition> findByMinPlanLevelAndEnabledTrue(@Param("planString") String planString);

    /**
     * Find features by category accessible by a minimum plan level.
     */
    @Query("SELECT f FROM FeatureDefinition f WHERE f.category = :category AND " +
           "CASE f.minPlan " +
           "  WHEN 'BASIC' THEN 1 " +
           "  WHEN 'PRO' THEN 2 " +
           "  WHEN 'ENTERPRISE' THEN 3 " +
           "END <= " +
           "CASE :planString " +
           "  WHEN 'BASIC' THEN 1 " +
           "  WHEN 'PRO' THEN 2 " +
           "  WHEN 'ENTERPRISE' THEN 3 " +
           "END")
    List<FeatureDefinition> findByCategoryAndMinPlanLevel(
        @Param("category") FeatureCategory category,
        @Param("planString") String planString);

    /**
     * Find enabled features by category accessible by a minimum plan level.
     */
    @Query("SELECT f FROM FeatureDefinition f WHERE f.enabled = true AND f.category = :category AND " +
           "CASE f.minPlan " +
           "  WHEN 'BASIC' THEN 1 " +
           "  WHEN 'PRO' THEN 2 " +
           "  WHEN 'ENTERPRISE' THEN 3 " +
           "END <= " +
           "CASE :planString " +
           "  WHEN 'BASIC' THEN 1 " +
           "  WHEN 'PRO' THEN 2 " +
           "  WHEN 'ENTERPRISE' THEN 3 " +
           "END")
    List<FeatureDefinition> findByCategoryAndMinPlanLevelAndEnabledTrue(
        @Param("category") FeatureCategory category,
        @Param("planString") String planString);

    /**
     * Find features that contain a specific role in their allowed roles list.
     */
    @Query(value = "SELECT * FROM feature_definition WHERE roles @> CAST(CONCAT('\"', :role, '\"') AS jsonb)", nativeQuery = true)
    List<FeatureDefinition> findByRole(@Param("role") String role);

    /**
     * Find enabled features that contain a specific role.
     */
    @Query(value = "SELECT * FROM feature_definition WHERE enabled = true AND roles @> CAST(CONCAT('\"', :role, '\"') AS jsonb)", nativeQuery = true)
    List<FeatureDefinition> findByRoleAndEnabledTrue(@Param("role") String role);

    /**
     * Count features by category.
     */
    long countByCategory(FeatureCategory category);

    /**
     * Count enabled features by category.
     */
    long countByCategoryAndEnabledTrue(FeatureCategory category);

    /**
     * Count features by type.
     */
    long countByFeatureType(FeatureType type);

    /**
     * Delete feature by key.
     */
    void deleteByFeatureKey(String featureKey);
}
