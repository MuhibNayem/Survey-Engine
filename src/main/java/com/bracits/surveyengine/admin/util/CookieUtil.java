package com.bracits.surveyengine.admin.util;

import com.bracits.surveyengine.admin.dto.AuthResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * Manages HttpOnly, Secure, SameSite=Strict cookies
 * for admin JWT access and refresh tokens.
 *
 * <p>
 * Cookie TTLs are driven by the same properties that govern
 * token generation ({@code survey-engine.jwt.ttl-seconds} and
 * {@code survey-engine.jwt.refresh-ttl-seconds}).
 */
@Component
public class CookieUtil {

    public static final String ACCESS_TOKEN_COOKIE = "access_token";
    public static final String REFRESH_TOKEN_COOKIE = "refresh_token";
    public static final String IMPERSONATOR_ACCESS_TOKEN_COOKIE = "sa_access_token";
    public static final String IMPERSONATOR_REFRESH_TOKEN_COOKIE = "sa_refresh_token";
    private static final String COOKIE_PATH = "/";

    private final long accessTokenMaxAge;
    private final long refreshTokenMaxAge;

    public CookieUtil(
            @Value("${survey-engine.jwt.ttl-seconds:3600}") long accessTokenMaxAge,
            @Value("${survey-engine.jwt.refresh-ttl-seconds:604800}") long refreshTokenMaxAge) {
        this.accessTokenMaxAge = accessTokenMaxAge;
        this.refreshTokenMaxAge = refreshTokenMaxAge;
    }

    /**
     * Adds access_token and refresh_token as HttpOnly cookies.
     */
    public void addTokenCookies(HttpServletResponse response, AuthResponse authResponse) {
        addCookie(response, ACCESS_TOKEN_COOKIE, authResponse.getAccessToken(), accessTokenMaxAge);
        addCookie(response, REFRESH_TOKEN_COOKIE, authResponse.getRefreshToken(), refreshTokenMaxAge);
    }

    /**
     * Clears both token cookies by setting maxAge to 0.
     */
    public void clearTokenCookies(HttpServletResponse response) {
        addCookie(response, ACCESS_TOKEN_COOKIE, "", 0);
        addCookie(response, REFRESH_TOKEN_COOKIE, "", 0);
        addCookie(response, IMPERSONATOR_ACCESS_TOKEN_COOKIE, "", 0);
        addCookie(response, IMPERSONATOR_REFRESH_TOKEN_COOKIE, "", 0);
    }

    public void stashImpersonatorCookies(jakarta.servlet.http.HttpServletRequest request,
            HttpServletResponse response) {
        if (request.getCookies() != null) {
            for (jakarta.servlet.http.Cookie cookie : request.getCookies()) {
                if (ACCESS_TOKEN_COOKIE.equals(cookie.getName())) {
                    addCookie(response, IMPERSONATOR_ACCESS_TOKEN_COOKIE, cookie.getValue(), accessTokenMaxAge);
                } else if (REFRESH_TOKEN_COOKIE.equals(cookie.getName())) {
                    addCookie(response, IMPERSONATOR_REFRESH_TOKEN_COOKIE, cookie.getValue(), refreshTokenMaxAge);
                }
            }
        }
    }

    public void restoreImpersonatorCookies(jakarta.servlet.http.HttpServletRequest request,
            HttpServletResponse response) {
        if (request.getCookies() != null) {
            String saAccess = null;
            String saRefresh = null;
            for (jakarta.servlet.http.Cookie cookie : request.getCookies()) {
                if (IMPERSONATOR_ACCESS_TOKEN_COOKIE.equals(cookie.getName())) {
                    saAccess = cookie.getValue();
                } else if (IMPERSONATOR_REFRESH_TOKEN_COOKIE.equals(cookie.getName())) {
                    saRefresh = cookie.getValue();
                }
            }
            if (saAccess != null && saRefresh != null) {
                addCookie(response, ACCESS_TOKEN_COOKIE, saAccess, accessTokenMaxAge);
                addCookie(response, REFRESH_TOKEN_COOKIE, saRefresh, refreshTokenMaxAge);
                addCookie(response, IMPERSONATOR_ACCESS_TOKEN_COOKIE, "", 0);
                addCookie(response, IMPERSONATOR_REFRESH_TOKEN_COOKIE, "", 0);
            }
        }
    }

    private void addCookie(HttpServletResponse response, String name, String value, long maxAge) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(true)
                .path(COOKIE_PATH)
                .maxAge(Duration.ofSeconds(maxAge))
                .sameSite("Strict")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}
