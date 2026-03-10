package com.bracits.surveyengine.search.dto;

import java.util.List;

public record GlobalSearchResponse(
        String query,
        int total,
        List<GlobalSearchItemResponse> items,
        String nextCursor) {
}
