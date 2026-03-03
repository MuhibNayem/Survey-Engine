-- =====================================================
-- Survey Engine — Baseline Migration
-- Creates the audit_log table for business event auditing
-- =====================================================

-- Audit Log: captures all auditable business events
CREATE TABLE audit_log (
    id              BIGSERIAL       PRIMARY KEY,
    entity_type     VARCHAR(100)    NOT NULL,
    entity_id       VARCHAR(255)    NOT NULL,
    action          VARCHAR(50)     NOT NULL,
    actor           VARCHAR(255)    NOT NULL,
    reason          TEXT,
    before_value    JSONB,
    after_value     JSONB,
    created_at      TIMESTAMP       NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_audit_log_entity ON audit_log (entity_type, entity_id);
CREATE INDEX idx_audit_log_actor ON audit_log (actor);
CREATE INDEX idx_audit_log_created_at ON audit_log (created_at);
