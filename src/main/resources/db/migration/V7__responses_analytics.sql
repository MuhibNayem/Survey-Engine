-- =====================================================
-- Survey Engine — Response Collection, Locking & Analytics
-- SRS §4.7, §8
-- =====================================================

-- Survey Responses
CREATE TABLE survey_response (
    id                          UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
    campaign_id                 UUID            NOT NULL REFERENCES campaign(id),
    survey_snapshot_id          UUID            NOT NULL REFERENCES survey_snapshot(id),
    respondent_identifier       VARCHAR(500),
    respondent_ip               VARCHAR(45),
    respondent_device_fingerprint VARCHAR(500),
    status                      VARCHAR(30)     NOT NULL DEFAULT 'IN_PROGRESS',
    started_at                  TIMESTAMP       NOT NULL DEFAULT NOW(),
    submitted_at                TIMESTAMP,
    locked_at                   TIMESTAMP
);

CREATE INDEX idx_response_campaign ON survey_response(campaign_id);
CREATE INDEX idx_response_status ON survey_response(status);
CREATE INDEX idx_response_respondent ON survey_response(respondent_identifier);
CREATE INDEX idx_response_ip ON survey_response(respondent_ip);

-- Answers (per question within a response)
CREATE TABLE answer (
    id                  UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
    survey_response_id  UUID            NOT NULL REFERENCES survey_response(id) ON DELETE CASCADE,
    question_id         UUID            NOT NULL REFERENCES question(id),
    question_version_id UUID            REFERENCES question_version(id),
    value               TEXT,
    score               NUMERIC(10,2)
);

CREATE INDEX idx_answer_response ON answer(survey_response_id);
CREATE INDEX idx_answer_question ON answer(question_id);

-- Reopen Audit (SRS §8 — full reopen history)
CREATE TABLE reopen_audit (
    id                      UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
    survey_response_id      UUID            NOT NULL REFERENCES survey_response(id),
    reopened_by             VARCHAR(255)    NOT NULL,
    reason                  TEXT            NOT NULL,
    reopened_at             TIMESTAMP       NOT NULL DEFAULT NOW(),
    reopen_window_minutes   INTEGER
);

CREATE INDEX idx_reopen_audit_response ON reopen_audit(survey_response_id);
