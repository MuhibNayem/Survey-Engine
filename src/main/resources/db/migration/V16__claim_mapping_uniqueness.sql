-- =====================================================
-- Survey Engine — Claim mapping hardening
-- =====================================================

CREATE UNIQUE INDEX IF NOT EXISTS uq_claim_mapping_profile_internal
    ON claim_mapping(auth_profile_id, internal_field);
