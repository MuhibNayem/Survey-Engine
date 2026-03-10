-- Add tour seed data for feature management
-- This migration adds onboarding tours for dashboard, surveys, campaigns, and questions

INSERT INTO feature_definition (feature_key, feature_type, category, name, description, enabled, rollout_percentage, min_plan, roles, platforms, metadata, created_by)
VALUES
(
    'tour.dashboard',
    'TOUR',
    'ONBOARDING',
    'Dashboard Tour',
    'Learn your way around the dashboard',
    true,
    100,
    'BASIC',
    '["ADMIN", "EDITOR", "VIEWER"]'::jsonb,
    '["WEB"]'::jsonb,
    '{
        "title": "Dashboard Overview",
        "steps": [
            {
                "id": "step1",
                "title": "Welcome to Your Dashboard",
                "description": "This is your command center for all survey activities.",
                "targetSelector": "[data-tour=\"dashboard-metrics\"]",
                "placement": "bottom"
            },
            {
                "id": "step2",
                "title": "Recent Campaigns",
                "description": "See your active and recent campaigns here.",
                "targetSelector": "[data-tour=\"recent-campaigns\"]",
                "placement": "top"
            },
            {
                "id": "step3",
                "title": "Quick Actions",
                "description": "Create new surveys, campaigns, or view reports.",
                "targetSelector": "[data-tour=\"quick-actions\"]",
                "placement": "left"
            }
        ]
    }'::jsonb,
    'SYSTEM'
)
ON CONFLICT (feature_key) DO NOTHING;

INSERT INTO feature_definition (feature_key, feature_type, category, name, description, enabled, rollout_percentage, min_plan, roles, platforms, metadata, created_by)
VALUES
(
    'tour.surveys',
    'TOUR',
    'ONBOARDING',
    'Surveys Tour',
    'Learn how to create and manage surveys',
    true,
    100,
    'BASIC',
    '["ADMIN", "EDITOR", "VIEWER"]'::jsonb,
    '["WEB"]'::jsonb,
    '{
        "title": "Survey Management",
        "steps": [
            {
                "id": "step1",
                "title": "Create New Survey",
                "description": "Click here to start building your survey.",
                "targetSelector": "[data-tour=\"new-survey-btn\"]",
                "placement": "right"
            },
            {
                "id": "step2",
                "title": "Survey List",
                "description": "View and manage all your surveys.",
                "targetSelector": "[data-tour=\"survey-list\"]",
                "placement": "bottom"
            }
        ]
    }'::jsonb,
    'SYSTEM'
)
ON CONFLICT (feature_key) DO NOTHING;

INSERT INTO feature_definition (feature_key, feature_type, category, name, description, enabled, rollout_percentage, min_plan, roles, platforms, metadata, created_by)
VALUES
(
    'tour.campaigns',
    'TOUR',
    'ONBOARDING',
    'Campaigns Tour',
    'Learn how to set up and launch campaigns',
    true,
    100,
    'BASIC',
    '["ADMIN", "EDITOR", "VIEWER"]'::jsonb,
    '["WEB"]'::jsonb,
    '{
        "title": "Campaign Setup",
        "steps": [
            {
                "id": "step1",
                "title": "Create Campaign",
                "description": "Start by clicking New Campaign.",
                "targetSelector": "[data-tour=\"new-campaign-btn\"]",
                "placement": "right"
            },
            {
                "id": "step2",
                "title": "Campaign Settings",
                "description": "Configure runtime controls before launch.",
                "targetSelector": "[data-tour=\"campaign-settings\"]",
                "placement": "top"
            },
            {
                "id": "step3",
                "title": "Launch Campaign",
                "description": "Click here to activate your campaign.",
                "targetSelector": "[data-tour=\"launch-campaign-btn\"]",
                "placement": "bottom"
            }
        ]
    }'::jsonb,
    'SYSTEM'
)
ON CONFLICT (feature_key) DO NOTHING;

INSERT INTO feature_definition (feature_key, feature_type, category, name, description, enabled, rollout_percentage, min_plan, roles, platforms, metadata, created_by)
VALUES
(
    'tour.questions',
    'TOUR',
    'ONBOARDING',
    'Questions Tour',
    'Learn how to build your question bank',
    true,
    100,
    'BASIC',
    '["ADMIN", "EDITOR", "VIEWER"]'::jsonb,
    '["WEB"]'::jsonb,
    '{
        "title": "Question Bank",
        "steps": [
            {
                "id": "step1",
                "title": "Add Question",
                "description": "Create reusable questions for your surveys.",
                "targetSelector": "[data-tour=\"new-question-btn\"]",
                "placement": "right"
            },
            {
                "id": "step2",
                "title": "Question List",
                "description": "View and manage all your questions.",
                "targetSelector": "[data-tour=\"question-list\"]",
                "placement": "bottom"
            }
        ]
    }'::jsonb,
    'SYSTEM'
)
ON CONFLICT (feature_key) DO NOTHING;
