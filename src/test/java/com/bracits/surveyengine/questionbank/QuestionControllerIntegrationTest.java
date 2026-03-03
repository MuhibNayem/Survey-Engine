package com.bracits.surveyengine.questionbank;

import com.bracits.surveyengine.TestcontainersConfiguration;
import com.bracits.surveyengine.questionbank.dto.QuestionRequest;
import com.bracits.surveyengine.questionbank.entity.QuestionType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class QuestionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldListActiveQuestions() throws Exception {
        mockMvc.perform(get("/api/v1/questions")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
