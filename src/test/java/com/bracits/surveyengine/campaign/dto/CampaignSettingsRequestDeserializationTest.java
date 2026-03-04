package com.bracits.surveyengine.campaign.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class CampaignSettingsRequestDeserializationTest {

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Test
    void deserializesCloseDateWhenDateOnlyStringIsProvided() throws Exception {
        CampaignSettingsRequest request = objectMapper.readValue("""
                {"closeDate":"2026-03-31"}
                """, CampaignSettingsRequest.class);

        assertThat(request.getCloseDate())
                .isEqualTo(Instant.parse("2026-03-31T23:59:59.999999999Z"));
    }

    @Test
    void deserializesCloseDateWhenIsoDateTimeIsProvided() throws Exception {
        CampaignSettingsRequest request = objectMapper.readValue("""
                {"closeDate":"2026-03-31T10:30:15Z"}
                """, CampaignSettingsRequest.class);

        assertThat(request.getCloseDate())
                .isEqualTo(Instant.parse("2026-03-31T10:30:15Z"));
    }
}
