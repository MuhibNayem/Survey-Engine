package com.bracits.surveyengine.search.dto;

public record GlobalSearchItemResponse(
        String id,
        String type,
        String title,
        String snippet,
        String route,
        double score) {
}
