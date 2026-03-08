package com.bracits.surveyengine.tenant.service;

import com.bracits.surveyengine.tenant.entity.Tenant;
import com.bracits.surveyengine.tenant.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
