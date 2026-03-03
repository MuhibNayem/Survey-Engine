package com.bracits.surveyengine.campaign.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * Associates a campaign with a theme template plus optional overrides.
 * <p>
 * SRS §4.4: "A campaign may select or customize a theme."
 */
@Entity
@Table(name = "survey_theme")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SurveyTheme {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "campaign_id", nullable = false, unique = true)
    private UUID campaignId;

    @Column(name = "template_id")
    private UUID templateId;

    @Column(name = "primary_color_override", length = 7)
    private String primaryColorOverride;

    @Column(name = "secondary_color_override", length = 7)
    private String secondaryColorOverride;

    @Column(name = "background_color_override", length = 7)
    private String backgroundColorOverride;

    @Column(name = "font_family_override", length = 100)
    private String fontFamilyOverride;

    @Column(name = "logo_url_override", length = 500)
    private String logoUrlOverride;

    @Column(name = "css_override", columnDefinition = "TEXT")
    private String cssOverride;
}
