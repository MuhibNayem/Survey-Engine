-- =====================================================
-- Survey Engine — Scoring & Weight Profiles
-- SRS §5
-- =====================================================

-- Weight Profiles (per campaign)
CREATE TABLE weight_profile (
    id              UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
    name            VARCHAR(255)    NOT NULL,
    campaign_id     UUID            NOT NULL REFERENCES campaign(id),
    active          BOOLEAN         NOT NULL DEFAULT TRUE,
    created_by      VARCHAR(255)    NOT NULL,
    created_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_by      VARCHAR(255),
    updated_at      TIMESTAMP
);

CREATE INDEX idx_weight_profile_campaign ON weight_profile(campaign_id);

-- Category Weights (percentage per category within a profile)
CREATE TABLE category_weight (
    id                  UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
    weight_profile_id   UUID            NOT NULL REFERENCES weight_profile(id) ON DELETE CASCADE,
    category_id         UUID            NOT NULL REFERENCES category(id),
    weight_percentage   NUMERIC(5,2)    NOT NULL,
    CONSTRAINT chk_weight_percentage CHECK (weight_percentage >= 0 AND weight_percentage <= 100)
);

CREATE INDEX idx_category_weight_profile ON category_weight(weight_profile_id);
CREATE INDEX idx_category_weight_category ON category_weight(category_id);
