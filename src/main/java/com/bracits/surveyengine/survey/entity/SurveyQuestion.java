package com.bracits.surveyengine.survey.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * A question placed on a survey page, referencing a bank question and its
 * version.
 * <p>
 * SRS §4.1: "Questions within a page are individually ordered."
 */
@Entity
@Table(name = "survey_question")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SurveyQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "page_id", nullable = false)
    private SurveyPage page;

    @Column(name = "question_id", nullable = false)
    private UUID questionId;

    @Column(name = "question_version_id")
    private UUID questionVersionId;

    @Column(name = "category_id")
    private UUID categoryId;

    @Column(name = "category_version_id")
    private UUID categoryVersionId;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

    @Column(name = "mandatory", nullable = false)
    @Builder.Default
    private boolean mandatory = false;

    @Column(name = "answer_config", columnDefinition = "TEXT")
    private String answerConfig;
}
