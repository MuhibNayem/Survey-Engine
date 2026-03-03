package com.bracits.surveyengine.subscription.repository;

import com.bracits.surveyengine.subscription.entity.PlanDefinition;
import com.bracits.surveyengine.subscription.entity.SubscriptionPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PlanDefinitionRepository extends JpaRepository<PlanDefinition, UUID> {
    Optional<PlanDefinition> findByPlanCode(SubscriptionPlan planCode);

    Optional<PlanDefinition> findByPlanCodeAndActiveTrue(SubscriptionPlan planCode);

    List<PlanDefinition> findByActiveTrueOrderByPriceAsc();
}
