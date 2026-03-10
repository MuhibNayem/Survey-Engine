package com.bracits.surveyengine.search;

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

import java.util.UUID;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class GlobalSearchIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AdminAuthService adminAuthService;

    private String tenantAToken;
    private String tenantBToken;
    private String titleA;
    private String titleB;

    @BeforeEach
    void setUp() throws Exception {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        tenantAToken = "Bearer " + register("search-a-" + suffix + "@example.com", "tenant-search-a-" + suffix);
        tenantBToken = "Bearer " + register("search-b-" + suffix + "@example.com", "tenant-search-b-" + suffix);
        titleA = "Isolation A " + suffix;
        titleB = "Isolation B " + suffix;

        createSurvey(tenantAToken, titleA);
        createSurvey(tenantBToken, titleB);
    }

    @Test
    void shouldReturnOnlyCurrentTenantData() throws Exception {
        mockMvc.perform(get("/api/v1/search/global")
                        .header("Authorization", tenantAToken)
                        .param("q", titleA)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[*].title", hasItem(titleA)))
                .andExpect(jsonPath("$.items[*].title", not(hasItem(titleB))));
    }

    @Test
    void shouldProvideCursorForInfiniteScroll() throws Exception {
        String suffix = UUID.randomUUID().toString().substring(0, 6);
        for (int i = 0; i < 8; i++) {
            createSurvey(tenantAToken, "Infinite Cursor " + suffix + " " + i);
        }

        String body = mockMvc.perform(get("/api/v1/search/global")
                        .header("Authorization", tenantAToken)
                        .param("q", "Infinite Cursor " + suffix)
                        .param("limit", "3")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()").value(3))
                .andExpect(jsonPath("$.nextCursor", notNullValue()))
                .andReturn()
                .getResponse()
                .getContentAsString();

        com.fasterxml.jackson.databind.JsonNode json = new com.fasterxml.jackson.databind.ObjectMapper().readTree(body);
        String nextCursor = json.path("nextCursor").asText();

        mockMvc.perform(get("/api/v1/search/global")
                        .header("Authorization", tenantAToken)
                        .param("q", "Infinite Cursor " + suffix)
                        .param("limit", "3")
                        .param("cursor", nextCursor)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()").value(3));
    }

    private String register(String email, String tenantId) {
        AuthResponse auth = adminAuthService.register(RegisterRequest.builder()
                .fullName("Search Test User")
                .email(email)
                .password("securepass123")
                .tenantId(tenantId)
                .build());
        return auth.getAccessToken();
    }

    private void createSurvey(String token, String title) throws Exception {
        String payload = """
                {
                  "title": "%s",
                  "description": "search integration test"
                }
                """.formatted(title);
        mockMvc.perform(post("/api/v1/surveys")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated());
    }
}
