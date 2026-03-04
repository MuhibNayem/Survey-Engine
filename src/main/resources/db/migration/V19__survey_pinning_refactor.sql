-- =====================================================
-- Survey Engine — Survey Pinning Refactor
-- - Question options live in question/question_version
-- - Survey questions can point to pinned category versions
-- =====================================================

ALTER TABLE question
    ADD COLUMN IF NOT EXISTS option_config TEXT;

ALTER TABLE question_version
    ADD COLUMN IF NOT EXISTS option_config TEXT;

ALTER TABLE survey_question
    ADD COLUMN IF NOT EXISTS category_version_id UUID;

UPDATE survey_question sq
SET category_version_id = (
    SELECT cv.id
    FROM category_version cv
    WHERE cv.category_id = sq.category_id
    ORDER BY cv.version_number DESC
    LIMIT 1
)
WHERE sq.category_id IS NOT NULL
  AND sq.category_version_id IS NULL;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'fk_survey_question_category_version'
    ) THEN
        ALTER TABLE survey_question
            ADD CONSTRAINT fk_survey_question_category_version
                FOREIGN KEY (category_version_id)
                    REFERENCES category_version(id)
                    ON DELETE RESTRICT;
    END IF;
END $$;

CREATE INDEX IF NOT EXISTS idx_survey_question_category_version
    ON survey_question(category_version_id);
