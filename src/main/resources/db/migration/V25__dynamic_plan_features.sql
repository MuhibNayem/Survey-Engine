-- V25: Add dynamic plan features support

CREATE TABLE IF NOT EXISTS plan_feature (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    feature_code  VARCHAR(100) NOT NULL UNIQUE,
    category      VARCHAR(100) NOT NULL,
    name          VARCHAR(255) NOT NULL,
    description   VARCHAR(500),
    display_order INTEGER      NOT NULL,
    created_by    VARCHAR(255) NOT NULL DEFAULT 'system',
    created_at    TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_by    VARCHAR(255),
    updated_at    TIMESTAMP
);

CREATE TABLE IF NOT EXISTS plan_definition_features (
    plan_definition_id UUID NOT NULL REFERENCES plan_definition(id) ON DELETE CASCADE,
    feature_id         UUID NOT NULL REFERENCES plan_feature(id) ON DELETE CASCADE,
    PRIMARY KEY (plan_definition_id, feature_id)
);

-- Insert standard Survey Engine features grouped by category
INSERT INTO plan_feature (feature_code, category, name, description, display_order) VALUES
-- Survey & Content
('FB_QUESTION_BANK_CRUD', 'Survey & Content', 'Question Bank (CRUD + Versioning)', 'Create, read, update, delete, and version questions within your internal question bank.', 10),
('FB_WEIGHTED_MAPPINGS', 'Survey & Content', 'Categories with Weighted Mappings', 'Organize questions into specific categories and attach mapped weightings to answers.', 20),
('FB_MULTIPAGE_BUILDER', 'Survey & Content', 'Multi-Page Survey Builder', 'Construct complex surveys using a multi-page routing framework.', 30),
('FB_SURVEY_SNAPSHOTS', 'Survey & Content', 'Survey Snapshots on Publish', 'Create locked, immutable snapshots whenever a survey is published for distribution.', 40),
('FT_CUSTOM_BRANDING', 'Survey & Content', 'Custom Branding & Themes', 'Style your surveys and distribution portals with extensive custom CSS and branding definitions.', 50),

-- Campaigns & Distribution
('FB_PUBLIC_CAMPAIGNS', 'Campaigns & Distribution', 'Public Campaigns', 'Distribute your survey via public links that anyone can answer.', 60),
('FT_PRIVATE_CAMPAIGNS', 'Campaigns & Distribution', 'Private Campaigns (Token Auth)', 'Lock down surveys requiring uniquely generated, signed demographic tokens.', 70),
('FB_6_DISTRIBUTION_CHANNELS', 'Campaigns & Distribution', '6 Distribution Channels', 'Deliver via Link, Email, SMS, QR Code, Web Embed, and Social integrations.', 80),
('FB_QUOTAS_IP_RESTRICTIONS', 'Campaigns & Distribution', 'Response Quotas & IP Restrictions', 'Set hard limits on total responses and block distinct IP ranges.', 90),
('FT_DEVICE_FINGERPRINT', 'Campaigns & Distribution', 'Device Fingerprint Dedup', 'Leverage advanced client-side fingerprinting heuristics to prevent duplicate answers.', 100),

-- Authentication
('FB_ADMIN_JWT_AUTH', 'Authentication', 'Admin JWT Auth', 'Secure administrative dashboard using standard JWT flows.', 110),
('FT_OIDC_PKCE_RESPONDENT', 'Authentication', 'OIDC / PKCE Respondent Auth', 'Support OIDC protocols like PKCE and implicit auth on the respondent portal.', 120),
('FT_CUSTOM_SSO', 'Authentication', 'Custom SSO Integration', 'Integrate enterprise identity providers such as Entra ID, Auth0, or Okta (SAML/OIDC).', 130),
('FT_AUTH_PROVIDER_TEMPLATES', 'Authentication', 'Auth Provider Templates (Okta, Auth0)', 'Pre-built provisioning templates for leading Identity Providers.', 140),

-- Analytics & Scoring
('FB_BASIC_CAMPAIGN_ANALYTICS', 'Analytics & Scoring', 'Basic Campaign Analytics', 'View top-line metric tracking and basic completion percentage data.', 150),
('FT_WEIGHTED_SCORING', 'Analytics & Scoring', 'Weighted Scoring Engine', 'Aggregate survey response metrics through advanced, configurable weight profiles.', 160),
('FB_RESPONSE_LOCKING_AUDIT', 'Analytics & Scoring', 'Response Locking & Reopen Audit', 'Immutable capture of submitted surveys with detailed audit logs for overrides.', 170),

-- Support
('FB_EMAIL_SUPPORT', 'Support', 'Email Support', 'Access our basic email support ticketing team.', 180),
('FP_PRIORITY_SUPPORT', 'Support', 'Priority Support', 'Jump the queue with expedited, prioritized ticketing responses.', 190),
('FE_DEDICATED_ACCOUNT_MANAGER', 'Support', 'Dedicated Account Manager', 'Receive a primary contact for implementation, troubleshooting, and best practices.', 200),
('FE_SLA_GUARANTEE', 'Support', 'SLA Guarantee', 'Financially-backed 99.9% uptime guaranteed in your contract terms.', 210),

-- API Access
('FT_API_ACCESS', 'Developer API', 'REST API Access', 'Programmatically interact with the Survey Engine infrastructure.', 220);


-- Map Features to the BASIC Plan
INSERT INTO plan_definition_features (plan_definition_id, feature_id)
SELECT pd.id, pf.id
FROM plan_definition pd, plan_feature pf
WHERE pd.plan_code = 'BASIC'
  AND pf.feature_code IN (
    'FB_QUESTION_BANK_CRUD',
    'FB_WEIGHTED_MAPPINGS',
    'FB_MULTIPAGE_BUILDER',
    'FB_SURVEY_SNAPSHOTS',
    'FB_PUBLIC_CAMPAIGNS',
    'FB_6_DISTRIBUTION_CHANNELS',
    'FB_QUOTAS_IP_RESTRICTIONS',
    'FB_ADMIN_JWT_AUTH',
    'FB_BASIC_CAMPAIGN_ANALYTICS',
    'FB_RESPONSE_LOCKING_AUDIT',
    'FB_EMAIL_SUPPORT'
  );

-- Map Features to the PRO Plan (Basic + Pro Specific)
INSERT INTO plan_definition_features (plan_definition_id, feature_id)
SELECT pd.id, pf.id
FROM plan_definition pd, plan_feature pf
WHERE pd.plan_code = 'PRO'
  AND pf.feature_code IN (
    'FB_QUESTION_BANK_CRUD',
    'FB_WEIGHTED_MAPPINGS',
    'FB_MULTIPAGE_BUILDER',
    'FB_SURVEY_SNAPSHOTS',
    'FT_CUSTOM_BRANDING',
    'FB_PUBLIC_CAMPAIGNS',
    'FT_PRIVATE_CAMPAIGNS',
    'FB_6_DISTRIBUTION_CHANNELS',
    'FB_QUOTAS_IP_RESTRICTIONS',
    'FT_DEVICE_FINGERPRINT',
    'FB_ADMIN_JWT_AUTH',
    'FT_OIDC_PKCE_RESPONDENT',
    'FT_AUTH_PROVIDER_TEMPLATES',
    'FB_BASIC_CAMPAIGN_ANALYTICS',
    'FT_WEIGHTED_SCORING',
    'FB_RESPONSE_LOCKING_AUDIT',
    'FB_EMAIL_SUPPORT',
    'FP_PRIORITY_SUPPORT'
  );

-- Map Features to the ENTERPRISE Plan (All Features)
INSERT INTO plan_definition_features (plan_definition_id, feature_id)
SELECT pd.id, pf.id
FROM plan_definition pd, plan_feature pf
WHERE pd.plan_code = 'ENTERPRISE';

