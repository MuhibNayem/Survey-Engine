package com.bracits.surveyengine.response.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * An individual answer to a question within a survey response.
 * <p>
 * SRS §4.7: "Each answer records the question reference, value, and computed
 * score."
 */
@Entity
@Table(name = "answer")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_response_id", nullable = false)
    private SurveyResponse surveyResponse;

    @Column(name = "question_id", nullable = false)
    private UUID questionId;

    @Column(name = "question_version_id")
    private UUID questionVersionId;

    @Column(name = "value", columnDefinition = "TEXT")
    private String value;

    @Column(name = "score", precision = 10, scale = 2)
    private BigDecimal score;
}
