-- =====================================================
-- Survey Engine — OIDC Responder Flow
-- =====================================================

ALTER TABLE auth_profile ADD COLUMN IF NOT EXISTS oidc_discovery_url VARCHAR(1000);
ALTER TABLE auth_profile ADD COLUMN IF NOT EXISTS oidc_client_id VARCHAR(500);
ALTER TABLE auth_profile ADD COLUMN IF NOT EXISTS oidc_client_secret VARCHAR(1000);
ALTER TABLE auth_profile ADD COLUMN IF NOT EXISTS oidc_redirect_uri VARCHAR(1000);
ALTER TABLE auth_profile ADD COLUMN IF NOT EXISTS oidc_scopes VARCHAR(500);

CREATE TABLE IF NOT EXISTS oidc_auth_state (
    id          UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
    state       VARCHAR(255)    NOT NULL UNIQUE,
    tenant_id   VARCHAR(255)    NOT NULL REFERENCES tenant(id) ON DELETE CASCADE,
    campaign_id UUID            NOT NULL,
    nonce       VARCHAR(255)    NOT NULL,
    return_path VARCHAR(1000),
    expires_at  TIMESTAMP       NOT NULL,
    used_at     TIMESTAMP,
    CONSTRAINT fk_oidc_auth_state_campaign_tenant
        FOREIGN KEY (campaign_id, tenant_id) REFERENCES campaign(id, tenant_id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_oidc_auth_state_exp ON oidc_auth_state(expires_at);

CREATE TABLE IF NOT EXISTS responder_access_code (
    id              UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
    access_code     VARCHAR(255)    NOT NULL UNIQUE,
    tenant_id       VARCHAR(255)    NOT NULL REFERENCES tenant(id) ON DELETE CASCADE,
    campaign_id     UUID            NOT NULL,
    respondent_id   VARCHAR(500)    NOT NULL,
    email           VARCHAR(500),
    expires_at      TIMESTAMP       NOT NULL,
    used_at         TIMESTAMP,
    created_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_responder_access_campaign_tenant
        FOREIGN KEY (campaign_id, tenant_id) REFERENCES campaign(id, tenant_id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_responder_access_exp ON responder_access_code(expires_at);
