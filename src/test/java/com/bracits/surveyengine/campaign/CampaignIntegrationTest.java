package com.bracits.surveyengine.campaign;

import com.bracits.surveyengine.TestcontainersConfiguration;
import com.bracits.surveyengine.campaign.dto.*;
import com.bracits.surveyengine.campaign.entity.AuthMode;
import com.bracits.surveyengine.campaign.entity.CampaignStatus;
import com.bracits.surveyengine.campaign.service.CampaignService;
import com.bracits.surveyengine.campaign.service.DistributionService;
import com.bracits.surveyengine.common.exception.BusinessException;
import com.bracits.surveyengine.questionbank.dto.QuestionRequest;
import com.bracits.surveyengine.questionbank.dto.QuestionResponse;
import com.bracits.surveyengine.questionbank.entity.QuestionType;
import com.bracits.surveyengine.questionbank.service.QuestionService;
import com.bracits.surveyengine.survey.dto.*;
import com.bracits.surveyengine.survey.service.SurveyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class CampaignIntegrationTest {

    @Autowired
    private CampaignService campaignService;
    @Autowired
    private DistributionService distributionService;
    @Autowired
    private SurveyService surveyService;
    @Autowired
    private QuestionService questionService;

    private UUID publishedSurveyId;

    @BeforeEach
    void setUp() {
        // Create a question
        QuestionResponse q = questionService.create(QuestionRequest.builder()
                .text("Campaign test question")
                .type(QuestionType.RATING_SCALE)
                .maxScore(new BigDecimal("10.00"))
                .build());

        // Create and publish a survey
        SurveyResponse survey = surveyService.create(SurveyRequest.builder()
                .title("Campaign Test Survey")
                .pages(List.of(SurveyPageRequest.builder()
                        .title("Page 1").sortOrder(1)
                        .questions(List.of(SurveyQuestionRequest.builder()
                                .questionId(q.getId()).sortOrder(1).mandatory(true).build()))
                        .build()))
                .build());

        surveyService.transitionLifecycle(survey.getId(),
                LifecycleTransitionRequest.builder().targetState("PUBLISHED").build());
        publishedSurveyId = survey.getId();
    }

    @Test
    void shouldCreateCampaignWithDefaultSettings() {
        CampaignResponse campaign = campaignService.create(CampaignRequest.builder()
                .name("Test Campaign")
                .surveyId(publishedSurveyId)
                .build());

        assertThat(campaign.getId()).isNotNull();
        assertThat(campaign.getName()).isEqualTo("Test Campaign");
        assertThat(campaign.getAuthMode()).isEqualTo(AuthMode.PUBLIC);
        assertThat(campaign.getStatus()).isEqualTo(CampaignStatus.DRAFT);
    }

    @Test
    void shouldUpdateCampaignSettings() {
        CampaignResponse campaign = campaignService.create(CampaignRequest.builder()
                .name("Settings Test")
                .surveyId(publishedSurveyId)
                .build());

        CampaignSettingsRequest settings = CampaignSettingsRequest.builder()
                .captchaEnabled(true)
                .oneResponsePerDevice(true)
                .responseQuota(500)
                .showQuestionNumbers(false)
                .collectEmail(true)
                .build();

        CampaignResponse updated = campaignService.updateSettings(campaign.getId(), settings);
        assertThat(updated).isNotNull();
    }

    @Test
    void shouldActivateCampaignWithPublishedSurvey() {
        CampaignResponse campaign = campaignService.create(CampaignRequest.builder()
                .name("Activate Test")
                .surveyId(publishedSurveyId)
                .build());

        CampaignResponse activated = campaignService.activate(campaign.getId());

        assertThat(activated.getStatus()).isEqualTo(CampaignStatus.ACTIVE);
        assertThat(activated.getSurveySnapshotId()).isNotNull();
    }

    @Test
    void shouldRejectActivationForDraftSurvey() {
        // Create a draft survey (not published)
        QuestionResponse q = questionService.create(QuestionRequest.builder()
                .text("Draft survey question")
                .type(QuestionType.SINGLE_CHOICE)
                .maxScore(new BigDecimal("5.00"))
                .build());

        SurveyResponse draftSurvey = surveyService.create(SurveyRequest.builder()
                .title("Draft Survey")
                .pages(List.of(SurveyPageRequest.builder()
                        .title("P1").sortOrder(1)
                        .questions(List.of(SurveyQuestionRequest.builder()
                                .questionId(q.getId()).sortOrder(1).build()))
                        .build()))
                .build());

        CampaignResponse campaign = campaignService.create(CampaignRequest.builder()
                .name("Draft Campaign")
                .surveyId(draftSurvey.getId())
                .build());

        assertThatThrownBy(() -> campaignService.activate(campaign.getId()))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("PUBLISHED state");
    }

    @Test
    void shouldGenerateDistributionChannels() {
        CampaignResponse campaign = campaignService.create(CampaignRequest.builder()
                .name("Distribution Test")
                .surveyId(publishedSurveyId)
                .build());

        List<DistributionChannelResponse> channels = distributionService.generateChannels(campaign.getId());

        assertThat(channels).hasSize(6);
        assertThat(channels).extracting(DistributionChannelResponse::getChannelType)
                .containsExactlyInAnyOrder(
                        com.bracits.surveyengine.campaign.entity.DistributionChannelType.PUBLIC_LINK,
                        com.bracits.surveyengine.campaign.entity.DistributionChannelType.PRIVATE_LINK,
                        com.bracits.surveyengine.campaign.entity.DistributionChannelType.HTML_EMBED,
                        com.bracits.surveyengine.campaign.entity.DistributionChannelType.WORDPRESS_EMBED,
                        com.bracits.surveyengine.campaign.entity.DistributionChannelType.JS_EMBED,
                        com.bracits.surveyengine.campaign.entity.DistributionChannelType.EMAIL);
    }

    @Test
    void shouldListActiveCampaigns() {
        campaignService.create(CampaignRequest.builder()
                .name("Active Campaign")
                .surveyId(publishedSurveyId)
                .build());

        List<CampaignResponse> activeList = campaignService.getAllActive();
        assertThat(activeList).isNotEmpty();
    }
}
