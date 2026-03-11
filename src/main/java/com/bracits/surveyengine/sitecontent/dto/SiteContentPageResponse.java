package com.bracits.surveyengine.sitecontent.dto;

import com.bracits.surveyengine.sitecontent.entity.SiteContentPageKey;
import com.fasterxml.jackson.annotation.JsonRawValue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SiteContentPageResponse {

    private SiteContentPageKey pageKey;

    @JsonRawValue
    private String draftContent;

    @JsonRawValue
    private String publishedContent;

    private boolean published;
    private Instant publishedAt;
    private Instant updatedAt;
}
