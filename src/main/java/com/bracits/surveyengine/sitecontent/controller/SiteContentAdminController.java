package com.bracits.surveyengine.sitecontent.controller;

import com.bracits.surveyengine.sitecontent.dto.SiteContentDocumentRequest;
import com.bracits.surveyengine.sitecontent.dto.SiteContentPageResponse;
import com.bracits.surveyengine.sitecontent.entity.SiteContentPageKey;
import com.bracits.surveyengine.sitecontent.service.SiteContentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/superadmin/site-content")
@PreAuthorize("hasRole('SUPER_ADMIN')")
@RequiredArgsConstructor
public class SiteContentAdminController {

    private final SiteContentService siteContentService;

    @GetMapping("/{pageKey}")
    public ResponseEntity<SiteContentPageResponse> getPage(@PathVariable SiteContentPageKey pageKey) {
        return ResponseEntity.ok(siteContentService.getEditorPage(pageKey));
    }

    @PutMapping("/{pageKey}")
    public ResponseEntity<SiteContentPageResponse> saveDraft(
            @PathVariable SiteContentPageKey pageKey,
            @Valid @RequestBody SiteContentDocumentRequest request) {
        return ResponseEntity.ok(siteContentService.saveDraft(pageKey, request));
    }

    @PostMapping("/{pageKey}/publish")
    public ResponseEntity<SiteContentPageResponse> publish(@PathVariable SiteContentPageKey pageKey) {
        return ResponseEntity.ok(siteContentService.publish(pageKey));
    }

    @PostMapping("/{pageKey}/unpublish")
    public ResponseEntity<SiteContentPageResponse> unpublish(@PathVariable SiteContentPageKey pageKey) {
        return ResponseEntity.ok(siteContentService.unpublish(pageKey));
    }
}
