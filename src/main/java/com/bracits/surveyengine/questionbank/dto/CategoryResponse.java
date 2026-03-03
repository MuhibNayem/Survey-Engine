package com.bracits.surveyengine.questionbank.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponse {
    private UUID id;
    private String name;
    private String description;
    private Integer currentVersion;
    private boolean active;
    private List<QuestionMappingResponse> questionMappings;
    private String createdBy;
    private Instant createdAt;
    private String updatedBy;
    private Instant updatedAt;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestionMappingResponse {
        private UUID questionId;
        private UUID questionVersionId;
        private String questionText;
        private Integer sortOrder;
        private BigDecimal weight;
    }
}
