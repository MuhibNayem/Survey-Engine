-- =====================================================
-- Survey Engine — Response/Question Link Integrity Hardening
-- Enforces DB-level consistency for question/question_version linkage
-- =====================================================

-- 1) Support composite FK checks (question_version_id must belong to question_id)
ALTER TABLE question_version
    ADD CONSTRAINT uk_question_version_id_question_id UNIQUE (id, question_id);

-- 2) Repair survey_question linkage for draft/editable content:
--    if question_version_id is missing or mismatched, bind to latest version of question_id when available.
UPDATE survey_question sq
SET question_version_id = (
    SELECT qv.id
    FROM question_version qv
    WHERE qv.question_id = sq.question_id
    ORDER BY qv.version_number DESC
    LIMIT 1
)
WHERE (sq.question_version_id IS NULL
       OR NOT EXISTS (
           SELECT 1
           FROM question_version qvm
           WHERE qvm.id = sq.question_version_id
             AND qvm.question_id = sq.question_id
       ))
  AND EXISTS (
      SELECT 1
      FROM question_version qve
      WHERE qve.question_id = sq.question_id
  );

-- 3) Repair answer linkage conservatively:
--    keep question_id as source of truth and null out only invalid version links.
UPDATE answer a
SET question_version_id = NULL
WHERE a.question_version_id IS NOT NULL
  AND NOT EXISTS (
      SELECT 1
      FROM question_version qv
      WHERE qv.id = a.question_version_id
        AND qv.question_id = a.question_id
  );

-- 4) Remove duplicate answers for the same response/question pair.
WITH ranked AS (
    SELECT ctid,
           row_number() OVER (
               PARTITION BY survey_response_id, question_id
               ORDER BY id
           ) AS rn
    FROM answer
)
DELETE FROM answer a
USING ranked r
WHERE a.ctid = r.ctid
  AND r.rn > 1;

-- 5) Enforce one answer per question in a response.
ALTER TABLE answer
    ADD CONSTRAINT uk_answer_response_question UNIQUE (survey_response_id, question_id);

-- 6) Enforce composite linkage integrity for question_version <-> question.
ALTER TABLE survey_question
    ADD CONSTRAINT fk_survey_question_question_version_pair
        FOREIGN KEY (question_version_id, question_id)
            REFERENCES question_version (id, question_id)
            ON DELETE RESTRICT;

ALTER TABLE answer
    ADD CONSTRAINT fk_answer_question_version_pair
        FOREIGN KEY (question_version_id, question_id)
            REFERENCES question_version (id, question_id)
            ON DELETE RESTRICT;

-- 7) Indexes supporting composite lookups/FK checks.
CREATE INDEX IF NOT EXISTS idx_survey_question_qv_q
    ON survey_question (question_version_id, question_id);
CREATE INDEX IF NOT EXISTS idx_answer_qv_q
    ON answer (question_version_id, question_id);
