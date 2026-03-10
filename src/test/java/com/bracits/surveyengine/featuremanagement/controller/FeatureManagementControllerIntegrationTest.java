package com.bracits.surveyengine.featuremanagement.controller;

import com.bracits.surveyengine.admin.entity.AdminRole;
import com.bracits.surveyengine.admin.entity.AdminUser;
import com.bracits.surveyengine.admin.repository.AdminUserRepository;
import com.bracits.surveyengine.featuremanagement.dto.CreateFeatureRequest;
import com.bracits.surveyengine.featuremanagement.dto.UpdateFeatureRequest;
import com.bracits.surveyengine.featuremanagement.entity.FeatureCategory;
import com.bracits.surveyengine.featuremanagement.entity.FeatureDefinition;
import com.bracits.surveyengine.featuremanagement.entity.FeatureType;
import com.bracits.surveyengine.featuremanagement.repository.FeatureDefinitionRepository;
import com.bracits.surveyengine.featuremanagement.repository.UserFeatureAccessRepository;
import com.bracits.surveyengine.subscription.entity.Subscription;
import com.bracits.surveyengine.subscription.entity.SubscriptionPlan;
import com.bracits.surveyengine.subscription.entity.SubscriptionStatus;
import com.bracits.surveyengine.subscription.repository.SubscriptionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for FeatureManagementController.
 * Tests REST API endpoints with full Spring context.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class FeatureManagementControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FeatureDefinitionRepository featureDefinitionRepository;

    @Autowired
    private AdminUserRepository adminUserRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private UserFeatureAccessRepository userFeatureAccessRepository;

    private AdminUser superAdminUser;
    private AdminUser regularUser;
    private String superAdminToken;
    private String regularUserToken;

    @BeforeEach
    void setUp() {
        // Clean up before each test
        featureDefinitionRepository.deleteAll();
        userFeatureAccessRepository.deleteAll();
        adminUserRepository.deleteAll();
        subscriptionRepository.deleteAll();

        // Create test users
        superAdminUser = createAdminUser("superadmin@test.com", AdminRole.SUPER_ADMIN, "tenant-1");
        regularUser = createAdminUser("user@test.com", AdminRole.ADMIN, "tenant-1");

        // Create subscription
        createSubscription("tenant-1", SubscriptionPlan.PRO);
    }

    @Test
    @DisplayName("SUPER_ADMIN can list all features")
    @WithMockUser(username = "superadmin@test.com", roles = {"SUPER_ADMIN"})
    void listFeatures_SuperAdmin_ReturnsSuccess() throws Exception {
        // Arrange
        createTestFeature("tour.test", "Test Tour");

        // Act & Assert
        mockMvc.perform(get("/api/v1/admin/features")
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", isA(List.class)));
    }

    @Test
    @DisplayName("Non-SUPER_ADMIN cannot list all features")
    @WithMockUser(username = "user@test.com", roles = {"ADMIN"})
    void listFeatures_NonSuperAdmin_ReturnsForbidden() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/admin/features")
                .with(csrf()))
            .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("SUPER_ADMIN can create new feature")
    @WithMockUser(username = "superadmin@test.com", roles = {"SUPER_ADMIN"})
    void createFeature_SuperAdmin_ReturnsCreated() throws Exception {
        // Arrange
        CreateFeatureRequest request = CreateFeatureRequest.builder()
            .featureKey("tour.new-feature")
            .name("New Feature Tour")
            .featureType(FeatureType.TOUR)
            .category(FeatureCategory.DASHBOARD)
            .description("Test description")
            .enabled(true)
            .rolloutPercentage(100)
            .minPlan(SubscriptionPlan.BASIC)
            .roles(List.of("ADMIN", "EDITOR"))
            .platforms(List.of("WEB"))
            .build();

        // Act & Assert
        mockMvc.perform(post("/api/v1/admin/features")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.featureKey").value("tour.new-feature"))
            .andExpect(jsonPath("$.name").value("New Feature Tour"));
    }

    @Test
    @DisplayName("Create feature with invalid key returns validation error")
    @WithMockUser(username = "superadmin@test.com", roles = {"SUPER_ADMIN"})
    void createFeature_InvalidKey_ReturnsBadRequest() throws Exception {
        // Arrange
        CreateFeatureRequest request = CreateFeatureRequest.builder()
            .featureKey("INVALID-KEY") // Invalid: uppercase and hyphen
            .name("Invalid Feature")
            .featureType(FeatureType.TOUR)
            .category(FeatureCategory.DASHBOARD)
            .build();

        // Act & Assert
        mockMvc.perform(post("/api/v1/admin/features")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("SUPER_ADMIN can update feature")
    @WithMockUser(username = "superadmin@test.com", roles = {"SUPER_ADMIN"})
    void updateFeature_SuperAdmin_ReturnsSuccess() throws Exception {
        // Arrange
        FeatureDefinition feature = createTestFeature("tour.update-test", "Update Test");

        UpdateFeatureRequest request = UpdateFeatureRequest.builder()
            .name("Updated Name")
            .enabled(false)
            .rolloutPercentage(50)
            .build();

        // Act & Assert
        mockMvc.perform(put("/api/v1/admin/features/{featureKey}", "tour.update-test")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Updated Name"))
            .andExpect(jsonPath("$.enabled").value(false))
            .andExpect(jsonPath("$.rolloutPercentage").value(50));
    }

    @Test
    @DisplayName("SUPER_ADMIN can delete feature")
    @WithMockUser(username = "superadmin@test.com", roles = {"SUPER_ADMIN"})
    void deleteFeature_SuperAdmin_ReturnsNoContent() throws Exception {
        // Arrange
        createTestFeature("tour.delete-test", "Delete Test");

        // Act & Assert
        mockMvc.perform(delete("/api/v1/admin/features/{featureKey}", "tour.delete-test")
                .with(csrf()))
            .andExpect(status().isNoContent());

        // Verify deletion
        mockMvc.perform(get("/api/v1/admin/features/{featureKey}", "tour.delete-test")
                .with(csrf()))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("User can check feature status")
    @WithMockUser(username = "user@test.com", roles = {"ADMIN"})
    void getFeatureStatus_AuthenticatedUser_ReturnsSuccess() throws Exception {
        // Arrange
        createTestFeature("tour.status-test", "Status Test");

        // Act & Assert
        mockMvc.perform(get("/api/v1/features/{featureKey}/status", "tour.status-test")
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.featureKey").value("tour.status-test"))
            .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    @DisplayName("User can complete feature")
    @WithMockUser(username = "user@test.com", roles = {"ADMIN"})
    void completeFeature_AuthenticatedUser_ReturnsSuccess() throws Exception {
        // Arrange
        createTestFeature("tour.complete-test", "Complete Test");

        // Act & Assert
        mockMvc.perform(post("/api/v1/features/{featureKey}/complete", "tour.complete-test")
                .with(csrf())
                .param("completed", "true"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.completed").value(true));
    }

    @Test
    @DisplayName("User can get available features")
    @WithMockUser(username = "user@test.com", roles = {"ADMIN"})
    void getAvailableFeatures_AuthenticatedUser_ReturnsSuccess() throws Exception {
        // Arrange
        createTestFeature("tour.available-1", "Available Tour 1");
        createTestFeature("tour.available-2", "Available Tour 2");

        // Act & Assert
        mockMvc.perform(get("/api/v1/features/available")
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", isA(List.class)));
    }

    @Test
    @DisplayName("User can get feature analytics (SUPER_ADMIN only)")
    @WithMockUser(username = "superadmin@test.com", roles = {"SUPER_ADMIN"})
    void getFeatureAnalytics_SuperAdmin_ReturnsSuccess() throws Exception {
        // Arrange
        createTestFeature("tour.analytics-test", "Analytics Test");

        // Act & Assert
        mockMvc.perform(get("/api/v1/admin/features/{featureKey}/analytics", "tour.analytics-test")
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.featureKey").value("tour.analytics-test"));
    }

    @Test
    @DisplayName("User can reset feature completion")
    @WithMockUser(username = "user@test.com", roles = {"ADMIN"})
    void resetFeature_AuthenticatedUser_ReturnsSuccess() throws Exception {
        // Arrange
        createTestFeature("tour.reset-test", "Reset Test");

        // First complete the feature
        mockMvc.perform(post("/api/v1/features/{featureKey}/complete", "tour.reset-test")
                .with(csrf())
                .param("completed", "true"));

        // Act & Assert - Reset
        mockMvc.perform(post("/api/v1/features/{featureKey}/reset", "tour.reset-test")
                .with(csrf()))
            .andExpect(status().isOk());
    }

    // ------------------------------------------------------------------------
    // Helper Methods
    // ------------------------------------------------------------------------

    private AdminUser createAdminUser(String email, AdminRole role, String tenantId) {
        AdminUser user = AdminUser.builder()
            .email(email)
            .passwordHash("hashed-password")
            .fullName("Test User")
            .role(role)
            .tenantId(tenantId)
            .active(true)
            .build();
        return adminUserRepository.save(user);
    }

    private Subscription createSubscription(String tenantId, SubscriptionPlan plan) {
        Subscription subscription = Subscription.builder()
            .tenantId(tenantId)
            .plan(plan)
            .status(SubscriptionStatus.ACTIVE)
            .currentPeriodStart(java.time.Instant.now())
            .currentPeriodEnd(java.time.Instant.now().plusSeconds(86400 * 365))
            .build();
        return subscriptionRepository.save(subscription);
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
            .minPlan(SubscriptionPlan.BASIC)
            .roles(List.of("ADMIN", "EDITOR", "VIEWER"))
            .platforms(List.of("WEB"))
            .metadata(java.util.Map.of("test", "value"))
            .createdBy("system")
            .build();
        return featureDefinitionRepository.save(feature);
    }
}
