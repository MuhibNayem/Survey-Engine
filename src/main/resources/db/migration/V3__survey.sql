-- =====================================================
-- Survey Engine — Survey Builder & Lifecycle
-- SRS §4.1, §4.6
-- =====================================================

-- Surveys
CREATE TABLE survey (
    id              UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
    title           VARCHAR(500)    NOT NULL,
    description     TEXT,
    lifecycle_state VARCHAR(30)     NOT NULL DEFAULT 'DRAFT',
    current_version INTEGER         NOT NULL DEFAULT 1,
    active          BOOLEAN         NOT NULL DEFAULT TRUE,
    created_by      VARCHAR(255)    NOT NULL,
    created_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_by      VARCHAR(255),
    updated_at      TIMESTAMP
);

CREATE INDEX idx_survey_lifecycle ON survey(lifecycle_state);

-- Survey Pages
CREATE TABLE survey_page (
    id              UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
    survey_id       UUID            NOT NULL REFERENCES survey(id) ON DELETE CASCADE,
    title           VARCHAR(500),
    sort_order      INTEGER         NOT NULL
);

CREATE INDEX idx_survey_page_survey ON survey_page(survey_id);

-- Survey Questions (placed on pages, reference bank questions)
CREATE TABLE survey_question (
    id                  UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
    page_id             UUID            NOT NULL REFERENCES survey_page(id) ON DELETE CASCADE,
    question_id         UUID            NOT NULL REFERENCES question(id),
    question_version_id UUID            REFERENCES question_version(id),
    category_id         UUID            REFERENCES category(id),
    sort_order          INTEGER         NOT NULL,
    mandatory           BOOLEAN         NOT NULL DEFAULT FALSE,
    answer_config       TEXT
);

CREATE INDEX idx_survey_question_page ON survey_question(page_id);
CREATE INDEX idx_survey_question_question ON survey_question(question_id);

-- Skip Logic Rules
CREATE TABLE skip_logic_rule (
    id                  UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
    survey_id           UUID            NOT NULL REFERENCES survey(id) ON DELETE CASCADE,
    source_question_id  UUID            NOT NULL REFERENCES question(id),
    condition_operator  VARCHAR(30)     NOT NULL,
    condition_value     VARCHAR(500)    NOT NULL,
    target_page_id      UUID            REFERENCES survey_page(id),
    target_question_id  UUID,
    sort_order          INTEGER         NOT NULL DEFAULT 0
);

CREATE INDEX idx_skip_logic_survey ON skip_logic_rule(survey_id);
CREATE INDEX idx_skip_logic_source ON skip_logic_rule(source_question_id);

-- Survey Snapshots (immutable frozen copies at publish time)
CREATE TABLE survey_snapshot (
    id              UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
    survey_id       UUID            NOT NULL REFERENCES survey(id),
    version_number  INTEGER         NOT NULL,
    snapshot_data   JSONB           NOT NULL,
    published_by    VARCHAR(255)    NOT NULL,
    published_at    TIMESTAMP       NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_survey_snapshot UNIQUE (survey_id, version_number)
);

CREATE INDEX idx_survey_snapshot_survey ON survey_snapshot(survey_id);
