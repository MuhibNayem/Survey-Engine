package com.bracits.surveyengine.questionbank.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Maps a question (by its version) to a category version, with per-question
 * weight.
 * <p>
 * SRS §4.2: "Questions may have per-question weight."
 */
@Entity
@Table(name = "category_question_mapping")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CategoryQuestionMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_version_id", nullable = false)
    private CategoryVersion categoryVersion;

    @Column(name = "question_id", nullable = false)
    private UUID questionId;

    @Column(name = "question_version_id", nullable = false)
    private UUID questionVersionId;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

    @Column(name = "weight", precision = 5, scale = 2)
    private BigDecimal weight;
}
