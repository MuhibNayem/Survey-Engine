package com.bracits.surveyengine.questionbank;

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
class QuestionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private AdminAuthService adminAuthService;

    private String bearerToken;

    @BeforeEach
    void setUp() {
        try {
            AuthResponse auth = adminAuthService.register(RegisterRequest.builder()
                    .fullName("QC Test").email("qc-test@example.com")
                    .password("securepass123").tenantId("qc-tenant").build());
            bearerToken = "Bearer " + auth.getAccessToken();
        } catch (Exception e) {
            // Already registered from previous test
            AuthResponse auth = adminAuthService.login(
                    com.bracits.surveyengine.admin.dto.LoginRequest.builder()
                            .email("qc-test@example.com").password("securepass123").build());
            bearerToken = "Bearer " + auth.getAccessToken();
        }
    }

    @Test
    void shouldCreateQuestionViaApi() throws Exception {
        String json = """
                {
                    "text": "API test question",
                    "type": "RATING_SCALE",
                    "maxScore": 10.00
                }
                """;

        mockMvc.perform(post("/api/v1/questions")
                .header("Authorization", bearerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.text").value("API test question"))
                .andExpect(jsonPath("$.type").value("RATING_SCALE"))
                .andExpect(jsonPath("$.currentVersion").value(1));
    }

    @Test
    void shouldRejectInvalidQuestion() throws Exception {
        String json = """
                {
                    "text": "",
                    "maxScore": 0
                }
                """;

        mockMvc.perform(post("/api/v1/questions")
                .header("Authorization", bearerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldListActiveQuestions() throws Exception {
        mockMvc.perform(get("/api/v1/questions")
                .header("Authorization", bearerToken)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
