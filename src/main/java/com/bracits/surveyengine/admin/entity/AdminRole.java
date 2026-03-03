package com.bracits.surveyengine.admin.entity;

/**
 * Admin roles for platform access control.
 * Mapped from JWT role claims.
 */
public enum AdminRole {
    /** Full platform access */
    SUPER_ADMIN,
    /** Can create/manage surveys, campaigns, settings */
    ADMIN,
    /** Can edit surveys and questions */
    EDITOR,
    /** Read-only access to reports and analytics */
    VIEWER
}
