package com.bracits.surveyengine.auth.entity;

/**
 * Supported authentication modes for campaign respondent access.
 * SRS §4.9.2
 */
public enum AuthenticationMode {
    /** No authentication — controls only (CAPTCHA, IP, etc.) */
    PUBLIC_ANONYMOUS,
    /** Signed launch token with signature, issuer, audience, expiry */
    SIGNED_LAUNCH_TOKEN,
    /** External SSO trust — OIDC/JWT/SAML validated externally */
    EXTERNAL_SSO_TRUST
}
