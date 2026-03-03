package com.bracits.surveyengine.auth;

import com.bracits.surveyengine.TestcontainersConfiguration;
import com.bracits.surveyengine.admin.dto.AuthResponse;
import com.bracits.surveyengine.admin.dto.LoginRequest;
import com.bracits.surveyengine.admin.dto.RegisterRequest;
import com.bracits.surveyengine.admin.service.AdminAuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class AuthProviderTemplateIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AdminAuthService adminAuthService;

    @Test
    void shouldExposeProviderTemplatesForAuthenticatedAdmin() throws Exception {
        String bearer = "Bearer " + registerAndLogin("templates+" + UUID.randomUUID() + "@example.com");

        mockMvc.perform(get("/api/v1/auth/providers/templates")
                .header("Authorization", bearer)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].providerCode").exists())
                .andExpect(jsonPath("$[0].defaultScopes").isArray());

        mockMvc.perform(get("/api/v1/auth/providers/templates/KEYCLOAK")
                .header("Authorization", bearer)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.providerCode").value("KEYCLOAK"))
                .andExpect(jsonPath("$.defaultClaimMappings[0].internalField").value("respondentId"));
    }

    private String registerAndLogin(String email) {
        String tenantId = "tenant-templates-" + UUID.randomUUID();
        AuthResponse auth = adminAuthService.register(RegisterRequest.builder()
                .fullName("Template User")
                .email(email)
                .password("securepass123")
                .tenantId(tenantId)
                .build());
        return adminAuthService.login(LoginRequest.builder()
                .email(auth.getEmail())
                .password("securepass123")
                .build()).getAccessToken();
    }
}
