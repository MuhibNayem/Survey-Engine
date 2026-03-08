package com.bracits.surveyengine.subscription.repository;

import com.bracits.surveyengine.subscription.entity.Subscription;
import com.bracits.surveyengine.subscription.entity.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {
    Optional<Subscription> findByTenantId(String tenantId);

    boolean existsByTenantIdAndStatusInAndCurrentPeriodEndAfter(
            String tenantId,
            Iterable<SubscriptionStatus> statuses,
            Instant now);

    long countByStatus(SubscriptionStatus status);
}
