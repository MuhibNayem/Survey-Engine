package com.bracits.surveyengine.subscription.repository;

import com.bracits.surveyengine.subscription.entity.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, UUID> {
    List<PaymentTransaction> findByTenantIdOrderByProcessedAtDesc(String tenantId);

    Optional<PaymentTransaction> findByGatewayReference(String gatewayReference);
}
