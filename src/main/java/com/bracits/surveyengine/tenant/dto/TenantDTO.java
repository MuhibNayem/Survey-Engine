package com.bracits.surveyengine.tenant.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantDTO {
    private String id;
    private String name;
    private boolean active;
    private String adminEmail;
    private String subscriptionPlan;
}
