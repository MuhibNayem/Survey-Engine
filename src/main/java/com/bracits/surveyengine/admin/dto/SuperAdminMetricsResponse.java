package com.bracits.surveyengine.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SuperAdminMetricsResponse {
    private long totalTenants;
    private long activeSubscriptions;
    private long totalPlatformUsage; // Total survey responses
}
