-- Add missing audit columns to user_feature_access table
ALTER TABLE user_feature_access 
ADD COLUMN created_by VARCHAR(255) NOT NULL DEFAULT 'SYSTEM',
ADD COLUMN updated_by VARCHAR(255);
