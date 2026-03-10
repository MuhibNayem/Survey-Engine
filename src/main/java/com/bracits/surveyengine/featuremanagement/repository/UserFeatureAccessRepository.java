package com.bracits.surveyengine.featuremanagement.repository;

import com.bracits.surveyengine.featuremanagement.entity.UserFeatureAccess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for UserFeatureAccess entities.
 * Provides query methods for user feature access tracking.
 */
@Repository
public interface UserFeatureAccessRepository extends JpaRepository<UserFeatureAccess, UUID> {

    /**
     * Find user's access record for a feature.
     */
    Optional<UserFeatureAccess> findByUserIdAndFeatureId(UUID userId, UUID featureId);

    /**
     * Find user's access record for a feature by feature key (via join).
     */
    @Query("SELECT u FROM UserFeatureAccess u JOIN u.feature f WHERE u.userId = :userId AND f.featureKey = :featureKey")
    Optional<UserFeatureAccess> findByUserIdAndFeatureKey(
        @Param("userId") UUID userId, 
        @Param("featureKey") String featureKey);

    /**
     * Find all access records for a user.
     */
    List<UserFeatureAccess> findByUserId(UUID userId);

    /**
     * Find all accessed features for a user.
     */
    List<UserFeatureAccess> findByUserIdAndAccessedTrue(UUID userId);

    /**
     * Find all completed features for a user.
     */
    List<UserFeatureAccess> findByUserIdAndCompletedTrue(UUID userId);

    /**
     * Find all incomplete features for a user (accessed but not completed).
     */
    List<UserFeatureAccess> findByUserIdAndAccessedTrueAndCompletedFalse(UUID userId);

    /**
     * Find all access records for a user in a tenant.
     */
    List<UserFeatureAccess> findByUserIdAndTenantId(UUID userId, String tenantId);

    /**
     * Find all access records for a feature across all users.
     */
    List<UserFeatureAccess> findByFeatureId(UUID featureId);

    /**
     * Count access records for a user.
     */
    long countByUserId(UUID userId);

    /**
     * Count completed features for a user.
     */
    long countByUserIdAndCompletedTrue(UUID userId);

    /**
     * Count accessed features for a user.
     */
    long countByUserIdAndAccessedTrue(UUID userId);

    /**
     * Check if user has accessed a feature.
     */
    @Query("SELECT COUNT(u) > 0 FROM UserFeatureAccess u JOIN u.feature f WHERE u.userId = :userId AND f.featureKey = :featureKey AND u.accessed = true")
    boolean existsByUserIdAndFeatureKeyAccessed(
        @Param("userId") UUID userId, 
        @Param("featureKey") String featureKey);

    /**
     * Check if user has completed a feature.
     */
    @Query("SELECT COUNT(u) > 0 FROM UserFeatureAccess u JOIN u.feature f WHERE u.userId = :userId AND f.featureKey = :featureKey AND u.completed = true")
    boolean existsByUserIdAndFeatureKeyCompleted(
        @Param("userId") UUID userId, 
        @Param("featureKey") String featureKey);

    /**
     * Check if user has an access record for a feature.
     */
    boolean existsByUserIdAndFeatureId(UUID userId, UUID featureId);

    /**
     * Delete all access records for a user.
     */
    void deleteAllByUserId(UUID userId);

    /**
     * Delete access record by user ID and feature ID.
     */
    void deleteByUserIdAndFeatureId(UUID userId, UUID featureId);

    /**
     * Update last accessed timestamp.
     */
    @Modifying
    @Transactional
    @Query("UPDATE UserFeatureAccess u SET u.lastAccessedAt = :timestamp WHERE u.id = :id")
    void updateLastAccessedAt(@Param("id") UUID id, @Param("timestamp") Instant timestamp);

    /**
     * Increment access count.
     */
    @Modifying
    @Transactional
    @Query("UPDATE UserFeatureAccess u SET u.accessCount = u.accessCount + 1 WHERE u.id = :id")
    void incrementAccessCount(@Param("id") UUID id);

    /**
     * Find users who have not completed a specific feature.
     */
    @Query("SELECT u FROM UserFeatureAccess u WHERE u.feature.id = :featureId AND u.completed = false")
    List<UserFeatureAccess> findIncompleteByFeatureId(@Param("featureId") UUID featureId);

    /**
     * Find access records by tenant and completion status.
     */
    List<UserFeatureAccess> findByTenantIdAndCompleted(String tenantId, boolean completed);

    /**
     * Find access records by tenant and feature.
     */
    List<UserFeatureAccess> findByTenantIdAndFeatureId(String tenantId, UUID featureId);

    /**
     * Count by tenant and feature.
     */
    long countByTenantIdAndFeatureId(String tenantId, UUID featureId);

    /**
     * Count completed by tenant and feature.
     */
    long countByTenantIdAndFeatureIdAndCompletedTrue(String tenantId, UUID featureId);
}
