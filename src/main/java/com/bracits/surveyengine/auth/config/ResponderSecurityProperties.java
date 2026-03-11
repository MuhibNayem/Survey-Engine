package com.bracits.surveyengine.auth.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@ConfigurationProperties(prefix = "survey-engine.auth.responder")
@Validated
@Getter
@Setter
public class ResponderSecurityProperties {

    private Cookie cookie = new Cookie();
    private Csrf csrf = new Csrf();
    private Exchange exchange = new Exchange();

    @PostConstruct
    void validate() {
        if ("None".equalsIgnoreCase(cookie.getSameSite()) && !cookie.isSecure()) {
            throw new IllegalStateException(
                    "survey-engine.auth.responder.cookie.same-site=None requires survey-engine.auth.responder.cookie.secure=true");
        }
        if (csrf.isEnabled() && (csrf.getSecret() == null || csrf.getSecret().isBlank())) {
            throw new IllegalStateException(
                    "survey-engine.auth.responder.csrf.secret is required when responder CSRF is enabled");
        }

        if (exchange.getAllowedOrigins() == null) {
            exchange.setAllowedOrigins(new ArrayList<>());
            return;
        }

        List<String> normalized = exchange.getAllowedOrigins().stream()
                .filter(value -> value != null && !value.isBlank())
                .map(String::trim)
                .map(value -> value.toLowerCase(Locale.ROOT))
                .distinct()
                .collect(Collectors.toList());
        exchange.setAllowedOrigins(normalized);

        if (exchange.isEnforceOriginAllowlist() && exchange.getAllowedOrigins().isEmpty()) {
            throw new IllegalStateException(
                    "survey-engine.auth.responder.exchange.enforce-origin-allowlist=true requires at least one allowed origin");
        }
        if (exchange.getRateLimitPerMinute() < 1) {
            throw new IllegalStateException(
                    "survey-engine.auth.responder.exchange.rate-limit-per-minute must be at least 1");
        }
    }

    @Getter
    @Setter
    public static class Cookie {
        private boolean secure = true;
        private String sameSite = "Lax";
    }

    @Getter
    @Setter
    public static class Csrf {
        private boolean enabled = true;
        private String cookieName = "responder_xsrf";
        private String headerName = "X-Responder-Csrf";
        private String secret = "change-me-in-production";
    }

    @Getter
    @Setter
    public static class Exchange {
        private boolean enforceOriginAllowlist = false;
        private boolean allowMissingOrigin = false;
        private List<String> allowedOrigins = new ArrayList<>();
        private boolean rateLimitEnabled = true;
        private int rateLimitPerMinute = 30;
    }
}
