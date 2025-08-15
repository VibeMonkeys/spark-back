-- Migration: Add password column to users table
-- Date: 2025-08-15
-- Description: Add password column for JWT authentication system

-- Step 1: Add password column as nullable first (to avoid issues with existing data)
ALTER TABLE users ADD COLUMN IF NOT EXISTS password VARCHAR(255);

-- Step 2: Set default empty string for any existing NULL values
UPDATE users SET password = '' WHERE password IS NULL;

-- Step 3: Set NOT NULL constraint
ALTER TABLE users ALTER COLUMN password SET NOT NULL;

-- Step 4: Set default value for future inserts
ALTER TABLE users ALTER COLUMN password SET DEFAULT '';

-- Verification query (uncomment to check the result)
-- SELECT column_name, data_type, is_nullable, column_default 
-- FROM information_schema.columns 
-- WHERE table_name = 'users' AND column_name = 'password';