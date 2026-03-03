package com.bracits.surveyengine.campaign.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * A predefined theme template (e.g., Modern Dark, Classic Light).
 * Seeded via migration data.
 * <p>
 * SRS §4.4: "System shall offer predefined templates and allow custom theme
 * selection."
 */
@Entity
@Table(name = "theme_template")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ThemeTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "primary_color", length = 7)
    private String primaryColor;

    @Column(name = "secondary_color", length = 7)
    private String secondaryColor;

    @Column(name = "background_color", length = 7)
    private String backgroundColor;

    @Column(name = "font_family", length = 100)
    private String fontFamily;

    @Column(name = "logo_url", length = 500)
    private String logoUrl;

    @Column(name = "css_override", columnDefinition = "TEXT")
    private String cssOverride;

    @Column(name = "is_system", nullable = false)
    @Builder.Default
    private boolean system = true;
}
