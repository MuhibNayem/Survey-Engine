-- =====================================================
-- Survey Engine — Campaign Default Scoring Simplification
-- - Store survey category weight directly on survey_question
-- - Store campaign default weight profile binding
-- - Persist weighted scoring result per survey_response
-- =====================================================

ALTER TABLE survey_question
    ADD COLUMN IF NOT EXISTS category_weight_percentage NUMERIC(5,2);

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'chk_survey_question_category_weight_percentage'
    ) THEN
        ALTER TABLE survey_question
            ADD CONSTRAINT chk_survey_question_category_weight_percentage
                CHECK (
                    category_weight_percentage IS NULL
                        OR (category_weight_percentage > 0 AND category_weight_percentage <= 100)
                    );
    END IF;
END $$;

ALTER TABLE campaign
    ADD COLUMN IF NOT EXISTS default_weight_profile_id UUID;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'fk_campaign_default_weight_profile'
    ) THEN
        ALTER TABLE campaign
            ADD CONSTRAINT fk_campaign_default_weight_profile
                FOREIGN KEY (default_weight_profile_id)
                    REFERENCES weight_profile(id)
                    ON DELETE SET NULL;
    END IF;
END $$;

CREATE INDEX IF NOT EXISTS idx_campaign_default_weight_profile
    ON campaign(default_weight_profile_id);

ALTER TABLE survey_response
    ADD COLUMN IF NOT EXISTS weight_profile_id UUID;

ALTER TABLE survey_response
    ADD COLUMN IF NOT EXISTS weighted_total_score NUMERIC(10,4);

ALTER TABLE survey_response
    ADD COLUMN IF NOT EXISTS scored_at TIMESTAMP;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'fk_survey_response_weight_profile'
    ) THEN
        ALTER TABLE survey_response
            ADD CONSTRAINT fk_survey_response_weight_profile
                FOREIGN KEY (weight_profile_id)
                    REFERENCES weight_profile(id)
                    ON DELETE SET NULL;
    END IF;
END $$;

CREATE INDEX IF NOT EXISTS idx_survey_response_weight_profile
    ON survey_response(weight_profile_id);

