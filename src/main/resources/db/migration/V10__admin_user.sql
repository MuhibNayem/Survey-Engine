-- =====================================================
-- Survey Engine — Admin User & Refresh Tokens
-- =====================================================

CREATE TABLE admin_user (
    id              UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
    email           VARCHAR(255)    NOT NULL UNIQUE,
    password_hash   VARCHAR(255)    NOT NULL,
    full_name       VARCHAR(255)    NOT NULL,
    role            VARCHAR(30)     NOT NULL DEFAULT 'ADMIN',
    tenant_id       VARCHAR(255)    NOT NULL,
    active          BOOLEAN         NOT NULL DEFAULT TRUE,
    created_by      VARCHAR(255)    NOT NULL,
    created_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_by      VARCHAR(255),
    updated_at      TIMESTAMP
);

CREATE INDEX idx_admin_user_email ON admin_user(email);
CREATE INDEX idx_admin_user_tenant ON admin_user(tenant_id);

-- Refresh tokens for admin auth
CREATE TABLE refresh_token (
    id          UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
    token       VARCHAR(255)    NOT NULL UNIQUE,
    user_id     UUID            NOT NULL REFERENCES admin_user(id) ON DELETE CASCADE,
    expires_at  TIMESTAMP       NOT NULL,
    revoked     BOOLEAN         NOT NULL DEFAULT FALSE
);

CREATE INDEX idx_refresh_token_token ON refresh_token(token);
CREATE INDEX idx_refresh_token_user ON refresh_token(user_id);
