package com.bracits.surveyengine.featuremanagement.controller;

import com.bracits.surveyengine.TestcontainersConfiguration;
import com.bracits.surveyengine.admin.dto.AuthResponse;
import com.bracits.surveyengine.admin.dto.LoginRequest;
import com.bracits.surveyengine.admin.dto.RegisterRequest;
import com.bracits.surveyengine.admin.entity.AdminRole;
import com.bracits.surveyengine.admin.entity.AdminUser;
import com.bracits.surveyengine.admin.repository.AdminUserRepository;
import com.bracits.surveyengine.admin.repository.RefreshTokenRepository;
import com.bracits.surveyengine.admin.repository.UserPreferenceRepository;
import com.bracits.surveyengine.admin.service.AdminAuthService;
import com.bracits.surveyengine.featuremanagement.dto.CreateFeatureRequest;
import com.bracits.surveyengine.featuremanagement.dto.UpdateFeatureRequest;
import com.bracits.surveyengine.featuremanagement.entity.FeatureCategory;
import com.bracits.surveyengine.featuremanagement.entity.FeatureDefinition;
import com.bracits.surveyengine.featuremanagement.entity.FeatureType;
import com.bracits.surveyengine.featuremanagement.repository.FeatureDefinitionRepository;
import com.bracits.surveyengine.featuremanagement.repository.UserFeatureAccessRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.isA;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
@Transactional
class FeatureManagementControllerIntegrationTest {

    private static final String PASSWORD = "securepass123";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AdminAuthService adminAuthService;

    @Autowired
    private AdminUserRepository adminUserRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserPreferenceRepository userPreferenceRepository;

    @Autowired
    private FeatureDefinitionRepository featureDefinitionRepository;

    @Autowired
    private UserFeatureAccessRepository userFeatureAccessRepository;

    private String superAdminBearer;
    private String adminBearer;

    @BeforeEach
    void setUp() {
        userFeatureAccessRepository.deleteAll();
        featureDefinitionRepository.deleteAll();
        userPreferenceRepository.deleteAll();
        refreshTokenRepository.deleteAll();
        adminUserRepository.deleteAll();

        String suffix = UUID.randomUUID().toString().substring(0, 8);
        String superEmail = "superadmin+" + suffix + "@test.com";
        String adminEmail = "admin+" + suffix + "@test.com";

        registerUser(superEmail, "tenant-super-" + suffix);
        registerUser(adminEmail, "tenant-admin-" + suffix);

        AdminUser superUser = adminUserRepository.findByEmail(superEmail).orElseThrow();
        superUser.setRole(AdminRole.SUPER_ADMIN);
        adminUserRepository.save(superUser);

        superAdminBearer = "Bearer " + login(superEmail).getAccessToken();
        adminBearer = "Bearer " + login(adminEmail).getAccessToken();
    }

    @Test
    @DisplayName("SUPER_ADMIN can list all features")
    void listFeatures_SuperAdmin_ReturnsSuccess() throws Exception {
        createTestFeature("tour.test", "Test Tour");

        mockMvc.perform(get("/api/v1/admin/features")
                        .header("Authorization", superAdminBearer))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", isA(List.class)));
    }

    @Test
    @DisplayName("Non-SUPER_ADMIN cannot list all features")
    void listFeatures_NonSuperAdmin_ReturnsForbidden() throws Exception {
        mockMvc.perform(get("/api/v1/admin/features")
                        .header("Authorization", adminBearer))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("SUPER_ADMIN can create new feature")
    void createFeature_SuperAdmin_ReturnsCreated() throws Exception {
        CreateFeatureRequest request = CreateFeatureRequest.builder()
                .featureKey("tour.new_feature")
                .name("New Feature Tour")
                .featureType(FeatureType.TOUR)
                .category(FeatureCategory.DASHBOARD)
                .description("Test description")
                .enabled(true)
                .rolloutPercentage(100)
                .minPlan(com.bracits.surveyengine.subscription.entity.SubscriptionPlan.BASIC)
                .roles(List.of("ADMIN", "EDITOR"))
                .platforms(List.of("WEB"))
                .build();

        mockMvc.perform(post("/api/v1/admin/features")
                        .header("Authorization", superAdminBearer)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.featureKey").value("tour.new_feature"))
                .andExpect(jsonPath("$.name").value("New Feature Tour"));
    }

    @Test
    @DisplayName("Create feature with invalid key returns validation error")
    void createFeature_InvalidKey_ReturnsBadRequest() throws Exception {
        CreateFeatureRequest request = CreateFeatureRequest.builder()
                .featureKey("INVALID-KEY")
                .name("Invalid Feature")
                .featureType(FeatureType.TOUR)
                .category(FeatureCategory.DASHBOARD)
                .build();

        mockMvc.perform(post("/api/v1/admin/features")
                        .header("Authorization", superAdminBearer)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("SUPER_ADMIN can update feature")
    void updateFeature_SuperAdmin_ReturnsSuccess() throws Exception {
        createTestFeature("tour.update-test", "Update Test");

        UpdateFeatureRequest request = UpdateFeatureRequest.builder()
                .name("Updated Name")
                .enabled(false)
                .rolloutPercentage(50)
                .build();

        mockMvc.perform(put("/api/v1/admin/features/{featureKey}", "tour.update-test")
                        .header("Authorization", superAdminBearer)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.enabled").value(false))
                .andExpect(jsonPath("$.rolloutPercentage").value(50));
    }

    @Test
    @DisplayName("SUPER_ADMIN can delete feature")
    void deleteFeature_SuperAdmin_ReturnsNoContent() throws Exception {
        createTestFeature("tour.delete-test", "Delete Test");

        mockMvc.perform(delete("/api/v1/admin/features/{featureKey}", "tour.delete-test")
                        .header("Authorization", superAdminBearer))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/admin/features/{featureKey}", "tour.delete-test")
                        .header("Authorization", superAdminBearer))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("User can check feature status")
    void getFeatureStatus_AuthenticatedUser_ReturnsSuccess() throws Exception {
        createTestFeature("tour.status-test", "Status Test");

        mockMvc.perform(get("/api/v1/features/{featureKey}/status", "tour.status-test")
                        .header("Authorization", adminBearer))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.featureKey").value("tour.status-test"))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    @DisplayName("User can complete feature")
    void completeFeature_AuthenticatedUser_ReturnsSuccess() throws Exception {
        createTestFeature("tour.complete-test", "Complete Test");

        mockMvc.perform(post("/api/v1/features/{featureKey}/complete", "tour.complete-test")
                        .header("Authorization", adminBearer)
                        .param("completed", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.completed").value(true));
    }

    @Test
    @DisplayName("User can get available features")
    void getAvailableFeatures_AuthenticatedUser_ReturnsSuccess() throws Exception {
        createTestFeature("tour.available-1", "Available Tour 1");
        createTestFeature("tour.available-2", "Available Tour 2");

        mockMvc.perform(get("/api/v1/features/available")
                        .header("Authorization", adminBearer))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", isA(List.class)));
    }

    @Test
    @DisplayName("Runtime feature endpoint returns orchestrated items for current page")
    void getRuntimeFeatures_ReturnsItems() throws Exception {
        createTestFeature("tour.runtime-test", "Runtime Tour");

        mockMvc.perform(get("/api/v1/features/runtime")
                        .header("Authorization", adminBearer)
                        .param("pagePath", "/dashboard")
                        .param("platform", "WEB")
                        .param("max", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items", isA(List.class)));
    }

    @Test
    @DisplayName("Feature event endpoint accepts event payload")
    void recordFeatureEvent_AcceptsPayload() throws Exception {
        createTestFeature("tour.event-test", "Event Tour");

        mockMvc.perform(post("/api/v1/features/{featureKey}/events", "tour.event-test")
                        .header("Authorization", adminBearer)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "eventType": "IMPRESSION",
                                  "metadata": { "stepIndex": 1 }
                                }
                                """))
                .andExpect(status().isAccepted());
    }

    @Test
    @DisplayName("User can get feature analytics (SUPER_ADMIN only)")
    void getFeatureAnalytics_SuperAdmin_ReturnsSuccess() throws Exception {
        createTestFeature("tour.analytics-test", "Analytics Test");

        mockMvc.perform(get("/api/v1/admin/features/{featureKey}/analytics", "tour.analytics-test")
                        .header("Authorization", superAdminBearer))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.featureKey").value("tour.analytics-test"));
    }

    @Test
    @DisplayName("User can reset feature completion")
    void resetFeature_AuthenticatedUser_ReturnsSuccess() throws Exception {
        createTestFeature("tour.reset-test", "Reset Test");

        mockMvc.perform(post("/api/v1/features/{featureKey}/complete", "tour.reset-test")
                        .header("Authorization", adminBearer)
                        .param("completed", "true"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/v1/features/{featureKey}/reset", "tour.reset-test")
                        .header("Authorization", adminBearer))
                .andExpect(status().isOk());
    }

    private void registerUser(String email, String tenantId) {
        adminAuthService.register(RegisterRequest.builder()
                .fullName("Feature Test User")
                .email(email)
                .password(PASSWORD)
                .tenantId(tenantId)
                .build());
    }

    private AuthResponse login(String email) {
        return adminAuthService.login(LoginRequest.builder()
                .email(email)
                .password(PASSWORD)
                .build());
    }

    private FeatureDefinition createTestFeature(String featureKey, String name) {
        FeatureDefinition feature = FeatureDefinition.builder()
                .featureKey(featureKey)
                .name(name)
                .featureType(FeatureType.TOUR)
                .category(FeatureCategory.DASHBOARD)
                .description("Test feature")
                .enabled(true)
                .rolloutPercentage(100)
                .minPlan(com.bracits.surveyengine.subscription.entity.SubscriptionPlan.BASIC)
                .roles(List.of("ADMIN", "EDITOR", "VIEWER"))
                .platforms(List.of("WEB"))
                .metadata(java.util.Map.of("test", "value"))
                .createdBy("system")
                .build();
        return featureDefinitionRepository.save(feature);
    }
}
