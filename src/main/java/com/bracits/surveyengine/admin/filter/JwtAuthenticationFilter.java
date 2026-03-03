package com.bracits.surveyengine.admin.filter;

import com.bracits.surveyengine.admin.context.TenantContext;
import com.bracits.surveyengine.admin.service.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * JWT authentication filter for engine-owned admin tokens.
 * Extracts Bearer token, validates via {@link JwtService},
 * populates SecurityContext and {@link TenantContext}.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String header = request.getHeader("Authorization");

            if (header != null && header.startsWith("Bearer ")) {
                String token = header.substring(7);
                try {
                    Claims claims = jwtService.parseToken(token);

                    String userId = claims.getSubject();
                    String tenantId = claims.get("tenant_id", String.class);
                    String email = claims.get("email", String.class);
                    String role = claims.get("role", String.class);

                    // Set Spring Security authentication
                    var authorities = List.of(
                            new SimpleGrantedAuthority("ROLE_" + role));
                    var auth = new UsernamePasswordAuthenticationToken(
                            userId, null, authorities);
                    SecurityContextHolder.getContext().setAuthentication(auth);

                    // Set TenantContext
                    TenantContext.set(new TenantContext.TenantInfo(
                            tenantId, userId, email, Set.of(role)));

                    log.debug("Admin JWT validated: tenant={}, user={}, role={}",
                            tenantId, userId, role);
                } catch (Exception e) {
                    log.debug("Invalid JWT: {}", e.getMessage());
                    // Don't set auth — let Spring Security handle 401
                }
            }

            filterChain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }
}
