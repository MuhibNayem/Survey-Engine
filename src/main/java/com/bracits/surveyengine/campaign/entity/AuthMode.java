package com.bracits.surveyengine.campaign.entity;

/**
 * Campaign access mode.
 * <p>
 * Auth configuration details are tenant-scoped via AuthProfile.
 * Campaigns only decide whether responder authentication is required.
 */
public enum AuthMode {
    PUBLIC,
    PRIVATE,
    /** @deprecated Use PRIVATE. Kept for backward compatibility. */
    @Deprecated
    SIGNED_TOKEN,
    /** @deprecated Use PRIVATE. Kept for backward compatibility. */
    @Deprecated
    SSO
}
