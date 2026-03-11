package com.bracits.surveyengine.sitecontent.service;

import com.bracits.surveyengine.common.exception.BusinessException;
import com.bracits.surveyengine.common.exception.ErrorCode;
import com.bracits.surveyengine.sitecontent.dto.PublicSiteContentResponse;
import com.bracits.surveyengine.sitecontent.dto.SiteContentDocumentRequest;
import com.bracits.surveyengine.sitecontent.dto.SiteContentPageResponse;
import com.bracits.surveyengine.sitecontent.entity.SiteContentPage;
import com.bracits.surveyengine.sitecontent.entity.SiteContentPageKey;
import com.bracits.surveyengine.sitecontent.repository.SiteContentPageRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class SiteContentService {

    private final SiteContentPageRepository siteContentPageRepository;
    private final ObjectMapper objectMapper;

    @Transactional(readOnly = true)
    public SiteContentPageResponse getEditorPage(SiteContentPageKey pageKey) {
        return toEditorResponse(loadPage(pageKey));
    }

    @Transactional
    @com.bracits.surveyengine.common.audit.annotation.Auditable(action = "SITE_CONTENT_DRAFT_SAVED")
    public SiteContentPageResponse saveDraft(SiteContentPageKey pageKey, SiteContentDocumentRequest request) {
        SiteContentPage page = loadPage(pageKey);
        page.setDraftContent(writeJson(request.getContent()));
        return toEditorResponse(siteContentPageRepository.save(page));
    }

    @Transactional
    @com.bracits.surveyengine.common.audit.annotation.Auditable(action = "SITE_CONTENT_PUBLISHED")
    public SiteContentPageResponse publish(SiteContentPageKey pageKey) {
        SiteContentPage page = loadPage(pageKey);
        page.setPublishedContent(page.getDraftContent());
        page.setPublished(true);
        page.setPublishedAt(Instant.now());
        return toEditorResponse(siteContentPageRepository.save(page));
    }

    @Transactional
    @com.bracits.surveyengine.common.audit.annotation.Auditable(action = "SITE_CONTENT_UNPUBLISHED")
    public SiteContentPageResponse unpublish(SiteContentPageKey pageKey) {
        SiteContentPage page = loadPage(pageKey);
        page.setPublished(false);
        return toEditorResponse(siteContentPageRepository.save(page));
    }

    @Transactional(readOnly = true)
    public PublicSiteContentResponse getPublicPage(SiteContentPageKey pageKey) {
        SiteContentPage page = loadPage(pageKey);
        String rawContent = page.isPublished() && page.getPublishedContent() != null
                ? page.getPublishedContent()
                : "{}";
        return PublicSiteContentResponse.builder()
                .pageKey(pageKey)
                .content(rawContent)
                .published(page.isPublished())
                .build();
    }

    private SiteContentPage loadPage(SiteContentPageKey pageKey) {
        return siteContentPageRepository.findByPageKeyAndActiveTrue(pageKey)
                .orElseGet(() -> siteContentPageRepository.save(SiteContentPage.builder()
                        .pageKey(pageKey)
                        .draftContent("{}")
                        .published(false)
                        .active(true)
                        .build()));
    }

    private SiteContentPageResponse toEditorResponse(SiteContentPage page) {
        return SiteContentPageResponse.builder()
                .pageKey(page.getPageKey())
                .draftContent(page.getDraftContent() != null ? page.getDraftContent() : "{}")
                .publishedContent(page.getPublishedContent() != null ? page.getPublishedContent() : "{}")
                .published(page.isPublished())
                .publishedAt(page.getPublishedAt())
                .updatedAt(page.getUpdatedAt())
                .build();
    }

    private String writeJson(Object content) {
        try {
            return objectMapper.writeValueAsString(content != null ? content : java.util.Collections.emptyMap());
        } catch (IOException ex) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "Unable to serialize site content");
        }
    }
}
