package com.bracits.surveyengine.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

/**
 * Enables JPA auditing and provides the current auditor for @CreatedBy
 * / @LastModifiedBy.
 * Phase 1: returns "system" as default; will integrate with SecurityContext in
 * Phase 7.
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class JpaAuditingConfig {

    @Bean
    public AuditorAware<String> auditorProvider() {
        // TODO Phase 7: resolve from SecurityContextHolder
        return () -> Optional.of("system");
    }
}
