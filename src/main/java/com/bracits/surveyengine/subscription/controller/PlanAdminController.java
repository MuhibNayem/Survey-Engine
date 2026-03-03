package com.bracits.surveyengine.subscription.controller;

import com.bracits.surveyengine.subscription.dto.PlanDefinitionRequest;
import com.bracits.surveyengine.subscription.dto.PlanDefinitionResponse;
import com.bracits.surveyengine.subscription.service.PlanCatalogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/plans")
@RequiredArgsConstructor
public class PlanAdminController {

    private final PlanCatalogService planCatalogService;

    @GetMapping
    public ResponseEntity<List<PlanDefinitionResponse>> listActivePlans() {
        return ResponseEntity.ok(planCatalogService.listActivePlans());
    }

    @PutMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<PlanDefinitionResponse> upsert(@Valid @RequestBody PlanDefinitionRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(planCatalogService.upsert(request));
    }
}
