-- =====================================================
-- Survey Engine — Plan Catalog & Quotas
-- =====================================================

CREATE TABLE IF NOT EXISTS plan_definition (
    id                          UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
    plan_code                   VARCHAR(30)     NOT NULL UNIQUE,
    display_name                VARCHAR(255)    NOT NULL,
    price                       NUMERIC(12,2)   NOT NULL,
    currency                    VARCHAR(10)     NOT NULL,
    billing_cycle_days          INTEGER         NOT NULL,
    trial_days                  INTEGER         NOT NULL,
    max_campaigns               INTEGER,
    max_responses_per_campaign  INTEGER,
    max_admin_users             INTEGER,
    active                      BOOLEAN         NOT NULL DEFAULT TRUE,
    created_by                  VARCHAR(255)    NOT NULL,
    created_at                  TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_by                  VARCHAR(255),
    updated_at                  TIMESTAMP,
    CONSTRAINT chk_plan_price_non_negative CHECK (price >= 0),
    CONSTRAINT chk_plan_billing_cycle_positive CHECK (billing_cycle_days > 0),
    CONSTRAINT chk_plan_trial_non_negative CHECK (trial_days >= 0),
    CONSTRAINT chk_plan_campaigns_positive CHECK (max_campaigns IS NULL OR max_campaigns > 0),
    CONSTRAINT chk_plan_responses_positive CHECK (max_responses_per_campaign IS NULL OR max_responses_per_campaign > 0),
    CONSTRAINT chk_plan_admin_users_positive CHECK (max_admin_users IS NULL OR max_admin_users > 0)
);

INSERT INTO plan_definition (
    plan_code, display_name, price, currency, billing_cycle_days, trial_days,
    max_campaigns, max_responses_per_campaign, max_admin_users, active, created_by
) VALUES
    ('BASIC', 'Basic', 29.00, 'USD', 30, 14, NULL, NULL, NULL, TRUE, 'system'),
    ('PRO', 'Pro', 99.00, 'USD', 30, 14, NULL, NULL, NULL, TRUE, 'system'),
    ('ENTERPRISE', 'Enterprise', 299.00, 'USD', 30, 14, NULL, NULL, NULL, TRUE, 'system')
ON CONFLICT (plan_code) DO NOTHING;
