package com.bracits.surveyengine.search.controller;

import com.bracits.surveyengine.search.dto.GlobalSearchResponse;
import com.bracits.surveyengine.search.service.GlobalSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
public class GlobalSearchController {

    private final GlobalSearchService globalSearchService;

    @GetMapping("/global")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'EDITOR', 'VIEWER')")
    public ResponseEntity<GlobalSearchResponse> globalSearch(
            @RequestParam("q") String query,
            @RequestParam(value = "limit", defaultValue = "0") int limit,
            @RequestParam(value = "cursor", required = false) String cursor) {
        return ResponseEntity.ok(globalSearchService.search(query, limit, cursor));
    }
}
