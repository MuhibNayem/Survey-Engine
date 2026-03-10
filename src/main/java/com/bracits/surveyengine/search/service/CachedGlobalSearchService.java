package com.bracits.surveyengine.search.service;

import com.bracits.surveyengine.search.config.SearchCacheNames;
import com.bracits.surveyengine.search.dto.GlobalSearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
@RequiredArgsConstructor
public class CachedGlobalSearchService implements GlobalSearchService {

    private final GlobalSearchServiceImpl delegate;

    @Override
    @Cacheable(
            cacheNames = SearchCacheNames.GLOBAL_SEARCH,
            key = "T(com.bracits.surveyengine.search.service.SearchCacheKeySupport).key(T(com.bracits.surveyengine.admin.context.TenantContext).getTenantId(), #query, #limit, #cursor)",
            unless = "#result == null || #result.total() == 0")
    public GlobalSearchResponse search(String query, int limit, String cursor) {
        return delegate.search(query, limit, cursor);
    }
}
