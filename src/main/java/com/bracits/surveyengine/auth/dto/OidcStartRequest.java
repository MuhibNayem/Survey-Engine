package com.bracits.surveyengine.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OidcStartRequest {

    @NotBlank
    private String tenantId;

    @NotNull
    private UUID campaignId;

    /**
     * Relative return path in Survey Engine UI where callback result should be redirected.
     * Example: /surveys/runtime/{campaignId}
     */
    private String returnPath;
}
