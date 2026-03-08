package com.bracits.surveyengine.admin;

import com.bracits.surveyengine.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import jakarta.servlet.http.Cookie;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
public class CsrfCookieIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testFullCsrfFlow() throws Exception {
        // 1. Fetch CSRF token endpoint
        MvcResult csrfResult = mockMvc.perform(get("/api/v1/admin/auth/csrf"))
                .andExpect(status().isOk())
                .andReturn();

        Cookie csrfCookie = csrfResult.getResponse().getCookie("XSRF-TOKEN");
        if (csrfCookie == null) {
            throw new IllegalStateException("CSRF cookie not returned!");
        }

        String token = csrfCookie.getValue();
        System.out.println("Extracted CSRF Token from Cookie: " + token);

        // 2. Perform a mutation (POST) with the cookie AND the header
        // For testing we hit an endpoint that requires authentication but we'll see if CSRF blocks it FIRST
        // Or hit a public mutation if any? Login is POST but ignored by CSRF.
        // Let's create an admin to get a valid token so we don't get 401 first.
        String registerContent = """
                {
                    "fullName": "CSRF Tester",
                    "email": "csrf-test@example.com",
                    "password": "Password123!",
                    "confirmPassword": "Password123!",
                    "tenantId": "test-tenant"
                }
                """;
        
        // Wait, register is public and ignored by CSRF. So it shouldn't need the CSRF token.
        MvcResult registerResult = mockMvc.perform(post("/api/v1/admin/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(registerContent))
                .andExpect(status().isOk())
                .andReturn();
        
        Cookie[] accessCookies = registerResult.getResponse().getCookies();
        Cookie tokenCookie = null;
        for (Cookie c : accessCookies) {
            if ("access_token".equals(c.getName())) tokenCookie = c;
        }

        if (tokenCookie == null) {
            throw new IllegalStateException("No access_token cookie returned");
        }

        // 3. Attempt mutation with BOTH cookies and the CSRF header
        String categoryJson = """
                {
                    "name": "CSRF Category",
                    "description": "Test"
                }
                """;
        
        System.out.println("Executing POST with CSRF header: " + token);
        MvcResult mutationResult = mockMvc.perform(post("/api/v1/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(csrfCookie, tokenCookie) // Send both cookies exactly as browser does
                .header("X-XSRF-TOKEN", token)
                .content(categoryJson))
                .andReturn();
        
        System.out.println("Mutation Response Status: " + mutationResult.getResponse().getStatus());
        System.out.println("Mutation Response Body: " + mutationResult.getResponse().getContentAsString());
        
    }
}
