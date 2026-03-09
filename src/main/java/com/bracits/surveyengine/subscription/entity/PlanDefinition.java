package com.bracits.surveyengine.subscription.entity;

import com.bracits.surveyengine.common.audit.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "plan_definition")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PlanDefinition extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "plan_code", nullable = false, unique = true, length = 30)
    private SubscriptionPlan planCode;

    @Column(name = "display_name", nullable = false, length = 255)
    private String displayName;

    @Column(name = "price", nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(name = "currency", nullable = false, length = 10)
    private String currency;

    @Column(name = "billing_cycle_days", nullable = false)
    private Integer billingCycleDays;

    @Column(name = "trial_days", nullable = false)
    private Integer trialDays;

    @Column(name = "max_campaigns")
    private Integer maxCampaigns;

    @Column(name = "max_responses_per_campaign")
    private Integer maxResponsesPerCampaign;

    @Column(name = "max_admin_users")
    private Integer maxAdminUsers;

    @Column(name = "weight_profiles_enabled", nullable = false)
    @Builder.Default
    private boolean weightProfilesEnabled = false;

    @Column(name = "signed_token_enabled", nullable = false)
    @Builder.Default
    private boolean signedTokenEnabled = false;

    @Column(name = "sso_enabled", nullable = false)
    @Builder.Default
    private boolean ssoEnabled = false;

    @Column(name = "custom_branding_enabled", nullable = false)
    @Builder.Default
    private boolean customBrandingEnabled = false;

    @Column(name = "device_fingerprint_enabled", nullable = false)
    @Builder.Default
    private boolean deviceFingerprintEnabled = false;

    @Column(name = "api_access_enabled", nullable = false)
    @Builder.Default
    private boolean apiAccessEnabled = false;

    @Column(name = "active", nullable = false)
    @Builder.Default
    private boolean active = true;

    @ManyToMany(fetch = jakarta.persistence.FetchType.EAGER)
    @jakarta.persistence.JoinTable(
            name = "plan_definition_features",
            joinColumns = @jakarta.persistence.JoinColumn(name = "plan_definition_id"),
            inverseJoinColumns = @jakarta.persistence.JoinColumn(name = "feature_id")
    )
    @Builder.Default
    private Set<PlanFeature> features = new HashSet<>();
}
