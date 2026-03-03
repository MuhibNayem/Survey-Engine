package com.bracits.surveyengine;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class SurveyEngineApplicationTests {

  @Test
  void contextLoads() {
    // Verifies the full application context starts successfully
    // including Flyway migrations, JPA, Redis, and RabbitMQ.
  }
}
