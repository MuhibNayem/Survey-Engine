package com.bracits.surveyengine.tenant.service;

import com.bracits.surveyengine.tenant.entity.Tenant;
import com.bracits.surveyengine.tenant.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TenantService {

    private final TenantRepository tenantRepository;

    @Transactional
    public Tenant ensureProvisioned(String tenantId) {
        return tenantRepository.findById(tenantId)
                .orElseGet(() -> tenantRepository.save(Tenant.builder()
                        .id(tenantId)
                        .name(tenantId)
                        .build()));
    }

    @Transactional(readOnly = true)
    public boolean exists(String tenantId) {
        return tenantRepository.existsById(tenantId);
    }

    @Transactional(readOnly = true)
    public List<Tenant> findAll() {
        return tenantRepository.findAllByOrderByCreatedAtDesc();
    }

    @Transactional(readOnly = true)
    public List<Tenant> searchByName(String searchTerm) {
        return tenantRepository.findByNameContainingIgnoreCaseOrderByCreatedAtDesc(searchTerm);
    }
}
