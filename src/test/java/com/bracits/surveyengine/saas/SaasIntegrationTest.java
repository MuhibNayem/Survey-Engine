package com.bracits.surveyengine.saas;

import com.bracits.surveyengine.TestcontainersConfiguration;
import com.bracits.surveyengine.admin.dto.AuthResponse;
import com.bracits.surveyengine.admin.dto.LoginRequest;
import com.bracits.surveyengine.admin.dto.RegisterRequest;
import com.bracits.surveyengine.admin.service.AdminAuthService;
import com.bracits.surveyengine.subscription.entity.Subscription;
import com.bracits.surveyengine.subscription.entity.SubscriptionStatus;
import com.bracits.surveyengine.subscription.repository.SubscriptionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class SaasIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AdminAuthService adminAuthService;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void tenantIsolation_questionsAreScopedPerTenant() throws Exception {
        String tenantA = "saas-tenant-a-" + UUID.randomUUID();
        String tenantB = "saas-tenant-b-" + UUID.randomUUID();

        String bearerA = "Bearer " + registerAndLogin("a+" + UUID.randomUUID() + "@example.com", tenantA);
        String bearerB = "Bearer " + registerAndLogin("b+" + UUID.randomUUID() + "@example.com", tenantB);

        mockMvc.perform(post("/api/v1/questions")
                .header("Authorization", bearerA)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "text": "Tenant A Question",
                          "type": "RATING_SCALE",
                          "maxScore": 10.0
                        }
                        """))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/questions")
                .header("Authorization", bearerB)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "text": "Tenant B Question",
                          "type": "RATING_SCALE",
                          "maxScore": 10.0
                        }
                        """))
                .andExpect(status().isCreated());

        String tenantAList = mockMvc.perform(get("/api/v1/questions")
                .header("Authorization", bearerA)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String tenantBList = mockMvc.perform(get("/api/v1/questions")
                .header("Authorization", bearerB)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertThat(tenantAList).contains("Tenant A Question");
        assertThat(tenantAList).doesNotContain("Tenant B Question");
        assertThat(tenantBList).contains("Tenant B Question");
        assertThat(tenantBList).doesNotContain("Tenant A Question");
    }

    @Test
    void subscriptionCheckout_initiatesSessionAndActivatesPlanAfterCallback() throws Exception {
        String tenantId = "saas-sub-" + UUID.randomUUID();
        String bearer = "Bearer " + registerAndLogin("sub+" + UUID.randomUUID() + "@example.com", tenantId);

        mockMvc.perform(get("/api/v1/admin/subscriptions/me")
                .header("Authorization", bearer)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tenantId").value(tenantId));

        String body = mockMvc.perform(post("/api/v1/admin/subscriptions/checkout")
                .header("Authorization", bearer)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "plan": "PRO",
                          "source": "settings"
                        }
                        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.provider").value("MOCK"))
                .andExpect(jsonPath("$.paymentUrl").value(org.hamcrest.Matchers.containsString("sandbox.sslcommerz.com/mock/")))
                .andReturn()
                .getResponse()
                .getContentAsString();
        String gatewayReference = objectMapper.readTree(body).path("gatewayReference").asText();

        mockMvc.perform(post("/api/v1/public/payments/sslcommerz/success")
                .param("tran_id", gatewayReference)
                .param("val_id", "mock-val-id")
                .param("status", "VALID"))
                .andExpect(status().isFound());

        mockMvc.perform(get("/api/v1/admin/subscriptions/me")
                .header("Authorization", bearer)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tenantId").value(tenantId))
                .andExpect(jsonPath("$.plan").value("PRO"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.lastPaymentGatewayReference").value(gatewayReference));
    }

    @Test
    void subscriptionCheckout_acceptsPlanCodeAlias() throws Exception {
        String tenantId = "saas-sub-alias-" + UUID.randomUUID();
        String bearer = "Bearer " + registerAndLogin("sub-alias+" + UUID.randomUUID() + "@example.com", tenantId);

        mockMvc.perform(post("/api/v1/admin/subscriptions/checkout")
                .header("Authorization", bearer)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "planCode": "PRO",
                          "source": "settings"
                        }
                        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.provider").value("MOCK"));
    }

    @Test
    void subscriptionCheckout_rejectsDowngradeForActiveSubscription() throws Exception {
        String tenantId = "saas-sub-downgrade-" + UUID.randomUUID();
        String bearer = "Bearer " + registerAndLogin("sub-downgrade+" + UUID.randomUUID() + "@example.com", tenantId);

        String firstCheckout = mockMvc.perform(post("/api/v1/admin/subscriptions/checkout")
                .header("Authorization", bearer)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "planCode": "PRO",
                          "source": "settings"
                        }
                        """))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        mockMvc.perform(post("/api/v1/public/payments/sslcommerz/success")
                .param("tran_id", objectMapper.readTree(firstCheckout).path("gatewayReference").asText())
                .param("val_id", "mock-val-id")
                .param("status", "VALID"))
                .andExpect(status().isFound());

        mockMvc.perform(post("/api/v1/admin/subscriptions/checkout")
                .header("Authorization", bearer)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "planCode": "BASIC"
                        }
                        """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Only plan upgrades are allowed for active or trial subscriptions"));
    }

    @Test
    void subscriptionCheckout_rejectsSamePlanForActiveSubscription() throws Exception {
        String tenantId = "saas-sub-same-plan-" + UUID.randomUUID();
        String bearer = "Bearer " + registerAndLogin("sub-same+" + UUID.randomUUID() + "@example.com", tenantId);

        String firstCheckout = mockMvc.perform(post("/api/v1/admin/subscriptions/checkout")
                .header("Authorization", bearer)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "planCode": "PRO",
                          "source": "settings"
                        }
                        """))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        mockMvc.perform(post("/api/v1/public/payments/sslcommerz/success")
                .param("tran_id", objectMapper.readTree(firstCheckout).path("gatewayReference").asText())
                .param("val_id", "mock-val-id")
                .param("status", "VALID"))
                .andExpect(status().isFound());

        mockMvc.perform(post("/api/v1/admin/subscriptions/checkout")
                .header("Authorization", bearer)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "planCode": "PRO"
                        }
                        """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Requested plan is already active for this tenant"));
    }

    @Test
    void adminApis_areBlockedWhenSubscriptionExpired() throws Exception {
        String tenantId = "saas-expired-" + UUID.randomUUID();
        String bearer = "Bearer " + registerAndLogin("expired+" + UUID.randomUUID() + "@example.com", tenantId);

        Subscription subscription = subscriptionRepository.findByTenantId(tenantId).orElseThrow();
        subscription.setStatus(SubscriptionStatus.EXPIRED);
        subscription.setCurrentPeriodEnd(Instant.now().minus(1, ChronoUnit.DAYS));
        subscriptionRepository.save(subscription);

        mockMvc.perform(get("/api/v1/questions")
                .header("Authorization", bearer)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isPaymentRequired());
    }

    private String registerAndLogin(String email, String tenantId) {
        AuthResponse auth = adminAuthService.register(RegisterRequest.builder()
                .fullName("SaaS User")
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
