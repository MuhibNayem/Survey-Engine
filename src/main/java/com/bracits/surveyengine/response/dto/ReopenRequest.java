package com.bracits.surveyengine.response.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReopenRequest {
    @NotBlank(message = "Reason is required for reopening a locked response")
    private String reason;
    private Integer reopenWindowMinutes;
}
