-- V23: Align audit_log schema with AuditLog entity fields.
-- Some environments were created before tenant/ip fields were introduced.

ALTER TABLE audit_log
    ADD COLUMN IF NOT EXISTS tenant_id VARCHAR(255),
    ADD COLUMN IF NOT EXISTS ip_address VARCHAR(45);

CREATE INDEX IF NOT EXISTS idx_audit_log_tenant ON audit_log (tenant_id);
