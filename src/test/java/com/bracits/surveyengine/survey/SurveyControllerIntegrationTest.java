package com.bracits.surveyengine.survey;

import com.bracits.surveyengine.TestcontainersConfiguration;
import com.bracits.surveyengine.admin.dto.AuthResponse;
import com.bracits.surveyengine.admin.dto.RegisterRequest;
import com.bracits.surveyengine.admin.service.AdminAuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class SurveyControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private AdminAuthService adminAuthService;

    private String bearerToken;

    @BeforeEach
    void setUp() {
        try {
            AuthResponse auth = adminAuthService.register(RegisterRequest.builder()
                    .fullName("SC Test").email("sc-test@example.com")
                    .password("securepass123").tenantId("sc-tenant").build());
            bearerToken = "Bearer " + auth.getAccessToken();
        } catch (Exception e) {
            AuthResponse auth = adminAuthService.login(
                    com.bracits.surveyengine.admin.dto.LoginRequest.builder()
                            .email("sc-test@example.com").password("securepass123").build());
            bearerToken = "Bearer " + auth.getAccessToken();
        }
    }

    @Test
    void shouldCreateSurveyViaApi() throws Exception {
        String json = """
                {
                    "title": "API Survey",
                    "description": "Created via API"
                }
                """;

        mockMvc.perform(post("/api/v1/surveys")
                .header("Authorization", bearerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.title").value("API Survey"))
                .andExpect(jsonPath("$.lifecycleState").value("DRAFT"));
    }

    @Test
    void shouldRejectSurveyWithoutTitle() throws Exception {
        String json = """
                {
                    "description": "No title"
                }
                """;

        mockMvc.perform(post("/api/v1/surveys")
                .header("Authorization", bearerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldListSurveys() throws Exception {
        mockMvc.perform(get("/api/v1/surveys")
                .header("Authorization", bearerToken)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
