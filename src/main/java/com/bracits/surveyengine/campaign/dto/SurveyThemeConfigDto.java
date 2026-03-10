package com.bracits.surveyengine.campaign.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SurveyThemeConfigDto {

    private String templateKey;
    private String paletteKey;
    private Palette palette;
    private Branding branding;
    private Layout layout;
    private Motion motion;
    private Header header;
    private Footer footer;
    private Advanced advanced;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Palette {
        private String background;
        private String shell;
        private String panel;
        private String card;
        private String border;
        private String textPrimary;
        private String textSecondary;
        private String primary;
        private String primaryText;
        private String accent;
        private String accentSoft;
        private String headerBackground;
        private String headerText;
        private String footerBackground;
        private String footerText;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Branding {
        private String brandLabel;
        private String logoUrl;
        private String logoPosition;
        private String fontFamily;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Layout {
        private String contentWidth;
        private String headerStyle;
        private String headerAlignment;
        private String footerStyle;
        private String footerAlignment;
        private String sectionStyle;
        private String questionCardStyle;
        private String categorySeparatorStyle;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Motion {
        private String animationPreset;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Header {
        private boolean enabled;
        private String eyebrow;
        private String title;
        private String subtitle;
        private String note;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Footer {
        private boolean enabled;
        private String line1;
        private String line2;
        private String legal;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Advanced {
        private boolean useCustomHeaderHtml;
        private boolean useCustomFooterHtml;
        private String customHeaderHtml;
        private String customFooterHtml;
        private String customCss;
    }
}
