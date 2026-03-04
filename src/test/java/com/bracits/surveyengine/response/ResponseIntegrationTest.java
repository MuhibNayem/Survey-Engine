package com.bracits.surveyengine.response;

import com.bracits.surveyengine.TestcontainersConfiguration;
import com.bracits.surveyengine.admin.context.TenantContext;
import com.bracits.surveyengine.auth.dto.AuthProfileRequest;
import com.bracits.surveyengine.auth.entity.ResponderAccessCode;
import com.bracits.surveyengine.auth.entity.AuthenticationMode;
import com.bracits.surveyengine.auth.repository.ResponderAccessCodeRepository;
import com.bracits.surveyengine.auth.service.AuthProfileService;
import com.bracits.surveyengine.campaign.dto.CampaignRequest;
import com.bracits.surveyengine.campaign.dto.CampaignResponse;
import com.bracits.surveyengine.campaign.entity.AuthMode;
import com.bracits.surveyengine.campaign.service.CampaignService;
import com.bracits.surveyengine.common.exception.BusinessException;
import com.bracits.surveyengine.questionbank.dto.QuestionRequest;
import com.bracits.surveyengine.questionbank.dto.QuestionResponse;
import com.bracits.surveyengine.questionbank.entity.QuestionType;
import com.bracits.surveyengine.questionbank.service.QuestionService;
import com.bracits.surveyengine.response.dto.*;
import com.bracits.surveyengine.response.entity.ResponseStatus;
import com.bracits.surveyengine.response.service.AnalyticsService;
import com.bracits.surveyengine.response.service.ResponseLockingService;
import com.bracits.surveyengine.response.service.ResponseService;
import com.bracits.surveyengine.survey.dto.*;
import com.bracits.surveyengine.survey.service.SurveyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class ResponseIntegrationTest {

    @Autowired
    private ResponseService responseService;
    @Autowired
    private ResponseLockingService lockingService;
    @Autowired
    private AnalyticsService analyticsService;
    @Autowired
    private CampaignService campaignService;
    @Autowired
    private SurveyService surveyService;
    @Autowired
    private QuestionService questionService;
    @Autowired
    private AuthProfileService authProfileService;
    @Autowired
    private ResponderAccessCodeRepository responderAccessCodeRepository;

    private UUID activeCampaignId;
    private UUID questionId;
    private UUID publishedSurveyId;

    @BeforeEach
    void setUp() {
        // Create question
        QuestionResponse q = questionService.create(QuestionRequest.builder()
                .text("Response test Q")
                .type(QuestionType.RATING_SCALE)
                .maxScore(new BigDecimal("10.00"))
                .build());
        questionId = q.getId();

        // Create and publish survey
        SurveyResponse survey = surveyService.create(SurveyRequest.builder()
                .title("Response Test Survey")
                .pages(List.of(SurveyPageRequest.builder()
                        .title("P1").sortOrder(1)
                        .questions(List.of(SurveyQuestionRequest.builder()
                                .questionId(q.getId()).sortOrder(1).mandatory(true).build()))
                        .build()))
                .build());
        surveyService.transitionLifecycle(survey.getId(),
                LifecycleTransitionRequest.builder().targetState("PUBLISHED").build());
        publishedSurveyId = survey.getId();

        // Create and activate campaign
        CampaignResponse campaign = campaignService.create(CampaignRequest.builder()
                .name("Response Campaign")
                .surveyId(survey.getId())
                .build());
        campaign = campaignService.activate(campaign.getId());
        activeCampaignId = campaign.getId();
    }

    @Test
    void shouldSubmitAndAutoLockResponse() {
        SurveyResponseResponse response = submitTestResponse();

        assertThat(response.getId()).isNotNull();
        assertThat(response.getStatus()).isEqualTo(ResponseStatus.LOCKED);
        assertThat(response.getLockedAt()).isNotNull();
        assertThat(response.getAnswers()).hasSize(1);
    }

    @Test
    void shouldRejectSubmissionToDraftCampaign() {
        // Create a new question, survey, and campaign but don't activate
        QuestionResponse q = questionService.create(QuestionRequest.builder()
                .text("Draft campaign Q")
                .type(QuestionType.SINGLE_CHOICE)
                .maxScore(new BigDecimal("1.00"))
                .build());
        SurveyResponse survey = surveyService.create(SurveyRequest.builder()
                .title("Draft Survey")
                .pages(List.of(SurveyPageRequest.builder()
                        .title("P1").sortOrder(1)
                        .questions(List.of(SurveyQuestionRequest.builder()
                                .questionId(q.getId()).sortOrder(1).build()))
                        .build()))
                .build());
        CampaignResponse draftCampaign = campaignService.create(CampaignRequest.builder()
                .name("Draft Campaign")
                .surveyId(survey.getId())
                .build());

        ResponseSubmissionRequest request = ResponseSubmissionRequest.builder()
                .campaignId(draftCampaign.getId())
                .answers(List.of())
                .build();

        assertThatThrownBy(() -> responseService.submit(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("not active");
    }

    @Test
    void shouldReopenLockedResponseWithReason() {
        SurveyResponseResponse response = submitTestResponse();

        SurveyResponseResponse reopened = lockingService.reopen(response.getId(),
                ReopenRequest.builder()
                        .reason("Respondent made a data entry error")
                        .reopenWindowMinutes(30)
                        .build());

        assertThat(reopened.getStatus()).isEqualTo(ResponseStatus.REOPENED);
        assertThat(reopened.getLockedAt()).isNull();
    }

    @Test
    void shouldRejectReopenOnNonLockedResponse() {
        SurveyResponseResponse response = submitTestResponse();

        // Reopen first
        lockingService.reopen(response.getId(),
                ReopenRequest.builder().reason("First reopen").build());

        // Try to reopen again — should fail because it's now REOPENED, not LOCKED
        assertThatThrownBy(() -> lockingService.reopen(response.getId(),
                ReopenRequest.builder().reason("Second reopen").build()))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("locked");
    }

    @Test
    void shouldRelockAfterReopen() {
        SurveyResponseResponse response = submitTestResponse();
        lockingService.reopen(response.getId(),
                ReopenRequest.builder().reason("Fix error").build());

        SurveyResponseResponse relocked = lockingService.lock(response.getId());
        assertThat(relocked.getStatus()).isEqualTo(ResponseStatus.LOCKED);
    }

    @Test
    void shouldComputeAnalytics() {
        submitTestResponse();
        submitTestResponse();

        CampaignAnalytics analytics = analyticsService.getAnalytics(activeCampaignId);

        assertThat(analytics.getTotalResponses()).isGreaterThanOrEqualTo(2);
        assertThat(analytics.getLockedCount()).isGreaterThanOrEqualTo(2);
        assertThat(analytics.getCompletionRate()).isGreaterThan(BigDecimal.ZERO);
    }

    @Test
    void shouldListResponsesByCampaign() {
        submitTestResponse();
        List<SurveyResponseResponse> responses = responseService.getByCampaignId(activeCampaignId);
        assertThat(responses).isNotEmpty();
    }

    @Test
    void shouldRejectAnswerForQuestionNotInCampaignSnapshot() {
        QuestionResponse externalQuestion = questionService.create(QuestionRequest.builder()
                .text("External question")
                .type(QuestionType.RATING_SCALE)
                .maxScore(new BigDecimal("10.00"))
                .build());

        assertThatThrownBy(() -> responseService.submit(ResponseSubmissionRequest.builder()
                .campaignId(activeCampaignId)
                .respondentIdentifier("foreign-question@example.com")
                .answers(List.of(ResponseSubmissionRequest.AnswerRequest.builder()
                        .questionId(externalQuestion.getId())
                        .value("8")
                        .build()))
                .build()))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("not part of the campaign survey");
    }

    @Test
    void shouldRejectWhenMandatoryAnswerMissing() {
        assertThatThrownBy(() -> responseService.submit(ResponseSubmissionRequest.builder()
                .campaignId(activeCampaignId)
                .respondentIdentifier("missing-mandatory@example.com")
                .answers(List.of())
                .build()))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Missing mandatory answers");
    }

    @Test
    void shouldRejectRatingScaleAnswerOutsideConfiguredRange() {
        QuestionResponse ratingQuestion = questionService.create(QuestionRequest.builder()
                .text("Rate from 1 to 5")
                .type(QuestionType.RATING_SCALE)
                .maxScore(new BigDecimal("10.00"))
                .build());
        UUID campaignId = createActivePublicCampaignWithSingleQuestion(
                ratingQuestion.getId(),
                true,
                "{\"min\":1,\"max\":5,\"step\":1}");

        assertThatThrownBy(() -> responseService.submit(ResponseSubmissionRequest.builder()
                .campaignId(campaignId)
                .respondentIdentifier("out-of-range@example.com")
                .answers(List.of(ResponseSubmissionRequest.AnswerRequest.builder()
                        .questionId(ratingQuestion.getId())
                        .value("7")
                        .build()))
                .build()))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("out of range");
    }

    @Test
    void shouldComputeScoreServerSideAndIgnoreClientScoreForRatingScale() {
        QuestionResponse ratingQuestion = questionService.create(QuestionRequest.builder()
                .text("Server score")
                .type(QuestionType.RATING_SCALE)
                .maxScore(new BigDecimal("10.00"))
                .build());
        UUID campaignId = createActivePublicCampaignWithSingleQuestion(
                ratingQuestion.getId(),
                true,
                "{\"min\":1,\"max\":5,\"step\":1}");

        SurveyResponseResponse response = responseService.submit(ResponseSubmissionRequest.builder()
                .campaignId(campaignId)
                .respondentIdentifier("score-calc@example.com")
                .answers(List.of(ResponseSubmissionRequest.AnswerRequest.builder()
                        .questionId(ratingQuestion.getId())
                        .value("5")
                        .score(new BigDecimal("0.01"))
                        .build()))
                .build());

        assertThat(response.getAnswers()).hasSize(1);
        assertThat(response.getAnswers().get(0).getScore()).isEqualByComparingTo(new BigDecimal("10.00"));
    }

    @Test
    void shouldRejectDuplicateAnswersForSameQuestion() {
        assertThatThrownBy(() -> responseService.submit(ResponseSubmissionRequest.builder()
                .campaignId(activeCampaignId)
                .respondentIdentifier("duplicate-answer@example.com")
                .answers(List.of(
                        ResponseSubmissionRequest.AnswerRequest.builder()
                                .questionId(questionId)
                                .value("8")
                                .build(),
                        ResponseSubmissionRequest.AnswerRequest.builder()
                                .questionId(questionId)
                                .value("9")
                                .build()))
                .build()))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Duplicate answer submitted");
    }

    @Test
    void shouldRejectWhenQuestionVersionIdDoesNotMatchSnapshot() {
        assertThatThrownBy(() -> responseService.submit(ResponseSubmissionRequest.builder()
                .campaignId(activeCampaignId)
                .respondentIdentifier("version-mismatch@example.com")
                .answers(List.of(ResponseSubmissionRequest.AnswerRequest.builder()
                        .questionId(questionId)
                        .questionVersionId(UUID.randomUUID())
                        .value("8")
                        .build()))
                .build()))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("questionVersionId mismatch");
    }

    @Test
    void shouldComputeScoreServerSideForSingleChoice() {
        QuestionResponse singleChoiceQuestion = questionService.create(QuestionRequest.builder()
                .text("Single choice score")
                .type(QuestionType.SINGLE_CHOICE)
                .maxScore(new BigDecimal("5.00"))
                .build());
        UUID campaignId = createActivePublicCampaignWithSingleQuestion(
                singleChoiceQuestion.getId(),
                true,
                "{\"options\":[{\"value\":\"A\",\"score\":5},{\"value\":\"B\",\"score\":2}]}");

        SurveyResponseResponse response = responseService.submit(ResponseSubmissionRequest.builder()
                .campaignId(campaignId)
                .respondentIdentifier("single-choice@example.com")
                .answers(List.of(ResponseSubmissionRequest.AnswerRequest.builder()
                        .questionId(singleChoiceQuestion.getId())
                        .value("A")
                        .score(new BigDecimal("0.01"))
                        .build()))
                .build());

        assertThat(response.getAnswers()).hasSize(1);
        assertThat(response.getAnswers().get(0).getScore()).isEqualByComparingTo(new BigDecimal("5.00"));
        assertThat(response.getAnswers().get(0).getValue()).isEqualTo("A");
    }

    @Test
    void shouldRejectSingleChoiceWhenOptionNotConfigured() {
        QuestionResponse singleChoiceQuestion = questionService.create(QuestionRequest.builder()
                .text("Single choice strict option")
                .type(QuestionType.SINGLE_CHOICE)
                .maxScore(new BigDecimal("5.00"))
                .build());
        UUID campaignId = createActivePublicCampaignWithSingleQuestion(
                singleChoiceQuestion.getId(),
                true,
                "{\"options\":[\"A\",\"B\"]}");

        assertThatThrownBy(() -> responseService.submit(ResponseSubmissionRequest.builder()
                .campaignId(campaignId)
                .respondentIdentifier("single-choice-invalid@example.com")
                .answers(List.of(ResponseSubmissionRequest.AnswerRequest.builder()
                        .questionId(singleChoiceQuestion.getId())
                        .value("C")
                        .build()))
                .build()))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("one configured option");
    }

    @Test
    void shouldComputeAndCapScoreForMultipleChoice() {
        QuestionResponse multipleChoiceQuestion = questionService.create(QuestionRequest.builder()
                .text("Multiple choice score")
                .type(QuestionType.MULTIPLE_CHOICE)
                .maxScore(new BigDecimal("10.00"))
                .build());
        UUID campaignId = createActivePublicCampaignWithSingleQuestion(
                multipleChoiceQuestion.getId(),
                true,
                "{\"options\":[{\"value\":\"x\",\"score\":7},{\"value\":\"y\",\"score\":6},{\"value\":\"z\",\"score\":1}],\"minSelections\":1,\"maxSelections\":3}");

        SurveyResponseResponse response = responseService.submit(ResponseSubmissionRequest.builder()
                .campaignId(campaignId)
                .respondentIdentifier("multiple-choice@example.com")
                .answers(List.of(ResponseSubmissionRequest.AnswerRequest.builder()
                        .questionId(multipleChoiceQuestion.getId())
                        .value("[\"x\",\"y\"]")
                        .score(new BigDecimal("0.01"))
                        .build()))
                .build());

        assertThat(response.getAnswers()).hasSize(1);
        assertThat(response.getAnswers().get(0).getScore()).isEqualByComparingTo(new BigDecimal("10.00"));
        assertThat(response.getAnswers().get(0).getValue()).isEqualTo("[\"x\",\"y\"]");
    }

    @Test
    void shouldRejectMultipleChoiceWithDuplicateSelections() {
        QuestionResponse multipleChoiceQuestion = questionService.create(QuestionRequest.builder()
                .text("Multiple choice duplicate")
                .type(QuestionType.MULTIPLE_CHOICE)
                .maxScore(new BigDecimal("10.00"))
                .build());
        UUID campaignId = createActivePublicCampaignWithSingleQuestion(
                multipleChoiceQuestion.getId(),
                true,
                "{\"options\":[\"x\",\"y\",\"z\"],\"minSelections\":1,\"maxSelections\":3}");

        assertThatThrownBy(() -> responseService.submit(ResponseSubmissionRequest.builder()
                .campaignId(campaignId)
                .respondentIdentifier("multiple-choice-duplicate@example.com")
                .answers(List.of(ResponseSubmissionRequest.AnswerRequest.builder()
                        .questionId(multipleChoiceQuestion.getId())
                        .value("[\"x\",\"x\"]")
                        .build()))
                .build()))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("duplicate values");
    }

    @Test
    void shouldComputeRankScoreUsingCorrectOrder() {
        QuestionResponse rankQuestion = questionService.create(QuestionRequest.builder()
                .text("Rank correct order")
                .type(QuestionType.RANK)
                .maxScore(new BigDecimal("10.00"))
                .build());
        UUID campaignId = createActivePublicCampaignWithSingleQuestion(
                rankQuestion.getId(),
                true,
                "{\"options\":[\"a\",\"b\",\"c\"],\"correctOrder\":[\"a\",\"b\",\"c\"]}");

        SurveyResponseResponse response = responseService.submit(ResponseSubmissionRequest.builder()
                .campaignId(campaignId)
                .respondentIdentifier("rank@example.com")
                .answers(List.of(ResponseSubmissionRequest.AnswerRequest.builder()
                        .questionId(rankQuestion.getId())
                        .value("[\"a\",\"c\",\"b\"]")
                        .score(new BigDecimal("10.00"))
                        .build()))
                .build());

        assertThat(response.getAnswers()).hasSize(1);
        assertThat(response.getAnswers().get(0).getScore()).isEqualByComparingTo(new BigDecimal("3.33"));
        assertThat(response.getAnswers().get(0).getValue()).isEqualTo("[\"a\",\"c\",\"b\"]");
    }

    @Test
    void shouldRejectPrivateCampaignSubmissionWithoutToken() {
        String tenantId = "tenant-private-no-token-" + UUID.randomUUID();
        CampaignQuestion privateCampaign = createActivePrivateCampaign(tenantId);

        assertThatThrownBy(() -> responseService.submit(ResponseSubmissionRequest.builder()
                .campaignId(privateCampaign.campaignId())
                .respondentIdentifier("private-user@example.com")
                .answers(List.of())
                .build()))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Responder token or access code is required");
    }

    @Test
    void shouldRejectPrivateCampaignSubmissionWithInvalidToken() {
        String tenantId = "tenant-private-invalid-token-" + UUID.randomUUID();
        CampaignQuestion privateCampaign = createActivePrivateCampaign(tenantId);
        authProfileService.create(AuthProfileRequest.builder()
                .tenantId(tenantId)
                .authMode(AuthenticationMode.SIGNED_LAUNCH_TOKEN)
                .signingSecret("private-secret-invalid")
                .build());

        assertThatThrownBy(() -> responseService.submit(ResponseSubmissionRequest.builder()
                .campaignId(privateCampaign.campaignId())
                .respondentIdentifier("private-user@example.com")
                .responderToken("bad.token")
                .answers(List.of())
                .build()))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Responder authentication failed");
    }

    @Test
    void shouldAcceptPrivateCampaignSubmissionWithValidToken() throws Exception {
        String tenantId = "tenant-private-valid-token-" + UUID.randomUUID();
        String secret = "private-valid-secret-123456789";
        CampaignQuestion privateCampaign = createActivePrivateCampaign(tenantId);
        authProfileService.create(AuthProfileRequest.builder()
                .tenantId(tenantId)
                .authMode(AuthenticationMode.SIGNED_LAUNCH_TOKEN)
                .signingSecret(secret)
                .claimMappings(List.of(
                        AuthProfileRequest.ClaimMappingRequest.builder()
                                .externalClaim("sub")
                                .internalField("respondentId")
                                .required(true)
                                .build(),
                        AuthProfileRequest.ClaimMappingRequest.builder()
                                .externalClaim("email")
                                .internalField("email")
                                .required(true)
                                .build()))
                .build());

        String payload = "{\"sub\":\"responder-1\",\"email\":\"private-user@example.com\",\"jti\":\"%s\",\"iat\":%d,\"exp\":%d}"
                .formatted(
                        UUID.randomUUID(),
                        Instant.now().getEpochSecond(),
                        Instant.now().plusSeconds(3600).getEpochSecond());
        String token = createSignedToken(secret, payload);

        SurveyResponseResponse response = responseService.submit(ResponseSubmissionRequest.builder()
                .campaignId(privateCampaign.campaignId())
                .responderToken(token)
                .answers(List.of(ResponseSubmissionRequest.AnswerRequest.builder()
                        .questionId(privateCampaign.questionId())
                        .value("9")
                        .score(new BigDecimal("9.00"))
                        .build()))
                .build());

        assertThat(response.getStatus()).isEqualTo(ResponseStatus.LOCKED);
        assertThat(response.getRespondentIdentifier()).isEqualTo("private-user@example.com");
    }

    @Test
    void shouldAcceptPrivateCampaignSubmissionWithAccessCode() {
        String tenantId = "tenant-private-access-code-" + UUID.randomUUID();
        CampaignQuestion privateCampaign = createActivePrivateCampaign(tenantId);
        String accessCode = "ac-" + UUID.randomUUID().toString().replace("-", "");

        responderAccessCodeRepository.save(ResponderAccessCode.builder()
                .accessCode(accessCode)
                .tenantId(tenantId)
                .campaignId(privateCampaign.campaignId())
                .respondentId("responder-access-1")
                .email("private-access@example.com")
                .expiresAt(Instant.now().plusSeconds(300))
                .createdAt(Instant.now())
                .build());

        SurveyResponseResponse response = responseService.submit(ResponseSubmissionRequest.builder()
                .campaignId(privateCampaign.campaignId())
                .responderAccessCode(accessCode)
                .answers(List.of(ResponseSubmissionRequest.AnswerRequest.builder()
                        .questionId(privateCampaign.questionId())
                        .value("10")
                        .score(new BigDecimal("10.00"))
                        .build()))
                .build());

        assertThat(response.getStatus()).isEqualTo(ResponseStatus.LOCKED);
        assertThat(response.getRespondentIdentifier()).isEqualTo("private-access@example.com");
    }

    private SurveyResponseResponse submitTestResponse() {
        return responseService.submit(ResponseSubmissionRequest.builder()
                .campaignId(activeCampaignId)
                .respondentIdentifier("test-" + UUID.randomUUID() + "@example.com")
                .respondentIp("192.168.1." + (int) (Math.random() * 254 + 1))
                .answers(List.of(ResponseSubmissionRequest.AnswerRequest.builder()
                        .questionId(questionId)
                        .value("8")
                        .score(new BigDecimal("8.00"))
                        .build()))
                .build());
    }

    private CampaignQuestion createActivePrivateCampaign(String tenantId) {
        TenantContext.set(new TenantContext.TenantInfo(
                tenantId,
                "admin-user",
                "admin@example.com",
                Set.of("ADMIN")));
        try {
            QuestionResponse tenantQuestion = questionService.create(QuestionRequest.builder()
                    .text("Private tenant question")
                    .type(QuestionType.RATING_SCALE)
                    .maxScore(new BigDecimal("10.00"))
                    .build());

            SurveyResponse tenantSurvey = surveyService.create(SurveyRequest.builder()
                    .title("Private Tenant Survey")
                    .pages(List.of(SurveyPageRequest.builder()
                            .title("P1").sortOrder(1)
                            .questions(List.of(SurveyQuestionRequest.builder()
                                    .questionId(tenantQuestion.getId()).sortOrder(1).mandatory(true).build()))
                            .build()))
                    .build());
            surveyService.transitionLifecycle(tenantSurvey.getId(),
                    LifecycleTransitionRequest.builder().targetState("PUBLISHED").build());

            CampaignResponse campaign = campaignService.create(CampaignRequest.builder()
                    .name("Private Campaign " + tenantId)
                    .surveyId(tenantSurvey.getId())
                    .authMode(AuthMode.PRIVATE)
                    .build());
            UUID campaignId = campaignService.activate(campaign.getId()).getId();
            return new CampaignQuestion(campaignId, tenantQuestion.getId());
        } finally {
            TenantContext.clear();
        }
    }

    private UUID createActivePublicCampaignWithSingleQuestion(UUID questionId, boolean mandatory, String answerConfig) {
        SurveyResponse survey = surveyService.create(SurveyRequest.builder()
                .title("Public validation campaign")
                .pages(List.of(SurveyPageRequest.builder()
                        .title("Page 1").sortOrder(1)
                        .questions(List.of(SurveyQuestionRequest.builder()
                                .questionId(questionId)
                                .sortOrder(1)
                                .mandatory(mandatory)
                                .answerConfig(answerConfig)
                                .build()))
                        .build()))
                .build());
        surveyService.transitionLifecycle(survey.getId(),
                LifecycleTransitionRequest.builder().targetState("PUBLISHED").build());

        CampaignResponse campaign = campaignService.create(CampaignRequest.builder()
                .name("Public Validation Campaign " + UUID.randomUUID())
                .surveyId(survey.getId())
                .authMode(AuthMode.PUBLIC)
                .build());
        return campaignService.activate(campaign.getId()).getId();
    }

    private String createSignedToken(String secret, String payloadJson) throws Exception {
        String headerJson = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
        String headerBase64 = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(headerJson.getBytes(StandardCharsets.UTF_8));
        String payloadBase64 = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(payloadJson.getBytes(StandardCharsets.UTF_8));
        String signingInput = headerBase64 + "." + payloadBase64;
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        String signature = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(mac.doFinal(signingInput.getBytes(StandardCharsets.UTF_8)));
        return signingInput + "." + signature;
    }

    private record CampaignQuestion(UUID campaignId, UUID questionId) {
    }
}
