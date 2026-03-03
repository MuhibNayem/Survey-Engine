-- =====================================================
-- Survey Engine — SaaS Tenancy & Subscription Billing
-- =====================================================

-- Canonical tenant catalog
CREATE TABLE IF NOT EXISTS tenant (
    id          VARCHAR(255)    PRIMARY KEY,
    name        VARCHAR(255)    NOT NULL,
    active      BOOLEAN         NOT NULL DEFAULT TRUE,
    created_by  VARCHAR(255)    NOT NULL,
    created_at  TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_by  VARCHAR(255),
    updated_at  TIMESTAMP
);

-- Ensure tenant_id is present and backfilled for core entities
UPDATE question SET tenant_id = COALESCE(tenant_id, 'default');
UPDATE category SET tenant_id = COALESCE(tenant_id, 'default');
UPDATE survey SET tenant_id = COALESCE(tenant_id, 'default');
UPDATE campaign SET tenant_id = COALESCE(tenant_id, 'default');
UPDATE weight_profile wp
SET tenant_id = COALESCE(wp.tenant_id, c.tenant_id, 'default')
FROM campaign c
WHERE c.id = wp.campaign_id;
UPDATE auth_profile SET tenant_id = COALESCE(tenant_id, 'default');

ALTER TABLE question ALTER COLUMN tenant_id SET NOT NULL;
ALTER TABLE category ALTER COLUMN tenant_id SET NOT NULL;
ALTER TABLE survey ALTER COLUMN tenant_id SET NOT NULL;
ALTER TABLE campaign ALTER COLUMN tenant_id SET NOT NULL;
ALTER TABLE weight_profile ALTER COLUMN tenant_id SET NOT NULL;
ALTER TABLE auth_profile ALTER COLUMN tenant_id SET NOT NULL;

-- Response tenancy for strict admin isolation
ALTER TABLE survey_response ADD COLUMN IF NOT EXISTS tenant_id VARCHAR(255);
UPDATE survey_response sr
SET tenant_id = COALESCE(sr.tenant_id, c.tenant_id, 'default')
FROM campaign c
WHERE c.id = sr.campaign_id;
ALTER TABLE survey_response ALTER COLUMN tenant_id SET NOT NULL;
CREATE INDEX IF NOT EXISTS idx_survey_response_tenant ON survey_response(tenant_id);

-- Seed existing tenants from owned entities/users
INSERT INTO tenant (id, name, active, created_by)
SELECT DISTINCT tenant_id, tenant_id, TRUE, 'system'
FROM (
    SELECT tenant_id FROM admin_user
    UNION
    SELECT tenant_id FROM survey
    UNION
    SELECT tenant_id FROM campaign
    UNION
    SELECT tenant_id FROM auth_profile
) t
WHERE tenant_id IS NOT NULL
ON CONFLICT (id) DO NOTHING;

-- Per-tenant subscription state
CREATE TABLE IF NOT EXISTS tenant_subscription (
    id                      UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id               VARCHAR(255)    NOT NULL UNIQUE REFERENCES tenant(id) ON DELETE CASCADE,
    plan                    VARCHAR(30)     NOT NULL,
    status                  VARCHAR(30)     NOT NULL,
    current_period_start    TIMESTAMP       NOT NULL,
    current_period_end      TIMESTAMP       NOT NULL,
    active                  BOOLEAN         NOT NULL DEFAULT TRUE,
    created_by              VARCHAR(255)    NOT NULL,
    created_at              TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_by              VARCHAR(255),
    updated_at              TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_subscription_tenant ON tenant_subscription(tenant_id);
CREATE INDEX IF NOT EXISTS idx_subscription_status_end ON tenant_subscription(status, current_period_end);

-- Payment transactions (mock gateway writes success records)
CREATE TABLE IF NOT EXISTS payment_transaction (
    id                  UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id           VARCHAR(255)    NOT NULL REFERENCES tenant(id) ON DELETE CASCADE,
    subscription_id     UUID            NOT NULL REFERENCES tenant_subscription(id) ON DELETE CASCADE,
    status              VARCHAR(30)     NOT NULL,
    gateway_reference   VARCHAR(255)    NOT NULL,
    amount              NUMERIC(12,2)   NOT NULL,
    currency            VARCHAR(10)     NOT NULL,
    processed_at        TIMESTAMP       NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_payment_tenant_time ON payment_transaction(tenant_id, processed_at DESC);
