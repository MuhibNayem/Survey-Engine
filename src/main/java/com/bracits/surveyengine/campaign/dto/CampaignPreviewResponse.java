package com.bracits.surveyengine.campaign.dto;

import com.bracits.surveyengine.campaign.entity.AuthMode;
import com.bracits.surveyengine.campaign.entity.CampaignStatus;
import com.bracits.surveyengine.questionbank.entity.QuestionType;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CampaignPreviewResponse {
    private UUID campaignId;
    private String tenantId;
    private String campaignName;
    private CampaignStatus campaignStatus;
    private AuthMode authMode;

    private UUID surveyId;
    private String surveyTitle;
    private String surveyDescription;

    private boolean showQuestionNumbers;
    private boolean showProgressIndicator;
    private boolean allowBackButton;
    private String startMessage;
    private String finishMessage;
    private String headerHtml;
    private String footerHtml;

    private boolean collectName;
    private boolean collectEmail;
    private boolean collectPhone;
    private boolean collectAddress;

    private List<PagePreview> pages;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PagePreview {
        private UUID id;
        private String title;
        private Integer sortOrder;
        private List<QuestionPreview> questions;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestionPreview {
        private UUID id;
        private UUID questionId;
        private UUID questionVersionId;
        private UUID categoryVersionId;
        private String text;
        private QuestionType type;
        private BigDecimal maxScore;
        private boolean mandatory;
        private Integer sortOrder;
        private String optionConfig;
        private String answerConfig;
    }
}
