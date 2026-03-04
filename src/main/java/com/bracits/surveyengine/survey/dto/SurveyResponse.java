package com.bracits.surveyengine.survey.dto;

import com.bracits.surveyengine.survey.entity.SurveyLifecycleState;
import lombok.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SurveyResponse {
    private UUID id;
    private String title;
    private String description;
    private SurveyLifecycleState lifecycleState;
    private Integer currentVersion;
    private boolean active;
    private List<PageResponse> pages;
    private String createdBy;
    private Instant createdAt;
    private String updatedBy;
    private Instant updatedAt;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PageResponse {
        private UUID id;
        private String title;
        private Integer sortOrder;
        private List<QuestionResponse> questions;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestionResponse {
        private UUID id;
        private UUID questionId;
        private UUID questionVersionId;
        private UUID categoryId;
        private UUID categoryVersionId;
        private Integer sortOrder;
        private boolean mandatory;
        private String answerConfig;
    }
}
