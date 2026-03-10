-- Add first login tracking and last login timestamp to admin_user

ALTER TABLE admin_user
ADD COLUMN first_login BOOLEAN NOT NULL DEFAULT TRUE,
ADD COLUMN last_login_at TIMESTAMP WITH TIME ZONE;

-- Add comment for documentation
COMMENT ON COLUMN admin_user.first_login IS 'Tracks if this is the user''s first login after registration. Set to false after first successful login.';
COMMENT ON COLUMN admin_user.last_login_at IS 'Timestamp of the user''s most recent login for activity tracking.';

-- Create index on last_login_at for activity queries
CREATE INDEX IF NOT EXISTS idx_admin_user_last_login ON admin_user(last_login_at);
