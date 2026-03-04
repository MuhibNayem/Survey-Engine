package com.bracits.surveyengine.admin.dto;

import com.bracits.surveyengine.admin.entity.AdminRole;
import lombok.*;

import java.util.UUID;

/**
 * Safe auth response DTO that does NOT expose tokens.
 * Tokens are set as HttpOnly cookies by the controller instead.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthUserResponse {
    private UUID userId;
    private String email;
    private String fullName;
    private String tenantId;
    private AdminRole role;
}
