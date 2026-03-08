package com.bracits.surveyengine.campaign.dto;

import com.bracits.surveyengine.campaign.entity.DataCollectionFieldType;
import lombok.*;

/**
 * Request DTO for creating/updating a data collection field.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataCollectionFieldRequest {
    private String fieldKey;
    private String label;
    private DataCollectionFieldType fieldType;
    private boolean required;
    private int sortOrder;
    @Builder.Default
    private boolean enabled = true;
}
