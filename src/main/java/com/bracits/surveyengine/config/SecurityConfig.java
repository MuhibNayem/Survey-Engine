package com.bracits.surveyengine.config;

import com.bracits.surveyengine.admin.filter.JwtAuthenticationFilter;
import com.bracits.surveyengine.subscription.service.SubscriptionEnforcementFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

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

        private final JwtAuthenticationFilter jwtAuthenticationFilter;
        private final SubscriptionEnforcementFilter subscriptionEnforcementFilter;

        public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                        SubscriptionEnforcementFilter subscriptionEnforcementFilter) {
                this.jwtAuthenticationFilter = jwtAuthenticationFilter;
                this.subscriptionEnforcementFilter = subscriptionEnforcementFilter;
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                .csrf(csrf -> csrf.disable())
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .authorizeHttpRequests(auth -> auth
                                                // Public — admin registration & login
                                                .requestMatchers("/api/v1/admin/auth/**").permitAll()
                                                .requestMatchers("/api/v1/admin/subscriptions/**").authenticated()
                                                // Public — health checks
                                                .requestMatchers("/actuator/**").permitAll()
                                                .requestMatchers(HttpMethod.PUT, "/api/v1/admin/plans/**")
                                                .hasRole("SUPER_ADMIN")
                                                // Public — respondent-facing (external auth handled separately per
                                                // campaign)
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

                                                .anyRequest().authenticated())
                                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                                .addFilterAfter(subscriptionEnforcementFilter, JwtAuthenticationFilter.class);

                return http.build();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }
}
