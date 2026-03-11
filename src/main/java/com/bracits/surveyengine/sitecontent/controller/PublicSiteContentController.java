package com.bracits.surveyengine.sitecontent.controller;

import com.bracits.surveyengine.sitecontent.dto.PublicSiteContentResponse;
import com.bracits.surveyengine.sitecontent.entity.SiteContentPageKey;
import com.bracits.surveyengine.sitecontent.service.SiteContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/public/site-content")
@RequiredArgsConstructor
public class PublicSiteContentController {

    private final SiteContentService siteContentService;

    @GetMapping("/{pageKey}")
    public ResponseEntity<PublicSiteContentResponse> getPage(@PathVariable SiteContentPageKey pageKey) {
        return ResponseEntity.ok(siteContentService.getPublicPage(pageKey));
    }
}
