package com.bracits.surveyengine.auth.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * Maps external JWT/SAML claims to internal respondent fields.
 * <p>
 * SRS §4.9.3: "Claim mapping: external user ID → respondent ID,
 * email, org/group claims."
 */
@Entity
@Table(name = "claim_mapping")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ClaimMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auth_profile_id", nullable = false)
    private AuthProfile authProfile;

    /** The external claim name (e.g., "sub", "email", "groups") */
    @Column(name = "external_claim", nullable = false)
    private String externalClaim;

    /**
     * The internal field it maps to (e.g., "respondentId", "email", "organization")
     */
    @Column(name = "internal_field", nullable = false)
    private String internalField;

    /** Whether this claim is required for authentication to succeed */
    @Column(name = "required", nullable = false)
    @Builder.Default
    private boolean required = false;
}
