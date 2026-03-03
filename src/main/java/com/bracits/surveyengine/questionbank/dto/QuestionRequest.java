package com.bracits.surveyengine.questionbank.dto;

import com.bracits.surveyengine.questionbank.entity.QuestionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionRequest {

    @NotBlank(message = "Question text is required")
    private String text;

    @NotNull(message = "Question type is required")
    private QuestionType type;

    @NotNull(message = "Max score is required")
    @DecimalMin(value = "0.01", message = "Max score must be greater than 0")
    private BigDecimal maxScore;
}
