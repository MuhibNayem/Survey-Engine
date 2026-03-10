package com.bracits.surveyengine.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponderSessionStatusResponse {
    private boolean authenticated;
    private String email;
}
