-- V21: Add per-feature boolean toggles to plan_definition
-- These columns allow the Super Admin to control which features each plan tier provides.

ALTER TABLE plan_definition
    ADD COLUMN weight_profiles_enabled  BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN signed_token_enabled     BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN sso_enabled              BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN custom_branding_enabled  BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN device_fingerprint_enabled BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN api_access_enabled       BOOLEAN NOT NULL DEFAULT FALSE;

-- Seed sensible defaults matching the pricing strategy:
-- BASIC: all toggles off
-- PRO: weight profiles, signed tokens, custom branding, device fingerprint on
-- ENTERPRISE: everything on
UPDATE plan_definition SET
    weight_profiles_enabled   = TRUE,
    signed_token_enabled      = TRUE,
    custom_branding_enabled   = TRUE,
    device_fingerprint_enabled = TRUE
WHERE plan_code = 'PRO';

UPDATE plan_definition SET
    weight_profiles_enabled   = TRUE,
    signed_token_enabled      = TRUE,
    sso_enabled               = TRUE,
    custom_branding_enabled   = TRUE,
    device_fingerprint_enabled = TRUE,
    api_access_enabled        = TRUE
WHERE plan_code = 'ENTERPRISE';
