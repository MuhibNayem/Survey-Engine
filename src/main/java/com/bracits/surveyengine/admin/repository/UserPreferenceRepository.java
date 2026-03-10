package com.bracits.surveyengine.admin.repository;

import com.bracits.surveyengine.admin.entity.UserPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for user preferences.
 */
@Repository
public interface UserPreferenceRepository extends JpaRepository<UserPreference, UUID> {

    /**
     * Find preferences for a specific user.
     */
    Optional<UserPreference> findByUserId(UUID userId);

    /**
     * Find or create preferences for a user.
     */
    @Query("SELECT p FROM UserPreference p WHERE p.userId = :userId")
    Optional<UserPreference> findByUserIdOrCreate(@Param("userId") UUID userId);

    /**
     * Delete preferences for a user (e.g., on account deletion).
     */
    @Modifying
    @Query("DELETE FROM UserPreference p WHERE p.userId = :userId")
    void deleteByUserId(@Param("userId") UUID userId);
}
