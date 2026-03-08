package com.bracits.surveyengine.questionbank;

import com.bracits.surveyengine.TestcontainersConfiguration;
import com.bracits.surveyengine.questionbank.dto.*;
import com.bracits.surveyengine.questionbank.entity.QuestionType;
import com.bracits.surveyengine.questionbank.service.CategoryService;
import com.bracits.surveyengine.questionbank.service.QuestionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class QuestionBankIntegrationTest {

        @Autowired
        private QuestionService questionService;

        @Autowired
        private CategoryService categoryService;

        @Test
        void shouldCreateQuestionWithInitialVersion() {
                QuestionRequest request = QuestionRequest.builder()
                                .text("How satisfied are you?")
                                .type(QuestionType.RATING_SCALE)
                                .maxScore(new BigDecimal("10.00"))
                                .build();

                QuestionResponse response = questionService.create(request);

                assertThat(response.getId()).isNotNull();
                assertThat(response.getText()).isEqualTo("How satisfied are you?");
                assertThat(response.getType()).isEqualTo(QuestionType.RATING_SCALE);
                assertThat(response.getMaxScore()).isEqualByComparingTo(new BigDecimal("10.00"));
                assertThat(response.getCurrentVersion()).isEqualTo(1);
                assertThat(response.isActive()).isTrue();
        }

        @Test
        void shouldCreateNewVersionOnUpdate() {
                QuestionRequest createReq = QuestionRequest.builder()
                                .text("Rate your experience")
                                .type(QuestionType.RANK)
                                .maxScore(new BigDecimal("5.00"))
                                .build();
                QuestionResponse created = questionService.create(createReq);

                QuestionRequest updateReq = QuestionRequest.builder()
                                .text("Rate your overall experience")
                                .type(QuestionType.RANK)
                                .maxScore(new BigDecimal("5.00"))
                                .build();
                QuestionResponse updated = questionService.update(created.getId(), updateReq);

                assertThat(updated.getCurrentVersion()).isEqualTo(1);
                assertThat(updated.getText()).isEqualTo("Rate your overall experience");
        }

        @Test
        void shouldSoftDeleteQuestion() {
                QuestionResponse q = questionService.create(QuestionRequest.builder()
                                .text("To be deleted")
                                .type(QuestionType.SINGLE_CHOICE)
                                .maxScore(new BigDecimal("1.00"))
                                .build());

                questionService.deactivate(q.getId());

                QuestionResponse deactivated = questionService.getById(q.getId());
                assertThat(deactivated.isActive()).isFalse();
        }

        @Test
        void shouldCreateCategoryWithQuestionMappings() {
                // Create questions first
                QuestionResponse q1 = questionService.create(QuestionRequest.builder()
                                .text("Category Q1")
                                .type(QuestionType.RATING_SCALE)
                                .maxScore(new BigDecimal("10.00"))
                                .build());
                QuestionResponse q2 = questionService.create(QuestionRequest.builder()
                                .text("Category Q2")
                                .type(QuestionType.MULTIPLE_CHOICE)
                                .maxScore(new BigDecimal("5.00"))
                                .build());

                CategoryRequest catReq = CategoryRequest.builder()
                                .name("Customer Satisfaction")
                                .description("Measures overall customer satisfaction")
                                .questionMappings(List.of(
                                                CategoryQuestionMappingRequest.builder()
                                                                .questionId(q1.getId())
                                                                .sortOrder(1)
                                                                .weight(new BigDecimal("60.00"))
                                                                .build(),
                                                CategoryQuestionMappingRequest.builder()
                                                                .questionId(q2.getId())
                                                                .sortOrder(2)
                                                                .weight(new BigDecimal("40.00"))
                                                                .build()))
                                .build();

                CategoryResponse catResponse = categoryService.create(catReq);

                assertThat(catResponse.getId()).isNotNull();
                assertThat(catResponse.getName()).isEqualTo("Customer Satisfaction");
                assertThat(catResponse.getCurrentVersion()).isEqualTo(1);
                assertThat(catResponse.getQuestionMappings()).hasSize(2);
                assertThat(catResponse.getQuestionMappings().get(0).getQuestionId()).isEqualTo(q1.getId());
        }

        @Test
        void shouldCreateNewCategoryVersionOnUpdate() {
                QuestionResponse q = questionService.create(QuestionRequest.builder()
                                .text("Version test Q")
                                .type(QuestionType.RANK)
                                .maxScore(new BigDecimal("5.00"))
                                .build());

                CategoryResponse created = categoryService.create(CategoryRequest.builder()
                                .name("Version Test Category")
                                .questionMappings(List.of(
                                                CategoryQuestionMappingRequest.builder()
                                                                .questionId(q.getId())
                                                                .sortOrder(1)
                                                                .build()))
                                .build());

                CategoryResponse updated = categoryService.update(created.getId(), CategoryRequest.builder()
                                .name("Updated Category Name")
                                .description("Now with description")
                                .questionMappings(List.of(
                                                CategoryQuestionMappingRequest.builder()
                                                                .questionId(q.getId())
                                                                .sortOrder(1)
                                                                .weight(new BigDecimal("100.00"))
                                                                .build()))
                                .build());

                assertThat(updated.getCurrentVersion()).isEqualTo(1);
                assertThat(updated.getName()).isEqualTo("Updated Category Name");
                assertThat(updated.getDescription()).isEqualTo("Now with description");
        }

        @Test
        void shouldListActiveQuestionsOnly() {
                QuestionResponse active = questionService.create(QuestionRequest.builder()
                                .text("Active question for list test")
                                .type(QuestionType.RATING_SCALE)
                                .maxScore(new BigDecimal("10.00"))
                                .build());
                QuestionResponse toDeactivate = questionService.create(QuestionRequest.builder()
                                .text("Inactive question for list test")
                                .type(QuestionType.RANK)
                                .maxScore(new BigDecimal("5.00"))
                                .build());
                questionService.deactivate(toDeactivate.getId());

                List<QuestionResponse> activeList = questionService.getAllActive(org.springframework.data.domain.PageRequest.of(0, 50)).getContent();

                assertThat(activeList).extracting(QuestionResponse::getId).contains(active.getId());
                assertThat(activeList).extracting(QuestionResponse::getId).doesNotContain(toDeactivate.getId());
        }
}
