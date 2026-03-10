package com.bracits.surveyengine.search.service;

import com.bracits.surveyengine.search.dto.GlobalSearchResponse;

public interface GlobalSearchService {
    default GlobalSearchResponse search(String query, int limit) {
        return search(query, limit, null);
    }

    GlobalSearchResponse search(String query, int limit, String cursor);
}
