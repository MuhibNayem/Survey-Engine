package com.bracits.surveyengine.admin.dto;

import com.bracits.surveyengine.admin.entity.AdminRole;
import lombok.*;

import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private UUID userId;
    private String email;
    private String fullName;
    private String tenantId;
    private AdminRole role;
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private long expiresIn;
}
