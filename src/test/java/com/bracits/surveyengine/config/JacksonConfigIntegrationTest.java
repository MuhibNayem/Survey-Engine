package com.bracits.surveyengine.config;

import com.bracits.surveyengine.TestcontainersConfiguration;
import com.bracits.surveyengine.campaign.dto.CampaignSettingsRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class JacksonConfigIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void objectMapperBeanDeserializesDateOnlyInstant() throws Exception {
        CampaignSettingsRequest request = objectMapper.readValue("""
                {"closeDate":"2026-03-31"}
                """, CampaignSettingsRequest.class);

        assertThat(request.getCloseDate())
                .isEqualTo(Instant.parse("2026-03-31T23:59:59.999999999Z"));
    }
}
