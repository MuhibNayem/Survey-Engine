package com.bracits.surveyengine.auth.controller;

import com.bracits.surveyengine.auth.dto.AuthProfileRequest;
import com.bracits.surveyengine.auth.dto.AuthProfileResponse;
import com.bracits.surveyengine.auth.dto.TokenValidationResult;
import com.bracits.surveyengine.auth.entity.AuthConfigAudit;
import com.bracits.surveyengine.auth.repository.AuthConfigAuditRepository;
import com.bracits.surveyengine.auth.service.AuthProfileService;
import com.bracits.surveyengine.auth.service.TokenValidationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthProfileService authProfileService;
    private final TokenValidationService tokenValidationService;
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
}
