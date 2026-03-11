package com.bracits.surveyengine.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponderAuthExchangeRequest {
    private String responderToken;
    private String responderAccessCode;
}

