package com.bracits.surveyengine.analytics.dto;

import com.bracits.surveyengine.questionbank.entity.QuestionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionAnalyticsResponse {
    private UUID campaignId;
    private UUID questionId;
    private QuestionType questionType;
    private long totalAnswers;
    
    // For numeric/scored questions
    private BigDecimal averageScore;
    private BigDecimal maxScore;
    
    // Frequency distribution
    private List<OptionFrequency> optionFrequencies;
}
