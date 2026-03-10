package com.bracits.surveyengine.auth.controller;

import com.bracits.surveyengine.auth.dto.AuthProfileRequest;
import com.bracits.surveyengine.auth.dto.AuthProfileResponse;
import com.bracits.surveyengine.auth.dto.OidcCallbackResponse;
import com.bracits.surveyengine.auth.dto.OidcStartRequest;
import com.bracits.surveyengine.auth.dto.OidcStartResponse;
import com.bracits.surveyengine.auth.dto.ProviderTemplateResponse;
import com.bracits.surveyengine.auth.dto.ResponderAccessIdentity;
import com.bracits.surveyengine.auth.dto.TokenValidationResult;
import com.bracits.surveyengine.auth.entity.AuthConfigAudit;
import com.bracits.surveyengine.auth.repository.AuthConfigAuditRepository;
import com.bracits.surveyengine.auth.service.AuthProfileService;
import com.bracits.surveyengine.auth.service.OidcResponderAuthService;
import com.bracits.surveyengine.auth.service.ResponderSessionService;
import com.bracits.surveyengine.auth.service.TokenValidationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthProfileService authProfileService;
    private final TokenValidationService tokenValidationService;
    private final OidcResponderAuthService oidcResponderAuthService;
    private final ResponderSessionService responderSessionService;
    private final AuthConfigAuditRepository auditRepository;

    @PostMapping("/profiles")
    public ResponseEntity<AuthProfileResponse> create(
            @Valid @RequestBody AuthProfileRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authProfileService.create(request));
    }

    @PutMapping("/profiles/{id}")
    public ResponseEntity<AuthProfileResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody AuthProfileRequest request) {
        return ResponseEntity.ok(authProfileService.update(id, request));
    }

    @GetMapping("/profiles/tenant/{tenantId}")
    public ResponseEntity<AuthProfileResponse> getByTenant(@PathVariable String tenantId) {
        return ResponseEntity.ok(authProfileService.getByTenantId(tenantId));
    }

    @PostMapping("/profiles/{id}/rotate-key")
    public ResponseEntity<AuthProfileResponse> rotateKey(@PathVariable UUID id) {
        return ResponseEntity.ok(authProfileService.rotateKey(id));
    }

    /** Respondent token validation — per tenant */
    @PostMapping("/validate/{tenantId}")
    public ResponseEntity<TokenValidationResult> validateToken(
            @PathVariable String tenantId,
            @RequestBody(required = false) String token) {
        return ResponseEntity.ok(tokenValidationService.validateToken(tenantId, token));
    }

    @GetMapping("/profiles/{id}/audit")
    public ResponseEntity<List<AuthConfigAudit>> getAuditHistory(@PathVariable UUID id) {
        return ResponseEntity.ok(
                auditRepository.findByAuthProfileIdOrderByChangedAtDesc(id));
    }

    @GetMapping("/providers/templates")
    public ResponseEntity<List<ProviderTemplateResponse>> listProviderTemplates() {
        return ResponseEntity.ok(authProfileService.listProviderTemplates());
    }

    @GetMapping("/providers/templates/{providerCode}")
    public ResponseEntity<ProviderTemplateResponse> getProviderTemplate(@PathVariable String providerCode) {
        return ResponseEntity.ok(authProfileService.getProviderTemplate(providerCode));
    }

    /** Respondent OIDC authorization flow start. */
    @PostMapping("/respondent/oidc/start")
    public ResponseEntity<OidcStartResponse> startOidc(
            @Valid @RequestBody OidcStartRequest request,
            HttpServletRequest httpRequest) {
        return ResponseEntity.ok(oidcResponderAuthService.start(request, baseUrl(httpRequest)));
    }

    /** Respondent OIDC callback from external IdP. */
    @GetMapping("/respondent/oidc/callback")
    public ResponseEntity<?> oidcCallback(
            @RequestParam String state,
            @RequestParam String code,
            HttpServletRequest httpRequest) {
        OidcCallbackResponse response = oidcResponderAuthService.callback(state, code, baseUrl(httpRequest));
        if (response.getRedirectUrl() != null) {
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header(
                            org.springframework.http.HttpHeaders.SET_COOKIE,
                            responderSessionService.createSessionCookie(
                                    httpRequest,
                                    response.getTenantId(),
                                    response.getCampaignId(),
                                    ResponderAccessIdentity.builder()
                                            .respondentId(response.getRespondentId())
                                            .email(response.getEmail())
                                            .build())
                                    .toString())
                    .location(URI.create(response.getRedirectUrl()))
                    .build();
        }
        return ResponseEntity.ok(response);
    }

    private String baseUrl(HttpServletRequest request) {
        String scheme = request.getScheme();
        String host = request.getServerName();
        int port = request.getServerPort();
        boolean defaultPort = ("http".equalsIgnoreCase(scheme) && port == 80)
                || ("https".equalsIgnoreCase(scheme) && port == 443);
        return defaultPort ? scheme + "://" + host : scheme + "://" + host + ":" + port;
    }
}
