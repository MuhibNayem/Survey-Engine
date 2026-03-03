-- =====================================================
-- Survey Engine — Constraint Fixes
-- Fixes identified in entity relationship audit
-- =====================================================

-- 1. Add ON DELETE CASCADE to category_question_mapping.category_version_id
--    Previously: FK without CASCADE — deleting a category_version would fail
--    if mappings exist. Adding CASCADE for defensive consistency.
ALTER TABLE category_question_mapping
    DROP CONSTRAINT IF EXISTS category_question_mapping_category_version_id_fkey;

ALTER TABLE category_question_mapping
    ADD CONSTRAINT category_question_mapping_category_version_id_fkey
    FOREIGN KEY (category_version_id) REFERENCES category_version(id) ON DELETE CASCADE;

-- 2. Add FK constraint on skip_logic_rule.target_question_id
--    Previously: column existed with no FK at all — referential integrity was
--    not enforced. Adding FK to question(id) for data integrity.
ALTER TABLE skip_logic_rule
    ADD CONSTRAINT fk_skip_logic_target_question
    FOREIGN KEY (target_question_id) REFERENCES question(id);
