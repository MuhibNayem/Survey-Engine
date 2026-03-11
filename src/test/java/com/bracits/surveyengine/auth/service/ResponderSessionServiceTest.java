package com.bracits.surveyengine.auth.service;

import com.bracits.surveyengine.auth.config.ResponderSecurityProperties;
import com.bracits.surveyengine.auth.dto.ResponderAccessIdentity;
import com.bracits.surveyengine.auth.repository.ResponderSessionRepository;
import com.bracits.surveyengine.campaign.repository.CampaignSettingsRepository;
import com.bracits.surveyengine.common.exception.BusinessException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.ResponseCookie;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ResponderSessionServiceTest {

    @Mock
    private ResponderSessionRepository responderSessionRepository;
    @Mock
    private CampaignSettingsRepository campaignSettingsRepository;
    @Mock
    private HttpServletRequest request;

    private ResponderSessionService service;

    @BeforeEach
    void setUp() {
        ResponderSecurityProperties properties = new ResponderSecurityProperties();
        properties.getCookie().setSameSite("None");
        properties.getCookie().setSecure(true);
        properties.getCsrf().setEnabled(true);
        properties.getCsrf().setCookieName("responder_xsrf");
        properties.getCsrf().setHeaderName("X-Responder-Csrf");
        properties.getCsrf().setSecret("test-responder-csrf-secret");

        service = new ResponderSessionService(
                responderSessionRepository,
                campaignSettingsRepository,
                properties);

        when(responderSessionRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(campaignSettingsRepository.findByCampaignId(any())).thenReturn(Optional.empty());
        when(request.getHeader("X-Forwarded-Proto")).thenReturn("https");
    }

    @Test
    void shouldCreateSessionAndCsrfCookies() {
        List<ResponseCookie> cookies = service.createSessionCookies(
                request,
                "tenant-1",
                UUID.randomUUID(),
                ResponderAccessIdentity.builder().respondentId("r-1").email("r1@example.com").build());

        assertThat(cookies).hasSize(2);
        assertThat(cookies.get(0).getName()).isEqualTo("responder_session");
        assertThat(cookies.get(1).getName()).isEqualTo("responder_xsrf");
        assertThat(cookies.get(1).isHttpOnly()).isFalse();
    }

    @Test
    void shouldAcceptValidResponderCsrfHeaderForSessionMutation() {
        List<ResponseCookie> created = service.createSessionCookies(
                request,
                "tenant-1",
                UUID.randomUUID(),
                ResponderAccessIdentity.builder().respondentId("r-2").email("r2@example.com").build());

        String sessionValue = created.get(0).getValue();
        String csrfValue = created.get(1).getValue();

        HttpServletRequest mutationRequest = org.mockito.Mockito.mock(HttpServletRequest.class);
        when(mutationRequest.getCookies()).thenReturn(new Cookie[] {
                new Cookie("responder_session", sessionValue),
                new Cookie("responder_xsrf", csrfValue)
        });
        when(mutationRequest.getHeader("X-Responder-Csrf")).thenReturn(csrfValue);

        service.enforceCsrfForSessionMutation(mutationRequest);
    }

    @Test
    void shouldRejectInvalidResponderCsrfHeaderForSessionMutation() {
        HttpServletRequest mutationRequest = org.mockito.Mockito.mock(HttpServletRequest.class);
        when(mutationRequest.getCookies()).thenReturn(new Cookie[] {
                new Cookie("responder_session", "session-token"),
                new Cookie("responder_xsrf", "bad")
        });
        when(mutationRequest.getHeader("X-Responder-Csrf")).thenReturn("bad");

        assertThatThrownBy(() -> service.enforceCsrfForSessionMutation(mutationRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Invalid responder CSRF token");
    }
}
