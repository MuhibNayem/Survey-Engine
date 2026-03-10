-- Add user preferences table for storing UI/UX preferences
-- Tracks dismissed tooltips, completed feature tours, and user settings

CREATE TABLE IF NOT EXISTS user_preference (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    tenant_id VARCHAR(255) NOT NULL,
    preferences JSONB NOT NULL DEFAULT '{}',
    created_by VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_by VARCHAR(255),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    
    -- Ensure one preference row per user
    CONSTRAINT uk_user_preference_user_id UNIQUE (user_id),
    
    -- Foreign key to admin user
    CONSTRAINT fk_user_preference_admin_user 
        FOREIGN KEY (user_id) 
        REFERENCES admin_user(id) 
        ON DELETE CASCADE,
    
    -- Foreign key to tenant
    CONSTRAINT fk_user_preference_tenant 
        FOREIGN KEY (tenant_id) 
        REFERENCES tenant(id)
);

-- Index for fast lookups by user
CREATE INDEX IF NOT EXISTS idx_user_preference_user_id ON user_preference(user_id);

-- Index for tenant-scoped queries
CREATE INDEX IF NOT EXISTS idx_user_preference_tenant_id ON user_preference(tenant_id);

-- Add comments for documentation
COMMENT ON TABLE user_preference IS 'Stores user UI/UX preferences including dismissed tooltips, completed tours, and feature flags';
COMMENT ON COLUMN user_preference.preferences IS 'JSON map of preference key-value pairs (e.g., "tour.dashboard.completed": "true")';
