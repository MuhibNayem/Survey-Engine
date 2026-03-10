package com.bracits.surveyengine.featuremanagement.service;

import com.bracits.surveyengine.admin.entity.AdminRole;
import com.bracits.surveyengine.admin.entity.AdminUser;
import com.bracits.surveyengine.admin.repository.AdminUserRepository;
import com.bracits.surveyengine.featuremanagement.dto.CreateFeatureRequest;
import com.bracits.surveyengine.featuremanagement.dto.FeatureDefinitionDTO;
import com.bracits.surveyengine.featuremanagement.dto.FeatureStatusDTO;
import com.bracits.surveyengine.featuremanagement.entity.FeatureCategory;
import com.bracits.surveyengine.featuremanagement.entity.FeatureDefinition;
import com.bracits.surveyengine.featuremanagement.entity.FeatureType;
import com.bracits.surveyengine.featuremanagement.entity.UserFeatureAccess;
import com.bracits.surveyengine.featuremanagement.repository.FeatureDefinitionRepository;
import com.bracits.surveyengine.featuremanagement.repository.TenantFeatureConfigRepository;
import com.bracits.surveyengine.featuremanagement.repository.UserFeatureAccessRepository;
import com.bracits.surveyengine.subscription.entity.Subscription;
import com.bracits.surveyengine.subscription.entity.SubscriptionPlan;
import com.bracits.surveyengine.subscription.repository.SubscriptionRepository;
import com.bracits.surveyengine.subscription.service.PlanCatalogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

/**
 * Unit tests for FeatureManagementService.
 * Tests core feature availability, completion tracking, and CRUD operations.
 */
@ExtendWith(MockitoExtension.class)
class FeatureManagementServiceTest {

    @Mock
    private FeatureDefinitionRepository featureDefinitionRepository;

    @Mock
    private TenantFeatureConfigRepository tenantFeatureConfigRepository;

    @Mock
    private UserFeatureAccessRepository userFeatureAccessRepository;

    @Mock
    private AdminUserRepository adminUserRepository;

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private PlanCatalogService planCatalogService;

    @Mock
    private ModelMapper modelMapper;

    private FeatureManagementService featureService;

    private UUID testUserId;
    private String testTenantId;
    private String testFeatureKey;

    @BeforeEach
    void setUp() {
        featureService = new FeatureManagementService(
            featureDefinitionRepository,
            tenantFeatureConfigRepository,
            userFeatureAccessRepository,
            adminUserRepository,
            subscriptionRepository,
            planCatalogService,
            modelMapper
        );

        testUserId = UUID.randomUUID();
        testTenantId = "tenant-123";
        testFeatureKey = "tour.dashboard";
    }

    @Test
    @DisplayName("Should return feature status as available when all conditions are met")
    void isFeatureAvailable_AllConditionsMet_ReturnsAvailable() {
        // Arrange
        FeatureDefinition feature = createTestFeature();
        AdminUser user = createTestUser();
        Subscription subscription = createTestSubscription();

        given(featureDefinitionRepository.findByFeatureKey(testFeatureKey))
            .willReturn(Optional.of(feature));
        given(adminUserRepository.findById(testUserId))
            .willReturn(Optional.of(user));
        given(subscriptionRepository.findByTenantId(testTenantId))
            .willReturn(Optional.of(subscription));
        given(userFeatureAccessRepository.findByUserIdAndFeatureId(testUserId, feature.getId()))
            .willReturn(Optional.empty());

        // Act
        FeatureStatusDTO status = featureService.isFeatureAvailable(testUserId, testFeatureKey);

        // Assert
        assertThat(status).isNotNull();
        assertThat(status.getAvailable()).isTrue();
        assertThat(status.getShouldShow()).isTrue();
        assertThat(status.getUnavailableReason()).isNull();
    }

    @Test
    @DisplayName("Should return feature status as disabled when feature is globally disabled")
    void isFeatureAvailable_FeatureDisabled_ReturnsDisabled() {
        // Arrange
        FeatureDefinition feature = createTestFeature();
        feature.setEnabled(false);

        given(featureDefinitionRepository.findByFeatureKey(testFeatureKey))
            .willReturn(Optional.of(feature));

        // Act
        FeatureStatusDTO status = featureService.isFeatureAvailable(testUserId, testFeatureKey);

        // Assert
        assertThat(status.getAvailable()).isFalse();
        assertThat(status.getUnavailableReason()).isEqualTo("DISABLED");
    }

    @Test
    @DisplayName("Should return feature status as plan restricted when user plan is insufficient")
    void isFeatureAvailable_InsufficientPlan_ReturnsPlanRestricted() {
        // Arrange
        FeatureDefinition feature = createTestFeature();
        feature.setMinPlan(SubscriptionPlan.ENTERPRISE);
        AdminUser user = createTestUser();
        Subscription subscription = createTestSubscription();
        subscription.setPlan(SubscriptionPlan.BASIC);

        given(featureDefinitionRepository.findByFeatureKey(testFeatureKey))
            .willReturn(Optional.of(feature));
        given(adminUserRepository.findById(testUserId))
            .willReturn(Optional.of(user));
        given(subscriptionRepository.findByTenantId(testTenantId))
            .willReturn(Optional.of(subscription));

        // Act
        FeatureStatusDTO status = featureService.isFeatureAvailable(testUserId, testFeatureKey);

        // Assert
        assertThat(status.getAvailable()).isFalse();
        assertThat(status.getUnavailableReason()).isEqualTo("PLAN_TOO_LOW");
    }

    @Test
    @DisplayName("Should return feature status as role restricted when user role is not allowed")
    void isFeatureAvailable_RoleNotAllowed_ReturnsRoleRestricted() {
        // Arrange
        FeatureDefinition feature = createTestFeature();
        feature.setRoles(List.of("ADMIN"));
        AdminUser user = createTestUser();
        user.setRole(AdminRole.VIEWER);

        given(featureDefinitionRepository.findByFeatureKey(testFeatureKey))
            .willReturn(Optional.of(feature));
        given(adminUserRepository.findById(testUserId))
            .willReturn(Optional.of(user));

        // Act
        FeatureStatusDTO status = featureService.isFeatureAvailable(testUserId, testFeatureKey);

        // Assert
        assertThat(status.getAvailable()).isFalse();
        assertThat(status.getUnavailableReason()).isEqualTo("ROLE_NOT_ALLOWED");
    }

    @Test
    @DisplayName("Should mark feature as completed successfully")
    void completeFeature_SuccessfullyMarksCompleted() {
        // Arrange
        FeatureDefinition feature = createTestFeature();
        AdminUser user = createTestUser();
        UserFeatureAccess access = UserFeatureAccess.builder()
            .userId(testUserId)
            .tenantId(testTenantId)
            .feature(feature)
            .accessed(true)
            .completed(false)
            .accessCount(1)
            .build();

        given(featureDefinitionRepository.findByFeatureKey(testFeatureKey))
            .willReturn(Optional.of(feature));
        given(userFeatureAccessRepository.findByUserIdAndFeatureId(testUserId, feature.getId()))
            .willReturn(Optional.of(access));

        // Act
        FeatureStatusDTO status = featureService.completeFeature(testUserId, testFeatureKey);

        // Assert
        assertThat(status).isNotNull();
        assertThat(status.getCompleted()).isTrue();
        verify(userFeatureAccessRepository).save(argThat(a -> a.isCompleted()));
    }

    @Test
    @DisplayName("Should create new feature successfully")
    void createFeature_SuccessfullyCreates() {
        // Arrange
        CreateFeatureRequest request = CreateFeatureRequest.builder()
            .featureKey(testFeatureKey)
            .name("Dashboard Tour")
            .featureType(FeatureType.TOUR)
            .category(FeatureCategory.DASHBOARD)
            .enabled(true)
            .rolloutPercentage(100)
            .minPlan(SubscriptionPlan.BASIC)
            .roles(List.of("ADMIN", "EDITOR"))
            .platforms(List.of("WEB"))
            .metadata(new HashMap<>())
            .build();

        FeatureDefinition feature = FeatureDefinition.builder()
            .id(UUID.randomUUID())
            .featureKey(testFeatureKey)
            .name("Dashboard Tour")
            .featureType(FeatureType.TOUR)
            .category(FeatureCategory.DASHBOARD)
            .enabled(true)
            .rolloutPercentage(100)
            .minPlan(SubscriptionPlan.BASIC)
            .roles(List.of("ADMIN", "EDITOR"))
            .platforms(List.of("WEB"))
            .metadata(new HashMap<>())
            .createdBy("test@example.com")
            .build();

        FeatureDefinitionDTO dto = FeatureDefinitionDTO.builder()
            .id(feature.getId())
            .featureKey(testFeatureKey)
            .name("Dashboard Tour")
            .build();

        given(featureDefinitionRepository.existsByFeatureKey(testFeatureKey))
            .willReturn(false);
        given(featureDefinitionRepository.save(any(FeatureDefinition.class)))
            .willAnswer(invocation -> invocation.getArgument(0));
        given(modelMapper.map(any(FeatureDefinition.class), eq(FeatureDefinitionDTO.class)))
            .willReturn(dto);

        // Act
        FeatureDefinitionDTO result = featureService.createFeature(request, "test@example.com");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getFeatureKey()).isEqualTo(testFeatureKey);
        verify(featureDefinitionRepository).save(any(FeatureDefinition.class));
    }

    @Test
    @DisplayName("Should throw exception when creating duplicate feature")
    void createFeature_Duplicate_ThrowsException() {
        // Arrange
        CreateFeatureRequest request = CreateFeatureRequest.builder()
            .featureKey(testFeatureKey)
            .name("Dashboard Tour")
            .featureType(FeatureType.TOUR)
            .category(FeatureCategory.DASHBOARD)
            .build();

        given(featureDefinitionRepository.existsByFeatureKey(testFeatureKey))
            .willReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> featureService.createFeature(request, "test@example.com"))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("already exists");
    }

    @Test
    @DisplayName("Should reset feature completion status")
    void resetFeature_SuccessfullyResets() {
        // Arrange
        FeatureDefinition feature = createTestFeature();
        UserFeatureAccess access = UserFeatureAccess.builder()
            .userId(testUserId)
            .tenantId(testTenantId)
            .feature(feature)
            .accessed(true)
            .completed(true)
            .accessCount(5)
            .build();

        given(featureDefinitionRepository.findByFeatureKey(testFeatureKey))
            .willReturn(Optional.of(feature));
        given(userFeatureAccessRepository.findByUserIdAndFeatureId(testUserId, feature.getId()))
            .willReturn(Optional.of(access));

        // Act
        featureService.resetFeature(testUserId, testFeatureKey);

        // Assert
        verify(userFeatureAccessRepository).save(argThat(a -> 
            !a.isCompleted() && !a.isAccessed() && a.getAccessCount() == 0
        ));
    }

    @Test
    @DisplayName("Should return available features filtered by plan and role")
    void getAvailableFeatures_FiltersByPlanAndRole() {
        // Arrange
        AdminUser user = createTestUser();
        Subscription subscription = createTestSubscription();
        FeatureDefinition feature1 = createTestFeature();
        FeatureDefinition feature2 = createTestFeature();
        feature2.setFeatureKey("tour.surveys");
        feature2.setMinPlan(SubscriptionPlan.PRO);

        given(adminUserRepository.findById(testUserId))
            .willReturn(Optional.of(user));
        given(subscriptionRepository.findByTenantId(testTenantId))
            .willReturn(Optional.of(subscription));
        given(featureDefinitionRepository.findByMinPlanLevelAndEnabledTrue(SubscriptionPlan.BASIC.name()))
            .willReturn(List.of(feature1, feature2));
        given(tenantFeatureConfigRepository.findByTenantIdAndFeatureId(anyString(), any(UUID.class)))
            .willReturn(Optional.empty());
        given(userFeatureAccessRepository.findByUserIdAndCompletedTrue(testUserId))
            .willReturn(List.of());
        given(modelMapper.map(any(FeatureDefinition.class), eq(FeatureDefinitionDTO.class)))
            .willAnswer(invocation -> {
                FeatureDefinition feature = invocation.getArgument(0);
                return FeatureDefinitionDTO.builder()
                    .id(feature.getId())
                    .featureKey(feature.getFeatureKey())
                    .name(feature.getName())
                    .build();
            });

        // Act
        List<FeatureDefinitionDTO> available = featureService.getAvailableFeatures(testUserId, null);

        // Assert
        assertThat(available).hasSize(2);
    }

    // ------------------------------------------------------------------------
    // Helper Methods
    // ------------------------------------------------------------------------

    private FeatureDefinition createTestFeature() {
        return FeatureDefinition.builder()
            .id(UUID.randomUUID())
            .featureKey(testFeatureKey)
            .name("Dashboard Tour")
            .featureType(FeatureType.TOUR)
            .category(FeatureCategory.DASHBOARD)
            .description("Test tour")
            .enabled(true)
            .rolloutPercentage(100)
            .minPlan(SubscriptionPlan.BASIC)
            .roles(List.of("ADMIN", "EDITOR", "VIEWER"))
            .platforms(List.of("WEB"))
            .metadata(new HashMap<>())
            .createdBy("system")
            .build();
    }

    private AdminUser createTestUser() {
        return AdminUser.builder()
            .id(testUserId)
            .email("test@example.com")
            .fullName("Test User")
            .role(AdminRole.ADMIN)
            .tenantId(testTenantId)
            .active(true)
            .build();
    }

    private Subscription createTestSubscription() {
        return Subscription.builder()
            .id(UUID.randomUUID())
            .tenantId(testTenantId)
            .plan(SubscriptionPlan.BASIC)
            .status(com.bracits.surveyengine.subscription.entity.SubscriptionStatus.ACTIVE)
            .build();
    }
}
