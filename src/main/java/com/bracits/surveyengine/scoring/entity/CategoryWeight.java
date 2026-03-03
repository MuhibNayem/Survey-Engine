package com.bracits.surveyengine.scoring.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Assigns a weight percentage to a category within a weight profile.
 * <p>
 * SRS §5.1: "Each profile assigns a percentage weight to each category.
 * The sum of all category weights in a profile must equal exactly 100%."
 */
@Entity
@Table(name = "category_weight")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CategoryWeight {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "weight_profile_id", nullable = false)
    private WeightProfile weightProfile;

    @Column(name = "category_id", nullable = false)
    private UUID categoryId;

    @Column(name = "weight_percentage", nullable = false, precision = 5, scale = 2)
    private BigDecimal weightPercentage;
}
