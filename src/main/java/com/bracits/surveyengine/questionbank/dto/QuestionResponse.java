package com.bracits.surveyengine.questionbank.dto;

import com.bracits.surveyengine.questionbank.entity.QuestionType;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionResponse {
    private UUID id;
    private String text;
    private QuestionType type;
    private BigDecimal maxScore;
    private String optionConfig;
    private Integer currentVersion;
    private boolean active;
    private String createdBy;
    private Instant createdAt;
    private String updatedBy;
    private Instant updatedAt;
}
