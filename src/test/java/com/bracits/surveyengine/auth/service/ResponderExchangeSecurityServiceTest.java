package com.bracits.surveyengine.auth.service;

import com.bracits.surveyengine.auth.config.ResponderSecurityProperties;
import com.bracits.surveyengine.campaign.entity.Campaign;
import com.bracits.surveyengine.common.audit.AuditLogService;
import com.bracits.surveyengine.common.exception.BusinessException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ResponderExchangeSecurityServiceTest {

    @Mock
    private AuditLogService auditLogService;

    private ResponderSecurityProperties properties;
    private ResponderExchangeSecurityService service;

    @BeforeEach
    void setUp() {
        properties = new ResponderSecurityProperties();
        properties.getExchange().setEnforceOriginAllowlist(true);
        properties.getExchange().setAllowedOrigins(List.of("https://app.tenant.example.com"));
        properties.getExchange().setRateLimitEnabled(true);
        properties.getExchange().setRateLimitPerMinute(1);

        service = new ResponderExchangeSecurityService(
                properties,
                auditLogService,
                new ObjectMapper(),
                Optional.empty());
    }

    @Test
    void shouldAllowKnownOriginAndResolveClientIp() {
        Campaign campaign = Campaign.builder().id(UUID.randomUUID()).tenantId("tenant-1").build();
        HttpServletRequest request = org.mockito.Mockito.mock(HttpServletRequest.class);
        when(request.getHeader("Origin")).thenReturn("https://app.tenant.example.com");
        when(request.getHeader("X-Forwarded-For")).thenReturn("203.0.113.10");
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");

        var context = service.enforceAndResolveContext(request, campaign);

        assertThat(context.origin()).isEqualTo("https://app.tenant.example.com");
        assertThat(context.clientIp()).isEqualTo("203.0.113.10");
    }

    @Test
    void shouldRejectDisallowedOrigin() {
        Campaign campaign = Campaign.builder().id(UUID.randomUUID()).tenantId("tenant-1").build();
        HttpServletRequest request = org.mockito.Mockito.mock(HttpServletRequest.class);
        when(request.getHeader("Origin")).thenReturn("https://evil.example.com");

        assertThatThrownBy(() -> service.enforceAndResolveContext(request, campaign))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Origin is not allowed");
    }

    @Test
    void shouldRateLimitExchangePerMinute() {
        Campaign campaign = Campaign.builder().id(UUID.randomUUID()).tenantId("tenant-1").build();
        HttpServletRequest request = org.mockito.Mockito.mock(HttpServletRequest.class);
        when(request.getHeader("Origin")).thenReturn("https://app.tenant.example.com");
        when(request.getHeader("X-Forwarded-For")).thenReturn("198.51.100.2");
        when(request.getRemoteAddr()).thenReturn("198.51.100.2");

        service.enforceAndResolveContext(request, campaign);

        assertThatThrownBy(() -> service.enforceAndResolveContext(request, campaign))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("rate limit exceeded");
    }

    @Test
    void shouldAuditExchangeSuccessAndFailure() {
        Campaign campaign = Campaign.builder().id(UUID.randomUUID()).tenantId("tenant-a").build();
        var context = new ResponderExchangeSecurityService.ExchangeSecurityContext("https://app.tenant.example.com", "203.0.113.3");

        service.auditSuccess(campaign, context, "resp-1", "resp-1@tenant.example.com", "SIGNED_OR_SSO_TOKEN");
        service.auditFailure(campaign, context, "bad token", "SIGNED_OR_SSO_TOKEN", "ACCESS_DENIED");

        verify(auditLogService, org.mockito.Mockito.times(2)).record(
                anyString(),
                anyString(),
                anyString(),
                anyString(),
                anyString(),
                anyString(),
                any(),
                any(),
                anyString());
    }
}
