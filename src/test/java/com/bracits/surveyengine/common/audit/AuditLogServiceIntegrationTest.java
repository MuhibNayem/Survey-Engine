package com.bracits.surveyengine.common.audit;

import com.bracits.surveyengine.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class AuditLogServiceIntegrationTest {

    @Autowired
    private AuditLogService auditLogService;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Test
    void shouldRecordAuditEntryWithBeforeAfterValues() {
        AuditLog entry = auditLogService.record(
                "Survey", "survey-123", "PUBLISH",
                "admin@test.com", "Initial publish",
                "{\"status\":\"DRAFT\"}", "{\"status\":\"PUBLISHED\"}");

        assertThat(entry.getId()).isNotNull();
        assertThat(entry.getCreatedAt()).isNotNull();
        assertThat(entry.getEntityType()).isEqualTo("Survey");
        assertThat(entry.getEntityId()).isEqualTo("survey-123");
        assertThat(entry.getAction()).isEqualTo("PUBLISH");
        assertThat(entry.getActor()).isEqualTo("admin@test.com");
        assertThat(entry.getBeforeValue()).contains("DRAFT");
        assertThat(entry.getAfterValue()).contains("PUBLISHED");
    }

    @Test
    void shouldRecordSimpleAuditEntry() {
        AuditLog entry = auditLogService.record(
                "Campaign", "campaign-456", "ACTIVATE",
                "manager@test.com", "Campaign goes live");

        assertThat(entry.getId()).isNotNull();
        assertThat(entry.getBeforeValue()).isNull();
        assertThat(entry.getAfterValue()).isNull();
    }

    @Test
    void shouldFindAuditLogsByEntity() {
        auditLogService.record("WeightProfile", "wp-1", "CREATE", "admin@test.com", null);
        auditLogService.record("WeightProfile", "wp-1", "UPDATE", "admin@test.com", "Adjust weights");

        List<AuditLog> logs = auditLogRepository
                .findByEntityTypeAndEntityIdOrderByCreatedAtDesc("WeightProfile", "wp-1");

        assertThat(logs).hasSizeGreaterThanOrEqualTo(2);
        assertThat(logs.get(0).getAction()).isEqualTo("UPDATE");
    }
}
