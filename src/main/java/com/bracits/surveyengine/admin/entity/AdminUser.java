package com.bracits.surveyengine.admin.entity;

import com.bracits.surveyengine.common.audit.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.UUID;

/**
 * Engine-owned admin user entity.
 * Admin users register and login through the engine itself.
 * Respondent auth is separate and configured externally per campaign.
 */
@Entity
@Table(name = "admin_user")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AdminUser extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 30)
    @Builder.Default
    private AdminRole role = AdminRole.ADMIN;

    @Column(name = "tenant_id", nullable = false)
    private String tenantId;

    @Column(name = "active", nullable = false)
    @Builder.Default
    private boolean active = true;

    /**
     * Tracks if this is the user's first login after registration.
     * Set to false after first successful login completion.
     */
    @Column(name = "first_login", nullable = false)
    @Builder.Default
    private boolean firstLogin = true;

    /**
     * Last login timestamp for tracking user activity.
     */
    @Column(name = "last_login_at")
    private Instant lastLoginAt;
}
