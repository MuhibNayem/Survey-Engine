package com.bracits.surveyengine.search.service;

public interface SearchCacheInvalidationService {

    void invalidateTenant(String tenantId);

    void invalidateCurrentTenant();

    void invalidateTenantAfterCommit(String tenantId);
}

