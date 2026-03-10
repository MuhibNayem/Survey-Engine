package com.bracits.surveyengine.search.service;

public final class SearchCacheKeySupport {

    private SearchCacheKeySupport() {
    }

    public static String key(String tenantId, String query, int limit, String cursor) {
        String normalizedQuery = normalizeQuery(query);
        String normalizedCursor = cursor == null ? "" : cursor.trim();
        return (tenantId == null ? "no-tenant" : tenantId) + "|" + normalizedQuery + "|" + limit + "|" + normalizedCursor;
    }

    private static String normalizeQuery(String query) {
        if (query == null) {
            return "";
        }
        String noControlChars = query.replaceAll("\\p{Cntrl}", " ");
        return noControlChars.trim().replaceAll("\\s{2,}", " ").toLowerCase();
    }
}
