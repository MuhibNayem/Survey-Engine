package com.bracits.surveyengine.response.dto;

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
public class ResponseDraftLookupRequest {

    private UUID responseId;

    private String respondentIdentifier;
    private String responderToken;
    private String responderAccessCode;
}
