package com.bracits.surveyengine.campaign.dto;

import com.bracits.surveyengine.common.jackson.FlexibleInstantDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CampaignSettingsRequest {
    private String password;
    private boolean captchaEnabled;
    private boolean oneResponsePerDevice;
    private boolean ipRestrictionEnabled;
    private boolean emailRestrictionEnabled;
    private Integer responseQuota;
    @JsonDeserialize(using = FlexibleInstantDeserializer.class)
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
    private List<DataCollectionFieldRequest> dataCollectionFields;
}
