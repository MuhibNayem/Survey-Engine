package com.bracits.surveyengine.tenant.repository;

import com.bracits.surveyengine.tenant.entity.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, String> {
    
    List<Tenant> findAllByOrderByCreatedAtDesc();
    
    List<Tenant> findByNameContainingIgnoreCaseOrderByCreatedAtDesc(String searchTerm);
}
