-- =====================================================
-- Global Search Foundations (PostgreSQL FTS + Trigram)
-- =====================================================

CREATE EXTENSION IF NOT EXISTS unaccent;
CREATE EXTENSION IF NOT EXISTS pg_trgm;

-- FTS expression indexes (tenant-scoped resources)
CREATE INDEX IF NOT EXISTS idx_survey_search_fts ON survey
USING gin (
  (
    setweight(to_tsvector('english', coalesce(title, '')), 'A') ||
    setweight(to_tsvector('english', coalesce(description, '')), 'B')
  )
);

CREATE INDEX IF NOT EXISTS idx_campaign_search_fts ON campaign
USING gin (
  (
    setweight(to_tsvector('english', coalesce(name, '')), 'A') ||
    setweight(to_tsvector('english', coalesce(description, '')), 'B')
  )
);

CREATE INDEX IF NOT EXISTS idx_question_search_fts ON question
USING gin (
  setweight(to_tsvector('english', coalesce(text, '')), 'A')
);

CREATE INDEX IF NOT EXISTS idx_category_search_fts ON category
USING gin (
  (
    setweight(to_tsvector('english', coalesce(name, '')), 'A') ||
    setweight(to_tsvector('english', coalesce(description, '')), 'B')
  )
);

-- Trigram indexes for fuzzy fallback matching
CREATE INDEX IF NOT EXISTS idx_survey_title_trgm
ON survey USING gin (lower(coalesce(title, '')) gin_trgm_ops);

CREATE INDEX IF NOT EXISTS idx_survey_description_trgm
ON survey USING gin (lower(coalesce(description, '')) gin_trgm_ops);

CREATE INDEX IF NOT EXISTS idx_campaign_name_trgm
ON campaign USING gin (lower(coalesce(name, '')) gin_trgm_ops);

CREATE INDEX IF NOT EXISTS idx_campaign_description_trgm
ON campaign USING gin (lower(coalesce(description, '')) gin_trgm_ops);

CREATE INDEX IF NOT EXISTS idx_question_text_trgm
ON question USING gin (lower(coalesce(text, '')) gin_trgm_ops);

CREATE INDEX IF NOT EXISTS idx_category_name_trgm
ON category USING gin (lower(coalesce(name, '')) gin_trgm_ops);

CREATE INDEX IF NOT EXISTS idx_category_description_trgm
ON category USING gin (lower(coalesce(description, '')) gin_trgm_ops);
