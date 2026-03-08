-- V22: Enable all plan feature toggles by default.
-- Keeps V21 immutable and applies the newer business policy in a forward-only migration.

UPDATE plan_definition
SET weight_profiles_enabled = TRUE,
    signed_token_enabled = TRUE,
    sso_enabled = TRUE,
    custom_branding_enabled = TRUE,
    device_fingerprint_enabled = TRUE,
    api_access_enabled = TRUE;
