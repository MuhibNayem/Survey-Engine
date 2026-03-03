package com.bracits.surveyengine.saas;

import com.bracits.surveyengine.TestcontainersConfiguration;
import com.bracits.surveyengine.admin.context.TenantContext;
import com.bracits.surveyengine.admin.dto.AuthResponse;
import com.bracits.surveyengine.admin.dto.LoginRequest;
import com.bracits.surveyengine.admin.dto.RegisterRequest;
import com.bracits.surveyengine.admin.entity.AdminRole;
import com.bracits.surveyengine.admin.entity.AdminUser;
import com.bracits.surveyengine.admin.repository.AdminUserRepository;
import com.bracits.surveyengine.admin.service.AdminAuthService;
import com.bracits.surveyengine.campaign.dto.CampaignRequest;
import com.bracits.surveyengine.campaign.service.CampaignService;
import com.bracits.surveyengine.common.exception.BusinessException;
import com.bracits.surveyengine.common.exception.ErrorCode;
import com.bracits.surveyengine.questionbank.dto.QuestionRequest;
import com.bracits.surveyengine.questionbank.dto.QuestionResponse;
import com.bracits.surveyengine.questionbank.entity.QuestionType;
import com.bracits.surveyengine.questionbank.service.QuestionService;
import com.bracits.surveyengine.survey.dto.LifecycleTransitionRequest;
import com.bracits.surveyengine.survey.dto.SurveyPageRequest;
import com.bracits.surveyengine.survey.dto.SurveyQuestionRequest;
import com.bracits.surveyengine.survey.dto.SurveyRequest;
import com.bracits.surveyengine.survey.dto.SurveyResponse;
import com.bracits.surveyengine.survey.service.SurveyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class PlanManagementIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AdminAuthService adminAuthService;

    @Autowired
    private AdminUserRepository adminUserRepository;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private SurveyService surveyService;

    @Autowired
    private CampaignService campaignService;

    @Test
    void planUpsert_isAllowedForSuperAdminOnly() throws Exception {
        String superTenant = "platform-tenant-" + UUID.randomUUID();
        String superEmail = "super+" + UUID.randomUUID() + "@example.com";
        registerUser(superEmail, superTenant);
        AdminUser superUser = adminUserRepository.findByEmail(superEmail).orElseThrow();
        superUser.setRole(AdminRole.SUPER_ADMIN);
        adminUserRepository.save(superUser);
        String superToken = login(superEmail);

        String adminToken = login(registerUser("admin+" + UUID.randomUUID() + "@example.com", "tenant-" + UUID.randomUUID()));

        String payload = """
                {
                  "planCode": "BASIC",
                  "displayName": "Basic",
                  "price": 39.00,
                  "currency": "USD",
                  "billingCycleDays": 30,
                  "trialDays": 14,
                  "maxCampaigns": 1000,
                  "maxResponsesPerCampaign": 500000,
                  "maxAdminUsers": 100,
                  "active": true
                }
                """;

        mockMvc.perform(put("/api/v1/admin/plans")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isForbidden());

        mockMvc.perform(put("/api/v1/admin/plans")
                .header("Authorization", "Bearer " + superToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isOk());
    }

    @Test
    void updatedPlanQuota_isEnforcedForCampaignCreation() {
        String superTenant = "platform-tenant-" + UUID.randomUUID();
        String superEmail = "super2+" + UUID.randomUUID() + "@example.com";
        registerUser(superEmail, superTenant);
        AdminUser superUser = adminUserRepository.findByEmail(superEmail).orElseThrow();
        superUser.setRole(AdminRole.SUPER_ADMIN);
        adminUserRepository.save(superUser);
        String superToken = login(superEmail);

        try {
            updateBasicPlan(superToken, 1);

            String tenantId = "quota-tenant-" + UUID.randomUUID();
            registerUser("quota+" + UUID.randomUUID() + "@example.com", tenantId);

            TenantContext.set(new TenantContext.TenantInfo(tenantId, "quota-admin", "quota@example.com", Set.of("ADMIN")));
            try {
                QuestionResponse question = questionService.create(QuestionRequest.builder()
                        .text("Quota Q")
                        .type(QuestionType.RATING_SCALE)
                        .maxScore(new BigDecimal("10.00"))
                        .build());

                SurveyResponse survey = surveyService.create(SurveyRequest.builder()
                        .title("Quota Survey")
                        .pages(List.of(SurveyPageRequest.builder()
                                .title("P1").sortOrder(1)
                                .questions(List.of(SurveyQuestionRequest.builder()
                                        .questionId(question.getId()).sortOrder(1).mandatory(true).build()))
                                .build()))
                        .build());
                surveyService.transitionLifecycle(survey.getId(),
                        LifecycleTransitionRequest.builder().targetState("PUBLISHED").build());

                campaignService.create(CampaignRequest.builder()
                        .name("Campaign One")
                        .surveyId(survey.getId())
                        .build());

                assertThatThrownBy(() -> campaignService.create(CampaignRequest.builder()
                        .name("Campaign Two")
                        .surveyId(survey.getId())
                        .build()))
                        .isInstanceOf(BusinessException.class)
                        .satisfies(ex -> assertThat(((BusinessException) ex).getErrorCode()).isEqualTo(ErrorCode.QUOTA_EXCEEDED));
            } finally {
                TenantContext.clear();
            }
        } finally {
            updateBasicPlan(superToken, 1000);
        }
    }

    private String registerUser(String email, String tenantId) {
        AuthResponse auth = adminAuthService.register(RegisterRequest.builder()
                .fullName("Plan User")
                .email(email)
                .password("securepass123")
                .tenantId(tenantId)
                .build());
        return auth.getEmail();
    }

    private String login(String email) {
        return adminAuthService.login(LoginRequest.builder()
                .email(email)
                .password("securepass123")
                .build()).getAccessToken();
    }

    private void updateBasicPlan(String superToken, int maxCampaigns) {
        String payload = """
                {
                  "planCode": "BASIC",
                  "displayName": "Basic",
                  "price": 39.00,
                  "currency": "USD",
                  "billingCycleDays": 30,
                  "trialDays": 14,
                  "maxCampaigns": %d,
                  "maxResponsesPerCampaign": 500000,
                  "maxAdminUsers": 100,
                  "active": true
                }
                """.formatted(maxCampaigns);

        try {
            mockMvc.perform(put("/api/v1/admin/plans")
                    .header("Authorization", "Bearer " + superToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(payload))
                    .andExpect(status().isOk());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
