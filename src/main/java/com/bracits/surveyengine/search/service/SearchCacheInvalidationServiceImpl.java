package com.bracits.surveyengine.search.service;

import com.bracits.surveyengine.admin.context.TenantContext;
import com.bracits.surveyengine.search.config.SearchCacheNames;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchCacheInvalidationServiceImpl implements SearchCacheInvalidationService {

    private final CacheManager cacheManager;

    @Override
    public void invalidateTenant(String tenantId) {
        if (tenantId == null || tenantId.isBlank()) {
            return;
        }
        Cache cache = cacheManager.getCache(SearchCacheNames.GLOBAL_SEARCH);
        if (cache != null) {
            cache.clear();
        }
        log.debug("Evicted global search cache entries after write for tenant {}", tenantId);
    }

    @Override
    public void invalidateCurrentTenant() {
        String tenantId = TenantContext.getTenantId();
        invalidateTenant(tenantId);
    }

    @Override
    public void invalidateTenantAfterCommit(String tenantId) {
        if (tenantId == null || tenantId.isBlank()) {
            return;
        }
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    invalidateTenant(tenantId);
                }
            });
            return;
        }
        invalidateTenant(tenantId);
    }
}
