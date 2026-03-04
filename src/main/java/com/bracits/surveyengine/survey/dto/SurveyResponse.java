package com.bracits.surveyengine.survey.dto;

import com.bracits.surveyengine.survey.entity.SurveyLifecycleState;
import com.bracits.surveyengine.questionbank.entity.QuestionType;
import lombok.*;

import java.math.BigDecimal;
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
        private BigDecimal categoryWeightPercentage;
        private Integer sortOrder;
        private boolean mandatory;
        private String answerConfig;

        /**
         * Survey-pinned (versioned copy) metadata shown in Survey Details.
         * These values are draft-editable and immutable after publish.
         */
        private String pinnedQuestionText;
        private QuestionType pinnedQuestionType;
        private BigDecimal pinnedQuestionMaxScore;
        private String pinnedQuestionOptionConfig;
        private String pinnedCategoryName;
        private String pinnedCategoryDescription;
    }
}
