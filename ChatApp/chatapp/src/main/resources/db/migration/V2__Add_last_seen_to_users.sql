-- This script safely adds the last_seen column to the existing users table
-- without deleting any data.

ALTER TABLE users
ADD COLUMN last_seen TIMESTAMP;
