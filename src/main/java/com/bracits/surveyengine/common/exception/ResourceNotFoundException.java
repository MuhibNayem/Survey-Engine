package com.bracits.surveyengine.common.exception;

/**
 * Thrown when a requested resource is not found.
 */
public class ResourceNotFoundException extends BusinessException {

    public ResourceNotFoundException(String entityType, Object id) {
        super(ErrorCode.RESOURCE_NOT_FOUND,
                entityType + " not found with id: " + id);
    }
}
