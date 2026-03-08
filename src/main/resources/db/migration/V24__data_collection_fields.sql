-- V24: Dynamic data collection fields + respondent metadata JSONB column

CREATE TABLE data_collection_field (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    campaign_settings_id UUID NOT NULL REFERENCES campaign_settings(id) ON DELETE CASCADE,
    field_key VARCHAR(50) NOT NULL,
    label VARCHAR(100) NOT NULL,
    field_type VARCHAR(20) NOT NULL DEFAULT 'TEXT',
    required BOOLEAN NOT NULL DEFAULT false,
    sort_order INT NOT NULL DEFAULT 0,
    enabled BOOLEAN NOT NULL DEFAULT true
);

CREATE INDEX idx_dcf_campaign_settings ON data_collection_field(campaign_settings_id);

-- Add JSONB column for flexible respondent metadata storage
ALTER TABLE survey_response ADD COLUMN respondent_metadata JSONB;

-- Migrate existing boolean flags to data_collection_field rows
INSERT INTO data_collection_field (campaign_settings_id, field_key, label, field_type, required, sort_order, enabled)
SELECT id, 'name', 'Name', 'TEXT', true, 1, true
FROM campaign_settings WHERE collect_name = true;

INSERT INTO data_collection_field (campaign_settings_id, field_key, label, field_type, required, sort_order, enabled)
SELECT id, 'email', 'Email', 'EMAIL', true, 2, true
FROM campaign_settings WHERE collect_email = true;

INSERT INTO data_collection_field (campaign_settings_id, field_key, label, field_type, required, sort_order, enabled)
SELECT id, 'phone', 'Phone', 'PHONE', true, 3, true
FROM campaign_settings WHERE collect_phone = true;

INSERT INTO data_collection_field (campaign_settings_id, field_key, label, field_type, required, sort_order, enabled)
SELECT id, 'address', 'Address', 'TEXTAREA', true, 4, true
FROM campaign_settings WHERE collect_address = true;
