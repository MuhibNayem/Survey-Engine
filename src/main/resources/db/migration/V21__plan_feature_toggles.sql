-- V21: Add per-feature boolean toggles to plan_definition
-- These columns allow the Super Admin to control which features each plan tier provides.

ALTER TABLE plan_definition
    ADD COLUMN weight_profiles_enabled  BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN signed_token_enabled     BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN sso_enabled              BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN custom_branding_enabled  BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN device_fingerprint_enabled BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN api_access_enabled       BOOLEAN NOT NULL DEFAULT FALSE;

-- Seed sensible defaults: all features enabled by default.
-- The Super Admin can selectively disable features for lower-tier plans
-- through the admin UI at /admin/plans.
UPDATE plan_definition SET
    weight_profiles_enabled   = TRUE,
    signed_token_enabled      = TRUE,
    sso_enabled               = TRUE,
    custom_branding_enabled   = TRUE,
    device_fingerprint_enabled = TRUE,
    api_access_enabled        = TRUE;
