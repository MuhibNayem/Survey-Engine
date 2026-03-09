package com.bracits.surveyengine.admin.filter;

import com.bracits.surveyengine.admin.context.TenantContext;
import com.bracits.surveyengine.admin.service.JwtService;
import com.bracits.surveyengine.admin.util.CookieUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
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
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * JWT authentication filter for engine-owned admin tokens.
 * <p>
 * Extracts the access token from:
 * <ol>
 * <li>{@code access_token} HttpOnly cookie (primary — cookie-based auth)</li>
 * <li>{@code Authorization: Bearer ...} header (fallback — API clients)</li>
 * </ol>
 * Validates via {@link JwtService}, populates SecurityContext and
 * {@link TenantContext}.
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
            String token = extractToken(request);

            if (token != null) {
                try {
                    Claims claims = jwtService.parseToken(token);

                    String userId = claims.getSubject();
                    String tenantId = claims.get("tenant_id", String.class);
                    String email = claims.get("email", String.class);
                    String role = claims.get("role", String.class);
                    String impersonatedBy = claims.get("impersonated_by", String.class);

                    // Set Spring Security authentication
                    var authorities = List.of(
                            new SimpleGrantedAuthority("ROLE_" + role));
                    var auth = new UsernamePasswordAuthenticationToken(
                            userId, null, authorities);
                    SecurityContextHolder.getContext().setAuthentication(auth);

                    // Set TenantContext (includes impersonation info if present)
                    TenantContext.set(new TenantContext.TenantInfo(
                            tenantId, userId, email, Set.of(role), impersonatedBy));

                    log.debug("Admin JWT validated: tenant={}, user={}, role={}, impersonatedBy={}",
                            tenantId, userId, role, impersonatedBy);
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

    /**
     * Extracts access token: cookie first, then Authorization header.
     */
    private String extractToken(HttpServletRequest request) {
        // 1. Prefer Authorization header (token-mode clients)
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }

        // 2. Fallback to HttpOnly cookie (browser session mode)
        if (request.getCookies() != null) {
            String cookieToken = Arrays.stream(request.getCookies())
                    .filter(c -> CookieUtil.ACCESS_TOKEN_COOKIE.equals(c.getName()))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse(null);
            if (cookieToken != null && !cookieToken.isBlank()) {
                return cookieToken;
            }
        }

        return null;
    }
}
