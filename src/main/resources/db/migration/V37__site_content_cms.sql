CREATE TABLE IF NOT EXISTS site_content_page (
    id                  UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
    page_key            VARCHAR(50)     NOT NULL UNIQUE,
    draft_content       TEXT            NOT NULL DEFAULT '{}',
    published_content   TEXT,
    published           BOOLEAN         NOT NULL DEFAULT FALSE,
    published_at        TIMESTAMP,
    active              BOOLEAN         NOT NULL DEFAULT TRUE,
    created_by          VARCHAR(255)    NOT NULL,
    created_at          TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_by          VARCHAR(255),
    updated_at          TIMESTAMP
);

INSERT INTO site_content_page (page_key, draft_content, published_content, published, published_at, active, created_by)
VALUES
    ('HOME', '{}', NULL, FALSE, NULL, TRUE, 'system'),
    ('PRICING', '{}', NULL, FALSE, NULL, TRUE, 'system')
ON CONFLICT (page_key) DO NOTHING;
