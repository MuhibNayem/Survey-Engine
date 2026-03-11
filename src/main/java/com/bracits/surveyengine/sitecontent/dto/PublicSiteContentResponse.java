package com.bracits.surveyengine.sitecontent.dto;

import com.bracits.surveyengine.sitecontent.entity.SiteContentPageKey;
import com.fasterxml.jackson.annotation.JsonRawValue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PublicSiteContentResponse {

    private SiteContentPageKey pageKey;

    /** Raw JSON string written directly into the response without re-serialization. */
    @JsonRawValue
    private String content;

    private boolean published;
}
