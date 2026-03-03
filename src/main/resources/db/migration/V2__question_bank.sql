-- =====================================================
-- Survey Engine — Question Bank & Category Model
-- SRS §4.1, §4.2
-- =====================================================

-- Questions
CREATE TABLE question (
    id              UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
    text            TEXT            NOT NULL,
    type            VARCHAR(30)     NOT NULL,
    max_score       NUMERIC(10,2)   NOT NULL,
    current_version INTEGER         NOT NULL DEFAULT 1,
    active          BOOLEAN         NOT NULL DEFAULT TRUE,
    created_by      VARCHAR(255)    NOT NULL,
    created_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_by      VARCHAR(255),
    updated_at      TIMESTAMP
);

-- Question Versions (immutable snapshots)
CREATE TABLE question_version (
    id              UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
    question_id     UUID            NOT NULL REFERENCES question(id),
    version_number  INTEGER         NOT NULL,
    text            TEXT            NOT NULL,
    type            VARCHAR(30)     NOT NULL,
    max_score       NUMERIC(10,2)   NOT NULL,
    created_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_question_version UNIQUE (question_id, version_number)
);

CREATE INDEX idx_question_version_question ON question_version(question_id);

-- Categories
CREATE TABLE category (
    id              UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
    name            VARCHAR(255)    NOT NULL,
    description     TEXT,
    current_version INTEGER         NOT NULL DEFAULT 1,
    active          BOOLEAN         NOT NULL DEFAULT TRUE,
    created_by      VARCHAR(255)    NOT NULL,
    created_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_by      VARCHAR(255),
    updated_at      TIMESTAMP
);

-- Category Versions (immutable snapshots)
CREATE TABLE category_version (
    id              UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
    category_id     UUID            NOT NULL REFERENCES category(id),
    version_number  INTEGER         NOT NULL,
    name            VARCHAR(255)    NOT NULL,
    description     TEXT,
    created_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_category_version UNIQUE (category_id, version_number)
);

CREATE INDEX idx_category_version_category ON category_version(category_id);

-- Category-Question Mapping (ties versioned questions to versioned categories)
CREATE TABLE category_question_mapping (
    id                   UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
    category_version_id  UUID            NOT NULL REFERENCES category_version(id),
    question_id          UUID            NOT NULL REFERENCES question(id),
    question_version_id  UUID            NOT NULL REFERENCES question_version(id),
    sort_order           INTEGER         NOT NULL,
    weight               NUMERIC(5,2)
);

CREATE INDEX idx_cqm_category_version ON category_question_mapping(category_version_id);
CREATE INDEX idx_cqm_question ON category_question_mapping(question_id);
