package com.bracits.surveyengine.campaign.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Campaign-level settings controlling respondent behavior.
 * <p>
 * SRS §4.5: Settings include password, CAPTCHA, one-per-device, IP/email
 * restrictions,
 * quota, close date, session timeout, question numbering, progress indicator,
 * back button,
 * start/finish messages, header/footer.
 */
@Entity
@Table(name = "campaign_settings")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CampaignSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "campaign_id", nullable = false, unique = true)
    private UUID campaignId;

    // --- Access & Security (SRS §4.5) ---
    @Column(name = "password")
    private String password;

    @Column(name = "captcha_enabled", nullable = false)
    @Builder.Default
    private boolean captchaEnabled = false;

    @Column(name = "one_response_per_device", nullable = false)
    @Builder.Default
    private boolean oneResponsePerDevice = false;

    @Column(name = "ip_restriction_enabled", nullable = false)
    @Builder.Default
    private boolean ipRestrictionEnabled = false;

    @Column(name = "email_restriction_enabled", nullable = false)
    @Builder.Default
    private boolean emailRestrictionEnabled = false;

    // --- Quota & Scheduling ---
    @Column(name = "response_quota")
    private Integer responseQuota;

    @Column(name = "close_date")
    private Instant closeDate;

    @Column(name = "session_timeout_minutes")
    @Builder.Default
    private Integer sessionTimeoutMinutes = 60;

    // --- Behavior & Layout (SRS §4.5) ---
    @Column(name = "show_question_numbers", nullable = false)
    @Builder.Default
    private boolean showQuestionNumbers = true;

    @Column(name = "show_progress_indicator", nullable = false)
    @Builder.Default
    private boolean showProgressIndicator = true;

    @Column(name = "allow_back_button", nullable = false)
    @Builder.Default
    private boolean allowBackButton = true;

    @Column(name = "start_message", columnDefinition = "TEXT")
    private String startMessage;

    @Column(name = "finish_message", columnDefinition = "TEXT")
    private String finishMessage;

    @Column(name = "header_html", columnDefinition = "TEXT")
    private String headerHtml;

    @Column(name = "footer_html", columnDefinition = "TEXT")
    private String footerHtml;

    // --- Respondent Metadata Collection (SRS §4.3) ---
    // Legacy booleans kept for backward compatibility; replaced by dataCollectionFields
    @Deprecated
    @Column(name = "collect_name", nullable = false)
    @Builder.Default
    private boolean collectName = false;

    @Deprecated
    @Column(name = "collect_email", nullable = false)
    @Builder.Default
    private boolean collectEmail = false;

    @Deprecated
    @Column(name = "collect_phone", nullable = false)
    @Builder.Default
    private boolean collectPhone = false;

    @Deprecated
    @Column(name = "collect_address", nullable = false)
    @Builder.Default
    private boolean collectAddress = false;

    // --- Dynamic Data Collection Fields ---
    @OneToMany(mappedBy = "campaignSettings", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sortOrder ASC")
    @Builder.Default
    private List<DataCollectionField> dataCollectionFields = new ArrayList<>();
}
