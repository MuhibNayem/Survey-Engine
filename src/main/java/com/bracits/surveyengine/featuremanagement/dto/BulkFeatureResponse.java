package com.bracits.surveyengine.featuremanagement.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

/**
 * Response DTO for bulk feature operations.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BulkFeatureResponse {

    /**
     * Total number of features processed.
     */
    private Integer totalProcessed;

    /**
     * Number of features successfully created/updated.
     */
    private Integer successCount;

    /**
     * Number of features that failed.
     */
    private Integer failureCount;

    /**
     * List of successfully processed feature keys.
     */
    private List<String> successfulKeys;

    /**
     * List of failed operations with error details.
     */
    private List<FailedOperation> failures;

    /**
     * Inner class for failed operation details.
     */
    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FailedOperation {
        /**
         * Feature key that failed.
         */
        private String featureKey;

        /**
         * Error message.
         */
        private String error;

        /**
         * Error code.
         */
        private String errorCode;
    }
}
