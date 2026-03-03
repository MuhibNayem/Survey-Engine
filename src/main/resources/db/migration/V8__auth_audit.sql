-- =====================================================
-- Survey Engine — Authentication, Audit & Hardening
-- SRS §4.9, §7
-- Auth profiles are per-tenant (not per-campaign)
-- =====================================================

-- Auth Profiles (per tenant — one config for all campaigns under a tenant)
CREATE TABLE auth_profile (
    id                  UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id           VARCHAR(255)    NOT NULL UNIQUE,
    auth_mode           VARCHAR(30)     NOT NULL DEFAULT 'PUBLIC_ANONYMOUS',
    issuer              VARCHAR(500),
    audience            VARCHAR(500),
    jwks_endpoint       VARCHAR(500),
    clock_skew_seconds  INTEGER         NOT NULL DEFAULT 30,
    token_ttl_seconds   INTEGER         NOT NULL DEFAULT 3600,
    signing_secret      VARCHAR(500),
    active_key_version  INTEGER         NOT NULL DEFAULT 1,
    fallback_policy     VARCHAR(30)     NOT NULL DEFAULT 'SSO_REQUIRED',
    active              BOOLEAN         NOT NULL DEFAULT TRUE,
    created_by          VARCHAR(255)    NOT NULL,
    created_at          TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_by          VARCHAR(255),
    updated_at          TIMESTAMP
);

-- Claim Mappings (external claim → internal field)
CREATE TABLE claim_mapping (
    id                  UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
    auth_profile_id     UUID            NOT NULL REFERENCES auth_profile(id) ON DELETE CASCADE,
    external_claim      VARCHAR(255)    NOT NULL,
    internal_field      VARCHAR(255)    NOT NULL,
    required            BOOLEAN         NOT NULL DEFAULT FALSE
);

CREATE INDEX idx_claim_mapping_profile ON claim_mapping(auth_profile_id);

-- Auth Config Audit (SRS §4.9.4)
CREATE TABLE auth_config_audit (
    id                  UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
    auth_profile_id     UUID            NOT NULL REFERENCES auth_profile(id),
    action              VARCHAR(50)     NOT NULL,
    changed_by          VARCHAR(255)    NOT NULL,
    before_value        TEXT,
    after_value         TEXT,
    changed_at          TIMESTAMP       NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_auth_audit_profile ON auth_config_audit(auth_profile_id);
CREATE INDEX idx_auth_audit_time ON auth_config_audit(changed_at);
