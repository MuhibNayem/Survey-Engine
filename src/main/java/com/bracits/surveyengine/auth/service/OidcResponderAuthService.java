package com.bracits.surveyengine.auth.service;

import com.bracits.surveyengine.auth.dto.OidcCallbackResponse;
import com.bracits.surveyengine.auth.dto.OidcStartRequest;
import com.bracits.surveyengine.auth.dto.OidcStartResponse;
import com.bracits.surveyengine.auth.dto.ResponderAccessIdentity;
import com.bracits.surveyengine.auth.entity.AuthProfile;
import com.bracits.surveyengine.auth.entity.AuthenticationMode;
import com.bracits.surveyengine.auth.entity.OidcAuthState;
import com.bracits.surveyengine.auth.entity.ResponderAccessCode;
import com.bracits.surveyengine.auth.repository.AuthProfileRepository;
import com.bracits.surveyengine.auth.repository.OidcAuthStateRepository;
import com.bracits.surveyengine.auth.repository.ResponderAccessCodeRepository;
import com.bracits.surveyengine.campaign.entity.AuthMode;
import com.bracits.surveyengine.campaign.entity.Campaign;
import com.bracits.surveyengine.campaign.repository.CampaignRepository;
import com.bracits.surveyengine.common.exception.BusinessException;
import com.bracits.surveyengine.common.exception.ErrorCode;
import com.bracits.surveyengine.common.exception.ResourceNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OidcResponderAuthService {

    private final CampaignRepository campaignRepository;
    private final AuthProfileRepository authProfileRepository;
    private final OidcAuthStateRepository oidcAuthStateRepository;
    private final ResponderAccessCodeRepository responderAccessCodeRepository;
    private final TokenValidationService tokenValidationService;
    private final AuthRemoteCacheService authRemoteCacheService;
    @Value("${survey-engine.links.public-base-url:https://survey.example.com}")
    private String publicBaseUrl;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final SecureRandom secureRandom = new SecureRandom();

    @Transactional
    public OidcStartResponse start(OidcStartRequest request, String baseUrl) {
        Campaign campaign = campaignRepository.findById(request.getCampaignId())
                .orElseThrow(() -> new ResourceNotFoundException("Campaign", request.getCampaignId()));

        if (!Objects.equals(campaign.getTenantId(), request.getTenantId())) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED, "Campaign tenant mismatch");
        }
        if (campaign.getAuthMode() != AuthMode.PRIVATE) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED,
                    "OIDC flow is only applicable for private campaigns");
        }

        AuthProfile profile = authProfileRepository.findByTenantId(request.getTenantId())
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCESS_DENIED,
                        "No auth profile configured for tenant"));

        if (profile.getAuthMode() != AuthenticationMode.EXTERNAL_SSO_TRUST) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED,
                    "Tenant auth mode must be EXTERNAL_SSO_TRUST for OIDC flow");
        }

        OidcMetadata metadata = loadMetadata(profile);

        String state = UUID.randomUUID().toString();
        String nonce = UUID.randomUUID().toString();
        String codeVerifier = generateCodeVerifier();
        String codeChallenge = toCodeChallenge(codeVerifier);
        Instant expiresAt = Instant.now().plusSeconds(300);

        oidcAuthStateRepository.deleteByExpiresAtBefore(Instant.now());
        oidcAuthStateRepository.save(OidcAuthState.builder()
                .state(state)
                .tenantId(request.getTenantId())
                .campaignId(request.getCampaignId())
                .nonce(nonce)
                .codeVerifier(codeVerifier)
                .returnPath(normalizeReturnPath(request.getReturnPath()))
                .expiresAt(expiresAt)
                .build());

        String redirectUri = resolveRedirectUri(profile, baseUrl);
        String scope = normalizeScopes(profile.getOidcScopes());

        String authorizationUrl = metadata.authorizationEndpoint
                + "?response_type=code"
                + "&client_id=" + enc(required(profile.getOidcClientId(), "OIDC clientId is required"))
                + "&redirect_uri=" + enc(redirectUri)
                + "&scope=" + enc(scope)
                + "&state=" + enc(state)
                + "&nonce=" + enc(nonce)
                + "&code_challenge=" + enc(codeChallenge)
                + "&code_challenge_method=S256";

        return OidcStartResponse.builder()
                .authorizationUrl(authorizationUrl)
                .state(state)
                .expiresAt(expiresAt)
                .build();
    }

    @Transactional
    public OidcCallbackResponse callback(String state, String code, String baseUrl) {
        if (state == null || state.isBlank() || code == null || code.isBlank()) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED, "OIDC state and code are required");
        }

        OidcAuthState authState = oidcAuthStateRepository.findByState(state)
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCESS_DENIED, "Invalid OIDC state"));

        if (authState.getUsedAt() != null) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED, "OIDC state already used");
        }
        if (Instant.now().isAfter(authState.getExpiresAt())) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED, "OIDC state expired");
        }

        AuthProfile profile = authProfileRepository.findByTenantId(authState.getTenantId())
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCESS_DENIED,
                        "Auth profile not found for tenant"));

        OidcMetadata metadata = loadMetadata(profile);
        String redirectUri = resolveRedirectUri(profile, baseUrl);

        String codeVerifier = required(authState.getCodeVerifier(), "OIDC PKCE code_verifier missing");
        String tokenResponse;
        try {
            tokenResponse = exchangeToken(metadata.tokenEndpoint, profile, code, redirectUri, codeVerifier);
        } catch (BusinessException ex) {
            // Metadata can go stale if IdP rotates endpoints. Refresh once and retry.
            evictMetadataCache(profile.getOidcDiscoveryUrl());
            OidcMetadata refreshedMetadata = loadMetadata(profile);
            tokenResponse = exchangeToken(refreshedMetadata.tokenEndpoint, profile, code, redirectUri, codeVerifier);
        }
        Map<String, Object> tokenJson = parseJsonMap(tokenResponse);
        String idToken = asString(tokenJson.get("id_token"));
        if (idToken == null || idToken.isBlank()) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED, "OIDC token response missing id_token");
        }

        var result = tokenValidationService.validateToken(authState.getTenantId(), idToken, authState.getNonce());
        if (!result.isValid()) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED,
                    "OIDC token validation failed: " + result.getErrorMessage());
        }

        Instant codeExpiry = Instant.now().plusSeconds(300);
        String accessCode = UUID.randomUUID().toString().replace("-", "");

        responderAccessCodeRepository.deleteByExpiresAtBefore(Instant.now());
        responderAccessCodeRepository.save(ResponderAccessCode.builder()
                .accessCode(accessCode)
                .tenantId(authState.getTenantId())
                .campaignId(authState.getCampaignId())
                .respondentId(result.getRespondentId())
                .email(result.getEmail())
                .expiresAt(codeExpiry)
                .createdAt(Instant.now())
                .build());

        authState.setUsedAt(Instant.now());
        oidcAuthStateRepository.save(authState);

        String redirectUrl = null;
        if (authState.getReturnPath() != null) {
            redirectUrl = resolveUiBaseUrl(baseUrl) + authState.getReturnPath()
                    + (authState.getReturnPath().contains("?") ? "&" : "?")
                    + "auth_code=" + enc(accessCode);
        }

        return OidcCallbackResponse.builder()
                .accessCode(accessCode)
                .tenantId(authState.getTenantId())
                .campaignId(authState.getCampaignId())
                .respondentId(result.getRespondentId())
                .email(result.getEmail())
                .expiresAt(codeExpiry)
                .redirectUrl(redirectUrl)
                .build();
    }

    @Transactional
    public ResponderAccessIdentity consumeAccessCode(String accessCode, String tenantId, UUID campaignId) {
        if (accessCode == null || accessCode.isBlank()) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED, "Responder access code is required");
        }

        responderAccessCodeRepository.deleteByExpiresAtBefore(Instant.now());
        ResponderAccessCode code = responderAccessCodeRepository.findByAccessCode(accessCode)
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCESS_DENIED, "Invalid responder access code"));

        if (code.getUsedAt() != null) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED, "Responder access code already used");
        }
        if (Instant.now().isAfter(code.getExpiresAt())) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED, "Responder access code expired");
        }
        if (!Objects.equals(code.getTenantId(), tenantId) || !Objects.equals(code.getCampaignId(), campaignId)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED, "Responder access code does not match campaign");
        }

        code.setUsedAt(Instant.now());
        responderAccessCodeRepository.save(code);

        return ResponderAccessIdentity.builder()
                .respondentId(code.getRespondentId())
                .email(code.getEmail())
                .build();
    }

    private OidcMetadata loadMetadata(AuthProfile profile) {
        try {
            String discoveryUrl = required(profile.getOidcDiscoveryUrl(), "OIDC discovery URL is required");
            String metadataJson = authRemoteCacheService.getOidcMetadataJson(discoveryUrl);
            Map<String, Object> json = parseJsonMap(metadataJson);
            OidcMetadata metadata = new OidcMetadata(
                    required(asString(json.get("authorization_endpoint")), "authorization_endpoint missing"),
                    required(asString(json.get("token_endpoint")), "token_endpoint missing"));
            return metadata;
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED, "Failed to load OIDC metadata: " + e.getMessage());
        }
    }

    public void evictMetadataCache(String discoveryUrl) {
        if (discoveryUrl != null && !discoveryUrl.isBlank()) {
            authRemoteCacheService.evictOidcMetadata(discoveryUrl);
        }
    }

    private String exchangeToken(String tokenEndpoint, AuthProfile profile, String code, String redirectUri,
            String codeVerifier) {
        try {
            String body = "grant_type=authorization_code"
                    + "&code=" + enc(code)
                    + "&redirect_uri=" + enc(redirectUri)
                    + "&client_id=" + enc(required(profile.getOidcClientId(), "OIDC clientId is required"))
                    + "&client_secret=" + enc(required(profile.getOidcClientSecret(), "OIDC clientSecret is required"))
                    + "&code_verifier=" + enc(required(codeVerifier, "OIDC code_verifier is required"));

            HttpRequest req = HttpRequest.newBuilder(URI.create(tokenEndpoint))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();
            HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() < 200 || resp.statusCode() >= 300) {
                throw new RuntimeException("Token exchange failed: HTTP " + resp.statusCode());
            }
            return resp.body();
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED, "OIDC token exchange failed: " + e.getMessage());
        }
    }

    private Map<String, Object> parseJsonMap(String json) {
        try {
            return objectMapper.readValue(json, Map.class);
        } catch (Exception e) {
            throw new RuntimeException("Invalid JSON", e);
        }
    }

    private String normalizeReturnPath(String path) {
        if (path == null || path.isBlank()) {
            return null;
        }
        if (!path.startsWith("/")) {
            throw new BusinessException(ErrorCode.VALIDATION_FAILED, "returnPath must start with '/'");
        }
        return path;
    }

    private String resolveUiBaseUrl(String fallbackBaseUrl) {
        String configured = normalizeBaseUrl(publicBaseUrl);
        return configured != null ? configured : normalizeBaseUrl(fallbackBaseUrl);
    }

    private String normalizeBaseUrl(String rawBaseUrl) {
        if (rawBaseUrl == null) {
            return null;
        }
        String value = rawBaseUrl.trim();
        if (value.isEmpty()) {
            return null;
        }
        return value.endsWith("/") ? value.substring(0, value.length() - 1) : value;
    }

    private String resolveRedirectUri(AuthProfile profile, String baseUrl) {
        if (profile.getOidcRedirectUri() != null && !profile.getOidcRedirectUri().isBlank()) {
            return profile.getOidcRedirectUri();
        }
        return baseUrl + "/api/v1/auth/respondent/oidc/callback";
    }

    private String required(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new BusinessException(ErrorCode.VALIDATION_FAILED, message);
        }
        return value;
    }

    private String enc(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private String asString(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    private String normalizeScopes(String rawScopes) {
        if (rawScopes == null || rawScopes.isBlank()) {
            return "openid email profile";
        }
        LinkedHashSet<String> scopes = new LinkedHashSet<>();
        for (String part : rawScopes.split("[,\\s]+")) {
            if (!part.isBlank()) {
                scopes.add(part.trim());
            }
        }
        scopes.add("openid");
        if (scopes.isEmpty()) {
            return "openid email profile";
        }
        return String.join(" ", scopes);
    }

    private String generateCodeVerifier() {
        byte[] random = new byte[32];
        secureRandom.nextBytes(random);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(random);
    }

    private String toCodeChallenge(String codeVerifier) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(codeVerifier.getBytes(StandardCharsets.US_ASCII));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "Failed to generate PKCE code challenge", e);
        }
    }

    private record OidcMetadata(String authorizationEndpoint, String tokenEndpoint) {
    }
}
