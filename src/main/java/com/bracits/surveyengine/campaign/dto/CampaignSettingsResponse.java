package com.bracits.surveyengine.campaign.dto;

import lombok.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CampaignSettingsResponse {
    private UUID campaignId;
    private String password;
    private boolean captchaEnabled;
    private boolean oneResponsePerDevice;
    private boolean ipRestrictionEnabled;
    private boolean emailRestrictionEnabled;
    private Integer responseQuota;
    private Instant closeDate;
    private Integer sessionTimeoutMinutes;
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
    private List<DataCollectionFieldResponse> dataCollectionFields;
}
