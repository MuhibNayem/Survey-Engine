CREATE TABLE responder_session (
    id UUID PRIMARY KEY,
    session_hash VARCHAR(128) NOT NULL UNIQUE,
    tenant_id VARCHAR(255) NOT NULL,
    campaign_id UUID NOT NULL,
    respondent_id VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    expires_at TIMESTAMPTZ NOT NULL,
    last_accessed_at TIMESTAMPTZ NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    revoked_at TIMESTAMPTZ
);

CREATE INDEX idx_responder_session_campaign ON responder_session (campaign_id);
CREATE INDEX idx_responder_session_expires_at ON responder_session (expires_at);
