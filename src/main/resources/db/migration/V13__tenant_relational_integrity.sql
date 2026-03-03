-- =====================================================
-- Survey Engine — Tenant Relational Integrity Hardening
-- Adds DB-level tenant foreign keys and tenant-consistent composite FKs
-- =====================================================

-- Ensure all tenant ids used in data exist in tenant catalog
INSERT INTO tenant (id, name, active, created_by)
VALUES ('default', 'default', TRUE, 'system')
ON CONFLICT (id) DO NOTHING;

INSERT INTO tenant (id, name, active, created_by)
SELECT DISTINCT tenant_id, tenant_id, TRUE, 'system'
FROM (
    SELECT tenant_id FROM admin_user
    UNION
    SELECT tenant_id FROM question
    UNION
    SELECT tenant_id FROM category
    UNION
    SELECT tenant_id FROM survey
    UNION
    SELECT tenant_id FROM campaign
    UNION
    SELECT tenant_id FROM weight_profile
    UNION
    SELECT tenant_id FROM auth_profile
    UNION
    SELECT tenant_id FROM survey_response
) t
WHERE tenant_id IS NOT NULL
ON CONFLICT (id) DO NOTHING;

-- Tenant foreign keys for tenant-owned tables
ALTER TABLE admin_user
    ADD CONSTRAINT fk_admin_user_tenant
        FOREIGN KEY (tenant_id) REFERENCES tenant(id) ON DELETE RESTRICT;

ALTER TABLE question
    ADD CONSTRAINT fk_question_tenant
        FOREIGN KEY (tenant_id) REFERENCES tenant(id) ON DELETE RESTRICT;

ALTER TABLE category
    ADD CONSTRAINT fk_category_tenant
        FOREIGN KEY (tenant_id) REFERENCES tenant(id) ON DELETE RESTRICT;

ALTER TABLE survey
    ADD CONSTRAINT fk_survey_tenant
        FOREIGN KEY (tenant_id) REFERENCES tenant(id) ON DELETE RESTRICT;

ALTER TABLE campaign
    ADD CONSTRAINT fk_campaign_tenant
        FOREIGN KEY (tenant_id) REFERENCES tenant(id) ON DELETE RESTRICT;

ALTER TABLE weight_profile
    ADD CONSTRAINT fk_weight_profile_tenant
        FOREIGN KEY (tenant_id) REFERENCES tenant(id) ON DELETE RESTRICT;

ALTER TABLE auth_profile
    ADD CONSTRAINT fk_auth_profile_tenant
        FOREIGN KEY (tenant_id) REFERENCES tenant(id) ON DELETE RESTRICT;

ALTER TABLE survey_response
    ADD CONSTRAINT fk_survey_response_tenant
        FOREIGN KEY (tenant_id) REFERENCES tenant(id) ON DELETE RESTRICT;

-- Composite unique keys used for tenant-consistent relationship FKs
ALTER TABLE survey
    ADD CONSTRAINT uk_survey_id_tenant UNIQUE (id, tenant_id);

ALTER TABLE campaign
    ADD CONSTRAINT uk_campaign_id_tenant UNIQUE (id, tenant_id);

-- Tenant-consistent relationship constraints
ALTER TABLE campaign
    ADD CONSTRAINT fk_campaign_survey_tenant
        FOREIGN KEY (survey_id, tenant_id) REFERENCES survey(id, tenant_id) ON DELETE RESTRICT;

ALTER TABLE weight_profile
    ADD CONSTRAINT fk_weight_profile_campaign_tenant
        FOREIGN KEY (campaign_id, tenant_id) REFERENCES campaign(id, tenant_id) ON DELETE RESTRICT;

ALTER TABLE survey_response
    ADD CONSTRAINT fk_survey_response_campaign_tenant
        FOREIGN KEY (campaign_id, tenant_id) REFERENCES campaign(id, tenant_id) ON DELETE RESTRICT;

-- Helpful tenant-scoped composite indexes
CREATE INDEX IF NOT EXISTS idx_campaign_tenant_survey ON campaign(tenant_id, survey_id);
CREATE INDEX IF NOT EXISTS idx_weight_profile_tenant_campaign ON weight_profile(tenant_id, campaign_id);
CREATE INDEX IF NOT EXISTS idx_survey_response_tenant_campaign ON survey_response(tenant_id, campaign_id);
