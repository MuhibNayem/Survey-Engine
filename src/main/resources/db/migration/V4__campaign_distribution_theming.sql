-- =====================================================
-- Survey Engine — Campaign, Settings, Distribution & Theming
-- SRS §4.3–§4.5, §4.8, §4.4
-- =====================================================

-- Campaigns
CREATE TABLE campaign (
    id                  UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
    name                VARCHAR(500)    NOT NULL,
    description         TEXT,
    survey_id           UUID            NOT NULL REFERENCES survey(id),
    survey_snapshot_id  UUID            REFERENCES survey_snapshot(id),
    auth_mode           VARCHAR(30)     NOT NULL DEFAULT 'PUBLIC',
    status              VARCHAR(30)     NOT NULL DEFAULT 'DRAFT',
    active              BOOLEAN         NOT NULL DEFAULT TRUE,
    created_by          VARCHAR(255)    NOT NULL,
    created_at          TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_by          VARCHAR(255),
    updated_at          TIMESTAMP
);

CREATE INDEX idx_campaign_survey ON campaign(survey_id);
CREATE INDEX idx_campaign_status ON campaign(status);

-- Campaign Settings (one-to-one with campaign)
CREATE TABLE campaign_settings (
    id                          UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
    campaign_id                 UUID            NOT NULL UNIQUE REFERENCES campaign(id) ON DELETE CASCADE,
    password                    VARCHAR(255),
    captcha_enabled             BOOLEAN         NOT NULL DEFAULT FALSE,
    one_response_per_device     BOOLEAN         NOT NULL DEFAULT FALSE,
    ip_restriction_enabled      BOOLEAN         NOT NULL DEFAULT FALSE,
    email_restriction_enabled   BOOLEAN         NOT NULL DEFAULT FALSE,
    response_quota              INTEGER,
    close_date                  TIMESTAMP,
    session_timeout_minutes     INTEGER         DEFAULT 60,
    show_question_numbers       BOOLEAN         NOT NULL DEFAULT TRUE,
    show_progress_indicator     BOOLEAN         NOT NULL DEFAULT TRUE,
    allow_back_button           BOOLEAN         NOT NULL DEFAULT TRUE,
    start_message               TEXT,
    finish_message              TEXT,
    header_html                 TEXT,
    footer_html                 TEXT,
    collect_name                BOOLEAN         NOT NULL DEFAULT FALSE,
    collect_email               BOOLEAN         NOT NULL DEFAULT FALSE,
    collect_phone               BOOLEAN         NOT NULL DEFAULT FALSE,
    collect_address             BOOLEAN         NOT NULL DEFAULT FALSE
);

-- Distribution Channels
CREATE TABLE distribution_channel (
    id              UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
    campaign_id     UUID            NOT NULL REFERENCES campaign(id) ON DELETE CASCADE,
    channel_type    VARCHAR(30)     NOT NULL,
    channel_value   TEXT            NOT NULL,
    created_at      TIMESTAMP       NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_distribution_campaign ON distribution_channel(campaign_id);

-- Theme Templates (predefined + custom)
CREATE TABLE theme_template (
    id                  UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
    name                VARCHAR(255)    NOT NULL,
    primary_color       VARCHAR(7),
    secondary_color     VARCHAR(7),
    background_color    VARCHAR(7),
    font_family         VARCHAR(100),
    logo_url            VARCHAR(500),
    css_override        TEXT,
    is_system           BOOLEAN         NOT NULL DEFAULT TRUE
);

-- Seed predefined templates
INSERT INTO theme_template (name, primary_color, secondary_color, background_color, font_family, is_system) VALUES
    ('Modern Dark',     '#1a1a2e', '#16213e', '#0f3460', 'Inter',   TRUE),
    ('Classic Light',   '#ffffff', '#f0f0f0', '#f8f9fa', 'Georgia', TRUE),
    ('Ocean Blue',      '#0077b6', '#00b4d8', '#caf0f8', 'Roboto',  TRUE),
    ('Forest Green',    '#2d6a4f', '#40916c', '#d8f3dc', 'Lato',    TRUE),
    ('Sunset Warm',     '#e63946', '#f4a261', '#fdf0d5', 'Poppins', TRUE);

-- Survey Theme (per-campaign theme with overrides)
CREATE TABLE survey_theme (
    id                          UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
    campaign_id                 UUID            NOT NULL UNIQUE REFERENCES campaign(id) ON DELETE CASCADE,
    template_id                 UUID            REFERENCES theme_template(id),
    primary_color_override      VARCHAR(7),
    secondary_color_override    VARCHAR(7),
    background_color_override   VARCHAR(7),
    font_family_override        VARCHAR(100),
    logo_url_override           VARCHAR(500),
    css_override                TEXT
);
