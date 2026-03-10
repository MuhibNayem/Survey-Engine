package com.bracits.surveyengine.search.service;

import com.bracits.surveyengine.search.dto.GlobalSearchResponse;

public interface GlobalSearchService {
    GlobalSearchResponse search(String query, int limit);
}
