package com.bracits.surveyengine.survey;

import com.bracits.surveyengine.TestcontainersConfiguration;
import com.bracits.surveyengine.common.exception.BusinessException;
import com.bracits.surveyengine.questionbank.dto.QuestionRequest;
import com.bracits.surveyengine.questionbank.dto.QuestionResponse;
import com.bracits.surveyengine.questionbank.entity.QuestionType;
import com.bracits.surveyengine.questionbank.service.QuestionService;
import com.bracits.surveyengine.survey.dto.*;
import com.bracits.surveyengine.survey.entity.SurveyLifecycleState;
import com.bracits.surveyengine.survey.entity.SurveySnapshot;
import com.bracits.surveyengine.survey.service.SurveyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class SurveyLifecycleIntegrationTest {

    @Autowired
    private SurveyService surveyService;

    @Autowired
    private QuestionService questionService;

    private QuestionResponse testQuestion;

    @BeforeEach
    void setUp() {
        testQuestion = questionService.create(QuestionRequest.builder()
                .text("Test question for survey")
                .type(QuestionType.RATING_SCALE)
                .maxScore(new BigDecimal("10.00"))
                .build());
    }

    @Test
    void shouldCreateSurveyInDraftState() {
        SurveyResponse survey = createTestSurvey();

        assertThat(survey.getId()).isNotNull();
        assertThat(survey.getTitle()).isEqualTo("Test Survey");
        assertThat(survey.getLifecycleState()).isEqualTo(SurveyLifecycleState.DRAFT);
        assertThat(survey.getPages()).hasSize(1);
        assertThat(survey.getPages().get(0).getQuestions()).hasSize(1);
    }

    @Test
    void shouldTransitionDraftToPublishedAndCreateSnapshot() {
        SurveyResponse survey = createTestSurvey();

        SurveyResponse published = surveyService.transitionLifecycle(survey.getId(),
                LifecycleTransitionRequest.builder().targetState("PUBLISHED").build());

        assertThat(published.getLifecycleState()).isEqualTo(SurveyLifecycleState.PUBLISHED);

        // Verify snapshot was created
        SurveySnapshot snapshot = surveyService.getLatestSnapshot(survey.getId());
        assertThat(snapshot).isNotNull();
        assertThat(snapshot.getSnapshotData()).contains("Test Survey");
        assertThat(snapshot.getVersionNumber()).isEqualTo(1);
    }

    @Test
    void shouldFollowFullLifecycle() {
        SurveyResponse survey = createTestSurvey();

        // Draft → Published
        survey = surveyService.transitionLifecycle(survey.getId(),
                LifecycleTransitionRequest.builder().targetState("PUBLISHED").build());
        assertThat(survey.getLifecycleState()).isEqualTo(SurveyLifecycleState.PUBLISHED);

        // Published → Closed
        survey = surveyService.transitionLifecycle(survey.getId(),
                LifecycleTransitionRequest.builder().targetState("CLOSED").build());
        assertThat(survey.getLifecycleState()).isEqualTo(SurveyLifecycleState.CLOSED);

        // Closed → Results Published
        survey = surveyService.transitionLifecycle(survey.getId(),
                LifecycleTransitionRequest.builder().targetState("RESULTS_PUBLISHED").build());
        assertThat(survey.getLifecycleState()).isEqualTo(SurveyLifecycleState.RESULTS_PUBLISHED);

        // Results Published → Archived
        survey = surveyService.transitionLifecycle(survey.getId(),
                LifecycleTransitionRequest.builder().targetState("ARCHIVED").build());
        assertThat(survey.getLifecycleState()).isEqualTo(SurveyLifecycleState.ARCHIVED);
    }

    @Test
    void shouldRejectInvalidTransition() {
        SurveyResponse survey = createTestSurvey();

        // Draft → CLOSED (invalid, must go through Published first)
        assertThatThrownBy(() -> surveyService.transitionLifecycle(survey.getId(),
                LifecycleTransitionRequest.builder().targetState("CLOSED").build()))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Cannot transition from DRAFT to CLOSED");
    }

    @Test
    void shouldRejectReopenWithoutReason() {
        SurveyResponse survey = createTestSurvey();
        surveyService.transitionLifecycle(survey.getId(),
                LifecycleTransitionRequest.builder().targetState("PUBLISHED").build());
        surveyService.transitionLifecycle(survey.getId(),
                LifecycleTransitionRequest.builder().targetState("CLOSED").build());

        // Reopen without reason
        assertThatThrownBy(() -> surveyService.transitionLifecycle(survey.getId(),
                LifecycleTransitionRequest.builder().targetState("PUBLISHED").build()))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Reason is required");
    }

    @Test
    void shouldAllowReopenWithReason() {
        SurveyResponse survey = createTestSurvey();
        surveyService.transitionLifecycle(survey.getId(),
                LifecycleTransitionRequest.builder().targetState("PUBLISHED").build());
        surveyService.transitionLifecycle(survey.getId(),
                LifecycleTransitionRequest.builder().targetState("CLOSED").build());

        // Reopen with reason
        SurveyResponse reopened = surveyService.transitionLifecycle(survey.getId(),
                LifecycleTransitionRequest.builder()
                        .targetState("PUBLISHED")
                        .reason("Need to collect more responses")
                        .build());

        assertThat(reopened.getLifecycleState()).isEqualTo(SurveyLifecycleState.PUBLISHED);
    }

    @Test
    void shouldRejectModificationAfterPublish() {
        SurveyResponse survey = createTestSurvey();
        surveyService.transitionLifecycle(survey.getId(),
                LifecycleTransitionRequest.builder().targetState("PUBLISHED").build());

        // Attempt to modify published survey
        assertThatThrownBy(() -> surveyService.update(survey.getId(), SurveyRequest.builder()
                .title("Modified Title")
                .build()))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("cannot be modified after publishing");
    }

    @Test
    void shouldAllowModificationInDraftState() {
        SurveyResponse survey = createTestSurvey();

        SurveyResponse updated = surveyService.update(survey.getId(),
                SurveyRequest.builder()
                        .title("Updated Title")
                        .description("Updated description")
                        .pages(List.of(SurveyPageRequest.builder()
                                .title("Updated Page")
                                .sortOrder(1)
                                .questions(List.of(SurveyQuestionRequest.builder()
                                        .questionId(testQuestion.getId())
                                        .sortOrder(1)
                                        .mandatory(true)
                                        .build()))
                                .build()))
                        .build());

        assertThat(updated.getTitle()).isEqualTo("Updated Title");
        assertThat(updated.getDescription()).isEqualTo("Updated description");
    }

    private SurveyResponse createTestSurvey() {
        return surveyService.create(SurveyRequest.builder()
                .title("Test Survey")
                .description("A test survey")
                .pages(List.of(SurveyPageRequest.builder()
                        .title("Page 1")
                        .sortOrder(1)
                        .questions(List.of(SurveyQuestionRequest.builder()
                                .questionId(testQuestion.getId())
                                .sortOrder(1)
                                .mandatory(true)
                                .build()))
                        .build()))
                .build());
    }
}
