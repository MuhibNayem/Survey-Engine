package com.bracits.surveyengine.survey.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * A skip logic rule that conditionally navigates the respondent to
 * a different page or question based on an answer value.
 * <p>
 * SRS §4.1: "System shall support configurable skip logic rules."
 */
@Entity
@Table(name = "skip_logic_rule")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SkipLogicRule {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "survey_id", nullable = false)
    private UUID surveyId;

    @Column(name = "source_question_id", nullable = false)
    private UUID sourceQuestionId;

    @Column(name = "condition_operator", nullable = false, length = 30)
    private String conditionOperator;

    @Column(name = "condition_value", nullable = false)
    private String conditionValue;

    @Column(name = "target_page_id")
    private UUID targetPageId;

    @Column(name = "target_question_id")
    private UUID targetQuestionId;

    @Column(name = "sort_order", nullable = false)
    @Builder.Default
    private Integer sortOrder = 0;
}
