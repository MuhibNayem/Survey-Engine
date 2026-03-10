-- ============================================================================
-- V28: Enterprise Feature Management System
-- ============================================================================
-- Complete feature flag, tour, and tooltip management system
-- Supports: feature definitions, tenant configs, user access tracking, analytics
-- ============================================================================

-- ----------------------------------------------------------------------------
-- 1. FEATURE DEFINITION TABLE
-- ----------------------------------------------------------------------------
-- Master registry of all features in the system
-- Stores feature metadata, rollout rules, and access constraints

CREATE TABLE IF NOT EXISTS feature_definition (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    feature_key VARCHAR(100) NOT NULL,
    feature_type VARCHAR(50) NOT NULL DEFAULT 'TOUR',
    category VARCHAR(100) NOT NULL DEFAULT 'GENERAL',
    name VARCHAR(255) NOT NULL,
    description TEXT,
    enabled BOOLEAN NOT NULL DEFAULT true,
    rollout_percentage INTEGER NOT NULL DEFAULT 100,
    min_plan VARCHAR(50) NOT NULL DEFAULT 'BASIC',
    roles JSONB NOT NULL DEFAULT '["ADMIN", "EDITOR", "VIEWER"]'::jsonb,
    platforms JSONB NOT NULL DEFAULT '["WEB", "MOBILE"]'::jsonb,
    metadata JSONB NOT NULL DEFAULT '{}'::jsonb,
    created_by VARCHAR(255) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_by VARCHAR(255),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),

    -- Ensure unique feature keys
    CONSTRAINT uk_feature_definition_feature_key UNIQUE (feature_key),

    -- Validate feature type
    CONSTRAINT chk_feature_definition_type CHECK (
        feature_type IN ('TOUR', 'TOOLTIP', 'BANNER', 'FEATURE_FLAG', 'ANNOUNCEMENT')
    ),

    -- Validate category
    CONSTRAINT chk_feature_definition_category CHECK (
        category IN ('GENERAL', 'ONBOARDING', 'DASHBOARD', 'SURVEYS', 'CAMPAIGNS', 'QUESTIONS', 'ANALYTICS', 'RESPONSES', 'SETTINGS', 'ADMIN')
    ),

    -- Validate rollout percentage
    CONSTRAINT chk_feature_definition_rollout CHECK (
        rollout_percentage >= 0 AND rollout_percentage <= 100
    ),

    -- Validate min plan
    CONSTRAINT chk_feature_definition_min_plan CHECK (
        min_plan IN ('BASIC', 'PRO', 'ENTERPRISE')
    )
);

-- Indexes for performance
CREATE INDEX IF NOT EXISTS idx_feature_definition_category ON feature_definition(category);
CREATE INDEX IF NOT EXISTS idx_feature_definition_enabled ON feature_definition(enabled);
CREATE INDEX IF NOT EXISTS idx_feature_definition_min_plan ON feature_definition(min_plan);
CREATE INDEX IF NOT EXISTS idx_feature_definition_type ON feature_definition(feature_type);
CREATE INDEX IF NOT EXISTS idx_feature_definition_metadata ON feature_definition USING GIN(metadata);

-- Comments
COMMENT ON TABLE feature_definition IS 'Master registry of all features including tours, tooltips, banners, and feature flags';
COMMENT ON COLUMN feature_definition.feature_key IS 'Unique identifier for the feature (e.g., "tour.dashboard", "tooltip.survey.create")';
COMMENT ON COLUMN feature_definition.feature_type IS 'Type of feature: TOUR, TOOLTIP, BANNER, FEATURE_FLAG, ANNOUNCEMENT';
COMMENT ON COLUMN feature_definition.category IS 'Functional category for grouping and filtering';
COMMENT ON COLUMN feature_definition.enabled IS 'Global enable/disable flag';
COMMENT ON COLUMN feature_definition.rollout_percentage IS 'Percentage of users who should see this feature (0-100)';
COMMENT ON COLUMN feature_definition.min_plan IS 'Minimum subscription plan required to access this feature';
COMMENT ON COLUMN feature_definition.roles IS 'JSON array of roles that can access this feature';
COMMENT ON COLUMN feature_definition.platforms IS 'JSON array of platforms where feature is available';
COMMENT ON COLUMN feature_definition.metadata IS 'Additional feature-specific metadata (steps, content, config)';

-- ----------------------------------------------------------------------------
-- 2. TENANT FEATURE CONFIGURATION TABLE
-- ----------------------------------------------------------------------------
-- Tenant-level overrides for feature behavior
-- Allows per-tenant customization of feature availability and settings

CREATE TABLE IF NOT EXISTS tenant_feature_config (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id VARCHAR(255) NOT NULL,
    feature_id UUID NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT true,
    rollout_percentage INTEGER,
    custom_metadata JSONB NOT NULL DEFAULT '{}'::jsonb,
    created_by VARCHAR(255) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_by VARCHAR(255),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),

    -- Foreign key to feature definition
    CONSTRAINT fk_tenant_feature_config_feature
        FOREIGN KEY (feature_id)
        REFERENCES feature_definition(id)
        ON DELETE CASCADE,

    -- Ensure one config per tenant per feature
    CONSTRAINT uk_tenant_feature_config_tenant_feature UNIQUE (tenant_id, feature_id),

    -- Validate rollout percentage
    CONSTRAINT chk_tenant_feature_config_rollout CHECK (
        rollout_percentage IS NULL OR (rollout_percentage >= 0 AND rollout_percentage <= 100)
    )
);

-- Indexes for performance
CREATE INDEX IF NOT EXISTS idx_tenant_feature_config_tenant_id ON tenant_feature_config(tenant_id);
CREATE INDEX IF NOT EXISTS idx_tenant_feature_config_feature_id ON tenant_feature_config(feature_id);
CREATE INDEX IF NOT EXISTS idx_tenant_feature_config_enabled ON tenant_feature_config(enabled);
CREATE INDEX IF NOT EXISTS idx_tenant_feature_config_custom_metadata ON tenant_feature_config USING GIN(custom_metadata);

-- Comments
COMMENT ON TABLE tenant_feature_config IS 'Tenant-level overrides for feature behavior and availability';
COMMENT ON COLUMN tenant_feature_config.tenant_id IS 'Tenant identifier';
COMMENT ON COLUMN tenant_feature_config.feature_id IS 'Reference to feature definition';
COMMENT ON COLUMN tenant_feature_config.enabled IS 'Tenant-specific enable/disable override';
COMMENT ON COLUMN tenant_feature_config.rollout_percentage IS 'Tenant-specific rollout percentage override';
COMMENT ON COLUMN tenant_feature_config.custom_metadata IS 'Tenant-specific metadata overrides';

-- ----------------------------------------------------------------------------
-- 3. USER FEATURE ACCESS TABLE
-- ----------------------------------------------------------------------------
-- Tracks user-level feature access and completion status
-- Used for tour progress, tooltip dismissals, and feature usage analytics

CREATE TABLE IF NOT EXISTS user_feature_access (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    tenant_id VARCHAR(255) NOT NULL,
    feature_id UUID NOT NULL,
    accessed BOOLEAN NOT NULL DEFAULT false,
    completed BOOLEAN NOT NULL DEFAULT false,
    access_count INTEGER NOT NULL DEFAULT 0,
    last_accessed_at TIMESTAMP WITH TIME ZONE,
    completed_at TIMESTAMP WITH TIME ZONE,
    metadata JSONB NOT NULL DEFAULT '{}'::jsonb,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),

    -- Foreign key to feature definition
    CONSTRAINT fk_user_feature_access_feature
        FOREIGN KEY (feature_id)
        REFERENCES feature_definition(id)
        ON DELETE CASCADE,

    -- Ensure one access record per user per feature
    CONSTRAINT uk_user_feature_access_user_feature UNIQUE (user_id, feature_id),

    -- Validate access count
    CONSTRAINT chk_user_feature_access_count CHECK (access_count >= 0)
);

-- Indexes for performance
CREATE INDEX IF NOT EXISTS idx_user_feature_access_user_id ON user_feature_access(user_id);
CREATE INDEX IF NOT EXISTS idx_user_feature_access_tenant_id ON user_feature_access(tenant_id);
CREATE INDEX IF NOT EXISTS idx_user_feature_access_feature_id ON user_feature_access(feature_id);
CREATE INDEX IF NOT EXISTS idx_user_feature_access_completed ON user_feature_access(completed);
CREATE INDEX IF NOT EXISTS idx_user_feature_access_accessed ON user_feature_access(accessed);
CREATE INDEX IF NOT EXISTS idx_user_feature_access_metadata ON user_feature_access USING GIN(metadata);
CREATE INDEX IF NOT EXISTS idx_user_feature_access_last_accessed ON user_feature_access(last_accessed_at);

-- Comments
COMMENT ON TABLE user_feature_access IS 'Tracks user-level feature access and completion status';
COMMENT ON COLUMN user_feature_access.user_id IS 'Reference to admin user';
COMMENT ON COLUMN user_feature_access.tenant_id IS 'Tenant identifier for scoping';
COMMENT ON COLUMN user_feature_access.feature_id IS 'Reference to feature definition';
COMMENT ON COLUMN user_feature_access.accessed IS 'Whether user has accessed/seen the feature';
COMMENT ON COLUMN user_feature_access.completed IS 'Whether user has completed/dismissed the feature';
COMMENT ON COLUMN user_feature_access.access_count IS 'Number of times user has accessed the feature';
COMMENT ON COLUMN user_feature_access.last_accessed_at IS 'Timestamp of last access';
COMMENT ON COLUMN user_feature_access.completed_at IS 'Timestamp when feature was completed';
COMMENT ON COLUMN user_feature_access.metadata IS 'Additional access-specific metadata';

-- ----------------------------------------------------------------------------
-- 4. SEED DATA: FEATURE DEFINITIONS
-- ----------------------------------------------------------------------------
-- Pre-populate with tour, tooltip, and feature flag definitions

-- Dashboard Tour
INSERT INTO feature_definition (feature_key, feature_type, category, name, description, enabled, rollout_percentage, min_plan, roles, platforms, metadata, created_by)
VALUES (
    'tour.dashboard',
    'TOUR',
    'DASHBOARD',
    'Dashboard Overview Tour',
    'Interactive tour introducing the main dashboard features and metrics',
    true,
    100,
    'BASIC',
    '["ADMIN", "EDITOR", "VIEWER"]'::jsonb,
    '["WEB"]'::jsonb,
    '{
        "steps": [
            {"id": "welcome", "title": "Welcome to Dashboard", "description": "Let us explore the main dashboard features", "targetSelector": "[data-tour=\"dashboard-header\"]"},
            {"id": "metrics", "title": "Key Metrics", "description": "View your campaign performance at a glance", "targetSelector": "[data-tour=\"dashboard-metrics\"]"},
            {"id": "charts", "title": "Analytics Charts", "description": "Visualize response trends over time", "targetSelector": "[data-tour=\"dashboard-charts\"]"},
            {"id": "quick-actions", "title": "Quick Actions", "description": "Create new campaigns and surveys quickly", "targetSelector": "[data-tour=\"dashboard-actions\"]"}
        ]
    }'::jsonb,
    'SYSTEM'
);

-- Surveys Tour
INSERT INTO feature_definition (feature_key, feature_type, category, name, description, enabled, rollout_percentage, min_plan, roles, platforms, metadata, created_by)
VALUES (
    'tour.surveys',
    'TOUR',
    'SURVEYS',
    'Surveys Management Tour',
    'Learn how to create and manage surveys effectively',
    true,
    100,
    'BASIC',
    '["ADMIN", "EDITOR"]'::jsonb,
    '["WEB"]'::jsonb,
    '{
        "steps": [
            {"id": "welcome", "title": "Surveys Overview", "description": "Manage all your surveys from here", "targetSelector": "[data-tour=\"surveys-header\"]"},
            {"id": "create", "title": "Create Survey", "description": "Click here to create a new survey", "targetSelector": "[data-tour=\"surveys-create\"]"},
            {"id": "list", "title": "Survey List", "description": "View and filter existing surveys", "targetSelector": "[data-tour=\"surveys-list\"]"},
            {"id": "actions", "title": "Survey Actions", "description": "Edit, duplicate, or delete surveys", "targetSelector": "[data-tour=\"surveys-actions\"]"}
        ]
    }'::jsonb,
    'SYSTEM'
);

-- Campaigns Tour
INSERT INTO feature_definition (feature_key, feature_type, category, name, description, enabled, rollout_percentage, min_plan, roles, platforms, metadata, created_by)
VALUES (
    'tour.campaigns',
    'TOUR',
    'CAMPAIGNS',
    'Campaigns Management Tour',
    'Master campaign creation and distribution',
    true,
    100,
    'BASIC',
    '["ADMIN", "EDITOR"]'::jsonb,
    '["WEB"]'::jsonb,
    '{
        "steps": [
            {"id": "welcome", "title": "Campaigns Overview", "description": "Launch and track your survey campaigns", "targetSelector": "[data-tour=\"campaigns-header\"]"},
            {"id": "create", "title": "Create Campaign", "description": "Start a new campaign", "targetSelector": "[data-tour=\"campaigns-create\"]"},
            {"id": "settings", "title": "Campaign Settings", "description": "Configure distribution and targeting", "targetSelector": "[data-tour=\"campaigns-settings\"]"},
            {"id": "analytics", "title": "Campaign Analytics", "description": "Track responses and engagement", "targetSelector": "[data-tour=\"campaigns-analytics\"]"}
        ]
    }'::jsonb,
    'SYSTEM'
);

-- Questions Tour
INSERT INTO feature_definition (feature_key, feature_type, category, name, description, enabled, rollout_percentage, min_plan, roles, platforms, metadata, created_by)
VALUES (
    'tour.questions',
    'TOUR',
    'QUESTIONS',
    'Question Bank Tour',
    'Learn to use the question bank effectively',
    true,
    100,
    'BASIC',
    '["ADMIN", "EDITOR"]'::jsonb,
    '["WEB"]'::jsonb,
    '{
        "steps": [
            {"id": "welcome", "title": "Question Bank", "description": "Reusable questions for your surveys", "targetSelector": "[data-tour=\"questions-header\"]"},
            {"id": "create", "title": "Add Question", "description": "Create new questions", "targetSelector": "[data-tour=\"questions-create\"]"},
            {"id": "categories", "title": "Categories", "description": "Organize questions by category", "targetSelector": "[data-tour=\"questions-categories\"]"},
            {"id": "search", "title": "Search Questions", "description": "Find questions quickly", "targetSelector": "[data-tour=\"questions-search\"]"}
        ]
    }'::jsonb,
    'SYSTEM'
);

-- Analytics Tour
INSERT INTO feature_definition (feature_key, feature_type, category, name, description, enabled, rollout_percentage, min_plan, roles, platforms, metadata, created_by)
VALUES (
    'tour.analytics',
    'TOUR',
    'ANALYTICS',
    'Analytics Dashboard Tour',
    'Explore advanced analytics and reporting features',
    true,
    100,
    'PRO',
    '["ADMIN", "EDITOR", "VIEWER"]'::jsonb,
    '["WEB"]'::jsonb,
    '{
        "steps": [
            {"id": "welcome", "title": "Analytics Overview", "description": "Deep dive into your data", "targetSelector": "[data-tour=\"analytics-header\"]"},
            {"id": "filters", "title": "Data Filters", "description": "Filter and segment responses", "targetSelector": "[data-tour=\"analytics-filters\"]"},
            {"id": "exports", "title": "Export Options", "description": "Download reports in various formats", "targetSelector": "[data-tour=\"analytics-exports\"]"},
            {"id": "insights", "title": "AI Insights", "description": "Get automated insights from your data", "targetSelector": "[data-tour=\"analytics-insights\"]"}
        ]
    }'::jsonb,
    'SYSTEM'
);

-- Common Tooltips
INSERT INTO feature_definition (feature_key, feature_type, category, name, description, enabled, rollout_percentage, min_plan, roles, platforms, metadata, created_by)
VALUES 
(
    'tooltip.survey.create',
    'TOOLTIP',
    'SURVEYS',
    'Create Survey Tooltip',
    'Quick tip for creating surveys',
    true,
    100,
    'BASIC',
    '["ADMIN", "EDITOR"]'::jsonb,
    '["WEB"]'::jsonb,
    '{
        "title": "Quick Survey Creation",
        "content": "Click here to create a new survey. You can also use Ctrl+N as a keyboard shortcut.",
        "targetSelector": "[data-tooltip=\"survey-create\"]",
        "placement": "bottom"
    }'::jsonb,
    'SYSTEM'
),
(
    'tooltip.campaign.distribute',
    'TOOLTIP',
    'CAMPAIGNS',
    'Campaign Distribution Tooltip',
    'Tips for distributing campaigns',
    true,
    100,
    'BASIC',
    '["ADMIN", "EDITOR"]'::jsonb,
    '["WEB"]'::jsonb,
    '{
        "title": "Distribution Options",
        "content": "Share your campaign via email, link, QR code, or embed it on your website.",
        "targetSelector": "[data-tooltip=\"campaign-distribute\"]",
        "placement": "top"
    }'::jsonb,
    'SYSTEM'
),
(
    'tooltip.analytics.export',
    'TOOLTIP',
    'ANALYTICS',
    'Analytics Export Tooltip',
    'How to export analytics data',
    true,
    100,
    'PRO',
    '["ADMIN", "EDITOR", "VIEWER"]'::jsonb,
    '["WEB"]'::jsonb,
    '{
        "title": "Export Your Data",
        "content": "Export analytics as PDF, CSV, or Excel. PRO and ENTERPRISE plans support scheduled exports.",
        "targetSelector": "[data-tooltip=\"analytics-export\"]",
        "placement": "left"
    }'::jsonb,
    'SYSTEM'
),
(
    'tooltip.responses.filter',
    'TOOLTIP',
    'RESPONSES',
    'Response Filtering Tooltip',
    'Tips for filtering responses',
    true,
    100,
    'BASIC',
    '["ADMIN", "EDITOR", "VIEWER"]'::jsonb,
    '["WEB"]'::jsonb,
    '{
        "title": "Filter Responses",
        "content": "Use filters to find specific responses. Filter by date, score, completion status, and more.",
        "targetSelector": "[data-tooltip=\"responses-filter\"]",
        "placement": "right"
    }'::jsonb,
    'SYSTEM'
);

-- Feature Flags (PRO/Enterprise features)
INSERT INTO feature_definition (feature_key, feature_type, category, name, description, enabled, rollout_percentage, min_plan, roles, platforms, metadata, created_by)
VALUES 
(
    'feature.advanced_scoring',
    'FEATURE_FLAG',
    'ANALYTICS',
    'Advanced Scoring Engine',
    'Weighted scoring profiles and custom scoring rules',
    true,
    100,
    'PRO',
    '["ADMIN", "EDITOR"]'::jsonb,
    '["WEB"]'::jsonb,
    '{
        "description": "Enable advanced scoring with weight profiles",
        "requiresSetup": true,
        "setupGuide": "/docs/scoring"
    }'::jsonb,
    'SYSTEM'
),
(
    'feature.sso_integration',
    'FEATURE_FLAG',
    'SETTINGS',
    'SSO Integration',
    'Single Sign-On with SAML/OIDC providers',
    true,
    100,
    'ENTERPRISE',
    '["ADMIN"]'::jsonb,
    '["WEB"]'::jsonb,
    '{
        "description": "Configure SSO with your identity provider",
        "requiresSetup": true,
        "setupGuide": "/docs/sso",
        "supportedProviders": ["SAML", "OIDC", "Azure AD", "Okta"]
    }'::jsonb,
    'SYSTEM'
),
(
    'feature.api_access',
    'FEATURE_FLAG',
    'SETTINGS',
    'API Access',
    'REST API access for integrations',
    true,
    100,
    'PRO',
    '["ADMIN"]'::jsonb,
    '["WEB"]'::jsonb,
    '{
        "description": "Generate API keys for programmatic access",
        "requiresSetup": false,
        "documentation": "/docs/api"
    }'::jsonb,
    'SYSTEM'
),
(
    'feature.custom_branding',
    'FEATURE_FLAG',
    'SETTINGS',
    'Custom Branding',
    'White-label surveys with your brand',
    true,
    100,
    'ENTERPRISE',
    '["ADMIN"]'::jsonb,
    '["WEB"]'::jsonb,
    '{
        "description": "Customize colors, logos, and domains",
        "requiresSetup": true,
        "setupGuide": "/docs/branding"
    }'::jsonb,
    'SYSTEM'
),
(
    'feature.scheduled_exports',
    'FEATURE_FLAG',
    'ANALYTICS',
    'Scheduled Exports',
    'Automated report delivery',
    true,
    100,
    'PRO',
    '["ADMIN", "EDITOR"]'::jsonb,
    '["WEB"]'::jsonb,
    '{
        "description": "Schedule automatic report exports via email",
        "requiresSetup": true,
        "frequencies": ["daily", "weekly", "monthly"]
    }'::jsonb,
    'SYSTEM'
);

-- Announcements
INSERT INTO feature_definition (feature_key, feature_type, category, name, description, enabled, rollout_percentage, min_plan, roles, platforms, metadata, created_by)
VALUES
(
    'announcement.new_year_2026',
    'ANNOUNCEMENT',
    'GENERAL',
    'New Year Update 2026',
    'Exciting new features for 2026',
    true,
    100,
    'BASIC',
    '["ADMIN", "EDITOR", "VIEWER"]'::jsonb,
    '["WEB", "MOBILE"]'::jsonb,
    '{
        "title": "Welcome to 2026!",
        "content": "We have launched new analytics features, improved performance, and added AI-powered insights.",
        "ctaText": "Learn More",
        "ctaUrl": "/docs/whats-new",
        "priority": "high",
        "dismissible": true,
        "startDate": "2026-01-01T00:00:00Z",
        "endDate": "2026-01-31T23:59:59Z"
    }'::jsonb,
    'SYSTEM'
);

-- ----------------------------------------------------------------------------
-- 5. MAP EXISTING PLAN FEATURES TO NEW FEATURE DEFINITIONS
-- ----------------------------------------------------------------------------
-- Link existing plan_feature records to new feature_definition records

-- Create mapping for features that already exist in plan_feature
-- Map old category names to new standardized category values
INSERT INTO feature_definition (feature_key, feature_type, category, name, description, enabled, rollout_percentage, min_plan, roles, platforms, metadata, created_by)
SELECT
    LOWER(REPLACE(pf.feature_code, '_', '.')) as feature_key,
    'FEATURE_FLAG' as feature_type,
    CASE 
        WHEN pf.category LIKE '%Survey%' OR pf.category LIKE '%Content%' THEN 'SURVEYS'
        WHEN pf.category LIKE '%Campaign%' OR pf.category LIKE '%Distribution%' THEN 'CAMPAIGNS'
        WHEN pf.category LIKE '%Auth%' OR pf.category LIKE '%SSO%' THEN 'SETTINGS'
        WHEN pf.category LIKE '%Analytics%' OR pf.category LIKE '%Scoring%' THEN 'ANALYTICS'
        WHEN pf.category LIKE '%API%' OR pf.category LIKE '%Developer%' THEN 'SETTINGS'
        WHEN pf.category LIKE '%Support%' THEN 'GENERAL'
        ELSE 'GENERAL'
    END as category,
    pf.name,
    pf.description,
    true as enabled,
    100 as rollout_percentage,
    'BASIC' as min_plan,
    '["ADMIN", "EDITOR", "VIEWER"]'::jsonb as roles,
    '["WEB"]'::jsonb as platforms,
    jsonb_build_object('legacyFeatureId', pf.id::text, 'displayOrder', pf.display_order) as metadata,
    'SYSTEM' as created_by
FROM plan_feature pf
WHERE NOT EXISTS (
    SELECT 1 FROM feature_definition fd
    WHERE fd.metadata->>'legacyFeatureId' = pf.id::text
);

-- ----------------------------------------------------------------------------
-- 6. CREATE VIEW FOR FEATURE ANALYTICS
-- ----------------------------------------------------------------------------
-- Simplified query for feature usage analytics

CREATE OR REPLACE VIEW feature_analytics_summary AS
SELECT 
    fd.id as feature_id,
    fd.feature_key,
    fd.name as feature_name,
    fd.feature_type,
    fd.category,
    fd.min_plan,
    COUNT(DISTINCT ufa.user_id) FILTER (WHERE ufa.accessed = true) as total_accessed,
    COUNT(DISTINCT ufa.user_id) FILTER (WHERE ufa.completed = true) as total_completed,
    COUNT(DISTINCT ufa.user_id) FILTER (WHERE ufa.accessed = false) as total_not_accessed,
    AVG(ufa.access_count) as avg_access_count,
    MIN(ufa.last_accessed_at) as first_accessed_at,
    MAX(ufa.last_accessed_at) as last_accessed_at,
    COUNT(DISTINCT ufa.tenant_id) as unique_tenants
FROM feature_definition fd
LEFT JOIN user_feature_access ufa ON fd.id = ufa.feature_id
GROUP BY fd.id, fd.feature_key, fd.name, fd.feature_type, fd.category, fd.min_plan;

COMMENT ON VIEW feature_analytics_summary IS 'Aggregated analytics for feature usage and completion';

-- ----------------------------------------------------------------------------
-- 7. CREATE INDEX FOR FEATURE STATUS LOOKUP
-- ----------------------------------------------------------------------------
-- Optimize common queries for feature status checks

CREATE INDEX IF NOT EXISTS idx_user_feature_access_lookup 
ON user_feature_access(user_id, feature_id, completed, accessed);

-- ============================================================================
-- END OF MIGRATION V28
-- ============================================================================
