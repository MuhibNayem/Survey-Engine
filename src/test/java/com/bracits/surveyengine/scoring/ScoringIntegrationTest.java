package com.bracits.surveyengine.scoring;

import com.bracits.surveyengine.TestcontainersConfiguration;
import com.bracits.surveyengine.campaign.dto.CampaignRequest;
import com.bracits.surveyengine.campaign.dto.CampaignResponse;
import com.bracits.surveyengine.campaign.entity.AuthMode;
import com.bracits.surveyengine.campaign.service.CampaignService;
import com.bracits.surveyengine.common.exception.BusinessException;
import com.bracits.surveyengine.questionbank.dto.CategoryQuestionMappingRequest;
import com.bracits.surveyengine.questionbank.dto.CategoryRequest;
import com.bracits.surveyengine.questionbank.dto.CategoryResponse;
import com.bracits.surveyengine.questionbank.dto.QuestionRequest;
import com.bracits.surveyengine.questionbank.dto.QuestionResponse;
import com.bracits.surveyengine.questionbank.entity.QuestionType;
import com.bracits.surveyengine.questionbank.service.CategoryService;
import com.bracits.surveyengine.questionbank.service.QuestionService;
import com.bracits.surveyengine.scoring.dto.*;
import com.bracits.surveyengine.scoring.service.ScoringEngineService;
import com.bracits.surveyengine.scoring.service.WeightProfileService;
import com.bracits.surveyengine.survey.dto.*;
import com.bracits.surveyengine.survey.service.SurveyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class ScoringIntegrationTest {

        @Autowired
        private WeightProfileService weightProfileService;
        @Autowired
        private ScoringEngineService scoringEngineService;
        @Autowired
        private CampaignService campaignService;
        @Autowired
        private SurveyService surveyService;
        @Autowired
        private QuestionService questionService;
        @Autowired
        private CategoryService categoryService;

        private UUID campaignId;
        private UUID categoryId1;
        private UUID categoryId2;

        @BeforeEach
        void setUp() {
                // Create two questions
                QuestionResponse q1 = questionService.create(QuestionRequest.builder()
                                .text("Scoring Q1")
                                .type(QuestionType.RATING_SCALE)
                                .maxScore(new BigDecimal("10.00"))
                                .build());
                QuestionResponse q2 = questionService.create(QuestionRequest.builder()
                                .text("Scoring Q2")
                                .type(QuestionType.RATING_SCALE)
                                .maxScore(new BigDecimal("20.00"))
                                .build());

                // Create two categories with question mappings
                CategoryResponse cat1 = categoryService.create(CategoryRequest.builder()
                                .name("Category A")
                                .questionMappings(List.of(CategoryQuestionMappingRequest.builder()
                                                .questionId(q1.getId()).sortOrder(1)
                                                .weight(new BigDecimal("1.00")).build()))
                                .build());
                CategoryResponse cat2 = categoryService.create(CategoryRequest.builder()
                                .name("Category B")
                                .questionMappings(List.of(CategoryQuestionMappingRequest.builder()
                                                .questionId(q2.getId()).sortOrder(1)
                                                .weight(new BigDecimal("1.00")).build()))
                                .build());
                categoryId1 = cat1.getId();
                categoryId2 = cat2.getId();

                // Create and publish a survey
                SurveyResponse survey = surveyService.create(SurveyRequest.builder()
                                .title("Scoring Test Survey")
                                .pages(List.of(SurveyPageRequest.builder()
                                                .title("Page 1").sortOrder(1)
                                                .questions(List.of(
                                                                SurveyQuestionRequest.builder()
                                                                                .questionId(q1.getId()).sortOrder(1)
                                                                                .mandatory(true).categoryId(categoryId1)
                                                                                .categoryWeightPercentage(
                                                                                                new BigDecimal("60.00"))
                                                                                .build(),
                                                                SurveyQuestionRequest.builder()
                                                                                .questionId(q2.getId()).sortOrder(2)
                                                                                .mandatory(true).categoryId(categoryId2)
                                                                                .categoryWeightPercentage(
                                                                                                new BigDecimal("40.00"))
                                                                                .build()))
                                                .build()))
                                .build());
                surveyService.transitionLifecycle(survey.getId(),
                                LifecycleTransitionRequest.builder().targetState("PUBLISHED").build());

                // Create campaign
                CampaignResponse campaign = campaignService.create(CampaignRequest.builder()
                                .name("Scoring Campaign")
                                .surveyId(survey.getId())
                                .authMode(AuthMode.PUBLIC)
                                .build());
                campaignId = campaign.getId();

                // Activate campaign to pin the survey snapshot (required for scoring)
                campaignService.activate(campaignId);
        }

        @Test
        void shouldCreateWeightProfile() {
                WeightProfileResponse profile = createValidProfile();

                assertThat(profile.getId()).isNotNull();
                assertThat(profile.getName()).isEqualTo("Default Profile");
                assertThat(profile.getCategoryWeights()).hasSize(2);
        }

        @Test
        void shouldValidateWeightSumExactly100() {
                WeightProfileResponse profile = createValidProfile();
                // Should not throw
                weightProfileService.validateWeightSum(profile.getId());
        }

        @Test
        void shouldRejectWeightSumNot100() {
                assertThatThrownBy(() -> weightProfileService.create(WeightProfileRequest.builder()
                                .name("Invalid Profile")
                                .campaignId(campaignId)
                                .categoryWeights(List.of(
                                                CategoryWeightRequest.builder()
                                                                .categoryId(categoryId1)
                                                                .weightPercentage(new BigDecimal("60.00")).build(),
                                                CategoryWeightRequest.builder()
                                                                .categoryId(categoryId2)
                                                                .weightPercentage(new BigDecimal("30.00")).build()))
                                .build()))
                                .isInstanceOf(BusinessException.class)
                                .hasMessageContaining("90.00%")
                                .hasMessageContaining("must be exactly 100%");
        }

        @Test
        void shouldCalculateWeightedScore() {
                WeightProfileResponse profile = createValidProfile();

                // Category A: raw=8 out of max=10, weight=60%
                // Category B: raw=15 out of max=20, weight=40%
                Map<UUID, BigDecimal> rawScores = Map.of(
                                categoryId1, new BigDecimal("8.00"),
                                categoryId2, new BigDecimal("15.00"));

                ScoreResult result = scoringEngineService.calculateScore(profile.getId(), rawScores);

                assertThat(result.getTotalScore()).isNotNull();
                assertThat(result.getCategoryScores()).hasSize(2);

                // Cat A: normalized = 8/10 = 0.8, weighted = 0.8 * 0.6 = 0.48
                // Cat B: normalized = 15/20 = 0.75, weighted = 0.75 * 0.4 = 0.30
                // Total = 0.78
                assertThat(result.getTotalScore()).isEqualByComparingTo(new BigDecimal("0.78"));
        }

        @Test
        void shouldHandleZeroRawScores() {
                WeightProfileResponse profile = createValidProfile();

                Map<UUID, BigDecimal> rawScores = Map.of(
                                categoryId1, BigDecimal.ZERO,
                                categoryId2, BigDecimal.ZERO);

                ScoreResult result = scoringEngineService.calculateScore(profile.getId(), rawScores);
                assertThat(result.getTotalScore()).isEqualByComparingTo(BigDecimal.ZERO);
        }

        @Test
        void shouldHandlePerfectScores() {
                WeightProfileResponse profile = createValidProfile();

                // Max scores: Cat A=10, Cat B=20
                Map<UUID, BigDecimal> rawScores = Map.of(
                                categoryId1, new BigDecimal("10.00"),
                                categoryId2, new BigDecimal("20.00"));

                ScoreResult result = scoringEngineService.calculateScore(profile.getId(), rawScores);
                // 10/10 * 0.6 + 20/20 * 0.4 = 0.6 + 0.4 = 1.0
                assertThat(result.getTotalScore()).isEqualByComparingTo(new BigDecimal("1.0"));
        }

        @Test
        void shouldListProfilesByCampaign() {
                createValidProfile();
                List<WeightProfileResponse> profiles = weightProfileService.getByCampaignId(campaignId);
                assertThat(profiles).isNotEmpty();
        }

        private WeightProfileResponse createValidProfile() {
                return weightProfileService.create(WeightProfileRequest.builder()
                                .name("Default Profile")
                                .campaignId(campaignId)
                                .categoryWeights(List.of(
                                                CategoryWeightRequest.builder()
                                                                .categoryId(categoryId1)
                                                                .weightPercentage(new BigDecimal("60.00")).build(),
                                                CategoryWeightRequest.builder()
                                                                .categoryId(categoryId2)
                                                                .weightPercentage(new BigDecimal("40.00")).build()))
                                .build());
        }
}
