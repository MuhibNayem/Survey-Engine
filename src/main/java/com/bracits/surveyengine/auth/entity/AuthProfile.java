package com.bracits.surveyengine.auth.entity;

import com.bracits.surveyengine.common.audit.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Authentication profile for a tenant — defines how respondents
 * are authenticated before accessing any survey under this tenant.
 * <p>
 * Configured once per tenant by the admin subscriber.
 * All campaigns under the tenant inherit this auth config.
 */
@Entity
@Table(name = "auth_profile")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthProfile extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "tenant_id", nullable = false, unique = true)
    private String tenantId;

    @Enumerated(EnumType.STRING)
    @Column(name = "auth_mode", nullable = false, length = 30)
    @Builder.Default
    private AuthenticationMode authMode = AuthenticationMode.PUBLIC_ANONYMOUS;

    /** Token issuer (iss claim) — for SIGNED_LAUNCH_TOKEN and EXTERNAL_SSO_TRUST */
    @Column(name = "issuer")
    private String issuer;

    /** Expected audience (aud claim) */
    @Column(name = "audience")
    private String audience;

    /** JWKS endpoint for key discovery (SSO mode) */
    @Column(name = "jwks_endpoint")
    private String jwksEndpoint;

    /** Allowed clock skew in seconds for token validation */
    @Column(name = "clock_skew_seconds")
    @Builder.Default
    private Integer clockSkewSeconds = 30;

    /** Token TTL in seconds */
    @Column(name = "token_ttl_seconds")
    @Builder.Default
    private Integer tokenTtlSeconds = 3600;

    /** Signing secret for SIGNED_LAUNCH_TOKEN mode (HMAC) */
    @Column(name = "signing_secret")
    private String signingSecret;

    /** Current active key version for rotation support */
    @Column(name = "active_key_version")
    @Builder.Default
    private Integer activeKeyVersion = 1;

    @Enumerated(EnumType.STRING)
    @Column(name = "fallback_policy", nullable = false, length = 30)
    @Builder.Default
    private FallbackPolicy fallbackPolicy = FallbackPolicy.SSO_REQUIRED;

    @Column(name = "active", nullable = false)
    @Builder.Default
    private boolean active = true;

    @OneToMany(mappedBy = "authProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ClaimMapping> claimMappings = new ArrayList<>();
}
