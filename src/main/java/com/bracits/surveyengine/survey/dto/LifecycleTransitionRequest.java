package com.bracits.surveyengine.survey.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LifecycleTransitionRequest {

    @NotBlank(message = "Target state is required")
    private String targetState;

    /**
     * Required when reopening (Closed → Published). SRS §4.6.
     */
    private String reason;
}
