package com.bracits.surveyengine.campaign.dto;

import com.bracits.surveyengine.campaign.entity.DataCollectionFieldType;
import lombok.*;

import java.util.UUID;

/**
 * Response DTO for a data collection field.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataCollectionFieldResponse {
    private UUID id;
    private String fieldKey;
    private String label;
    private DataCollectionFieldType fieldType;
    private boolean required;
    private int sortOrder;
    private boolean enabled;
}
