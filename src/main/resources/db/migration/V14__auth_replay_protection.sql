-- =====================================================
-- Survey Engine — Auth Replay Protection
-- =====================================================

CREATE TABLE IF NOT EXISTS auth_token_replay (
    id          UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id   VARCHAR(255)    NOT NULL REFERENCES tenant(id) ON DELETE CASCADE,
    jti         VARCHAR(255)    NOT NULL,
    expires_at  TIMESTAMP       NOT NULL,
    used_at     TIMESTAMP       NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_auth_token_replay_tenant_jti UNIQUE (tenant_id, jti)
);

CREATE INDEX IF NOT EXISTS idx_auth_token_replay_exp ON auth_token_replay(expires_at);
