ALTER TABLE survey_theme
    ADD COLUMN IF NOT EXISTS theme_config TEXT;
