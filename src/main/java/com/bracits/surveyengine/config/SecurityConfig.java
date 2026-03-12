package com.bracits.surveyengine.config;

import com.bracits.surveyengine.auth.config.ResponderSecurityProperties;
import com.bracits.surveyengine.admin.filter.JwtAuthenticationFilter;
import com.bracits.surveyengine.common.exception.CustomAuthenticationEntryPoint;
import com.bracits.surveyengine.subscription.service.SubscriptionEnforcementFilter;
import org.springframework.security.config.Customizer;
import org.springframework.http.HttpHeaders;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.ArrayList;
import java.util.List;

/**
 * Security configuration with engine-owned JWT authentication.
 * <p>
 * Admin endpoints require a Bearer token issued by the engine
 * (/api/v1/admin/auth/login).
 * Respondent endpoints remain public (respondent auth is external, configured
 * per campaign).
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
        private static final RequestMatcher BEARER_TOKEN_REQUEST_MATCHER = request -> {
                String header = request.getHeader(HttpHeaders.AUTHORIZATION);
                return header != null && header.startsWith("Bearer ");
        };

        private final JwtAuthenticationFilter jwtAuthenticationFilter;
        private final SubscriptionEnforcementFilter subscriptionEnforcementFilter;
        private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
        private final com.bracits.surveyengine.admin.filter.CsrfCookieFilter csrfCookieFilter;
        private final ResponderSecurityProperties responderSecurityProperties;
        private final CorsConfigurationSource corsConfigurationSource;

        public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                              SubscriptionEnforcementFilter subscriptionEnforcementFilter,
                              CustomAuthenticationEntryPoint customAuthenticationEntryPoint,
                              com.bracits.surveyengine.admin.filter.CsrfCookieFilter csrfCookieFilter,
                              ResponderSecurityProperties responderSecurityProperties,
                              CorsConfigurationSource corsConfigurationSource) {
                this.jwtAuthenticationFilter = jwtAuthenticationFilter;
                this.subscriptionEnforcementFilter = subscriptionEnforcementFilter;
                this.customAuthenticationEntryPoint = customAuthenticationEntryPoint;
                this.csrfCookieFilter = csrfCookieFilter;
                this.responderSecurityProperties = responderSecurityProperties;
                this.corsConfigurationSource = corsConfigurationSource;
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http.cors(cors -> cors.configurationSource(corsConfigurationSource));
                
                CookieCsrfTokenRepository csrfRepo = CookieCsrfTokenRepository.withHttpOnlyFalse();
                csrfRepo.setCookiePath("/");

                http
                                .csrf(csrf -> csrf
                                                .csrfTokenRepository(csrfRepo)
                                                // SPA cookie/header mode: accept plain token submitted via
                                                // X-XSRF-TOKEN header
                                                .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
                                                .ignoringRequestMatchers(
                                                                // Token-mode and public endpoints do not use browser
                                                                // cookies for auth
                                                                BEARER_TOKEN_REQUEST_MATCHER)
                                                .ignoringRequestMatchers(
                                                                "/api/v1/admin/auth/**",
                                                                "/api/v1/public/**",
                                                                "/api/v1/auth/validate/**",
                                                                "/api/v1/auth/respondent/oidc/**",
                                                                "/api/v1/responses",
                                                                "/actuator/**",
                                                                "/swagger-ui/**",
                                                                "/v3/api-docs/**",
                                                                "/openapi.yaml",
                                                                "/openapi.yml",
                                                                "/openapi.ymal"))
                                .headers(headers -> headers
                                                .frameOptions(frame -> frame.disable())
                                                .referrerPolicy(referrer -> referrer.policy(
                                                                org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter.ReferrerPolicy.SAME_ORIGIN))
                                                .httpStrictTransportSecurity(hsts -> hsts.includeSubDomains(true).preload(true).maxAgeInSeconds(31536000))
                                                .contentSecurityPolicy(csp -> csp.policyDirectives(frameAncestorsDirective()))
                                                .xssProtection(Customizer.withDefaults())
                                                .contentTypeOptions(Customizer.withDefaults()))
                                .exceptionHandling(exception -> exception
                                                .authenticationEntryPoint(customAuthenticationEntryPoint))
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .authorizeHttpRequests(auth -> auth
                                                // Authenticated — /me returns current user info (must be before
                                                // wildcard)
                                                .requestMatchers("/api/v1/admin/auth/me").authenticated()
                                                // Public — admin registration, login, logout, refresh
                                                .requestMatchers("/api/v1/admin/auth/**").permitAll()
                                                .requestMatchers("/api/v1/admin/subscriptions/**").authenticated()
                                                .requestMatchers("/swagger-ui.html", "/swagger-ui/**").permitAll()
                                                .requestMatchers("/v3/api-docs/**").permitAll()
                                                .requestMatchers("/openapi.yaml", "/openapi.yml", "/openapi.ymal")
                                                .permitAll()
                                                .requestMatchers("/error", "/error/**").permitAll()
                                                // Public — health checks
                                                .requestMatchers("/actuator/**").permitAll()
                                                .requestMatchers(HttpMethod.GET, "/api/v1/admin/plans").permitAll()
                                                .requestMatchers(HttpMethod.PUT, "/api/v1/admin/plans/**")
                                                .hasRole("SUPER_ADMIN")
                                                // Public — respondent-facing (external auth handled separately per
                                                // campaign)
                                                .requestMatchers("/api/v1/public/campaigns/**").permitAll()
                                                .requestMatchers("/api/v1/public/site-content/**").permitAll()
                                                .requestMatchers("/api/v1/public/payments/sslcommerz/**").permitAll()
                                                .requestMatchers("/api/v1/auth/validate/**").permitAll()
                                                .requestMatchers("/api/v1/auth/respondent/oidc/**").permitAll()
                                                .requestMatchers("/api/v1/responses").permitAll()

                                                // Admin endpoints — require engine-issued JWT
                                                .requestMatchers("/api/v1/questions/**").authenticated()
                                                .requestMatchers("/api/v1/categories/**").authenticated()
                                                .requestMatchers("/api/v1/surveys/**").authenticated()
                                                .requestMatchers("/api/v1/campaigns/**").authenticated()
                                                .requestMatchers("/api/v1/scoring/**").authenticated()
                                                .requestMatchers("/api/v1/auth/profiles/**").authenticated()
                                                .requestMatchers("/api/v1/responses/**").authenticated()
                                                .requestMatchers("/api/v1/audit-logs/**").authenticated()

                                                .anyRequest().authenticated())
                                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                                .addFilterAfter(subscriptionEnforcementFilter, JwtAuthenticationFilter.class)
                                .addFilterAfter(csrfCookieFilter, org.springframework.security.web.authentication.www.BasicAuthenticationFilter.class);

                return http.build();
        }

        private String frameAncestorsDirective() {
                List<String> origins = new ArrayList<>();
                origins.add("'self'");
                origins.addAll(responderSecurityProperties.getExchange().getAllowedOrigins());
                String frameAncestors = "frame-ancestors " + String.join(" ", origins);
                return frameAncestors + "; object-src 'none'; base-uri 'self'";
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }
}
