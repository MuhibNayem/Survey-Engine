-- =====================================================
-- Survey Engine — OIDC PKCE support
-- =====================================================

ALTER TABLE oidc_auth_state
    ADD COLUMN IF NOT EXISTS code_verifier VARCHAR(255);
