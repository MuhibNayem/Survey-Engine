package com.bracits.surveyengine.response.dto;

import com.bracits.surveyengine.response.entity.ResponseStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SurveyResponseResponse {
    private UUID id;
    private UUID campaignId;
    private UUID surveySnapshotId;
    private String respondentIdentifier;
    private ResponseStatus status;
    private Instant startedAt;
    private Instant submittedAt;
    private Instant lockedAt;
    private List<AnswerResponse> answers;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnswerResponse {
        private UUID id;
        private UUID questionId;
        private UUID questionVersionId;
        private String value;
        private BigDecimal score;
    }
}
