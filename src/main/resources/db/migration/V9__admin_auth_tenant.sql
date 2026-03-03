-- =====================================================
-- Survey Engine — Admin Auth & Tenant Tracking
-- Adds tenant_id columns for multi-tenant data isolation
-- =====================================================

-- Track which tenant owns each top-level entity
ALTER TABLE question ADD COLUMN IF NOT EXISTS tenant_id VARCHAR(255);
ALTER TABLE category ADD COLUMN IF NOT EXISTS tenant_id VARCHAR(255);
ALTER TABLE survey ADD COLUMN IF NOT EXISTS tenant_id VARCHAR(255);
ALTER TABLE campaign ADD COLUMN IF NOT EXISTS tenant_id VARCHAR(255);
ALTER TABLE weight_profile ADD COLUMN IF NOT EXISTS tenant_id VARCHAR(255);
ALTER TABLE auth_profile ADD COLUMN IF NOT EXISTS tenant_id VARCHAR(255);

-- Indexes for tenant-scoped queries
CREATE INDEX IF NOT EXISTS idx_question_tenant ON question(tenant_id);
CREATE INDEX IF NOT EXISTS idx_category_tenant ON category(tenant_id);
CREATE INDEX IF NOT EXISTS idx_survey_tenant ON survey(tenant_id);
CREATE INDEX IF NOT EXISTS idx_campaign_tenant ON campaign(tenant_id);
CREATE INDEX IF NOT EXISTS idx_weight_profile_tenant ON weight_profile(tenant_id);
CREATE INDEX IF NOT EXISTS idx_auth_profile_tenant ON auth_profile(tenant_id);
