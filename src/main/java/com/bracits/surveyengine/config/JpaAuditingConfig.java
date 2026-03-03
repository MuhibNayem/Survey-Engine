package com.bracits.surveyengine.config;

import com.bracits.surveyengine.admin.context.TenantContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

/**
 * Enables JPA auditing and resolves the current auditor from TenantContext.
 * Falls back to "system" when no authenticated user is present.
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class JpaAuditingConfig {

    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> {
            String userId = TenantContext.getUserId();
            if (userId != null) {
                String tenantId = TenantContext.getTenantId();
                return Optional.of(tenantId + "/" + userId);
            }
            return Optional.of("system");
        };
    }
}
