package com.bracits.surveyengine.response.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseSubmissionRequest {

    @NotNull(message = "Campaign ID is required")
    private UUID campaignId;

    private String respondentIdentifier;
    private String respondentIp;
    private String respondentDeviceFingerprint;
    private String responderToken;
    private String responderAccessCode;
    private Map<String, String> respondentMetadata;

    private List<AnswerRequest> answers;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnswerRequest {
        @NotNull(message = "Question ID is required")
        private UUID questionId;
        private UUID questionVersionId;
        private String value;
        private BigDecimal score;
    }
}
