-- Run this in pgAdmin's Query Tool (connected as postgres on port 5433)
-- Step 1: Create the taskflow role if it doesn't exist
DO $$
BEGIN
  IF NOT EXISTS (SELECT FROM pg_roles WHERE rolname = 'taskflow') THEN
    CREATE ROLE taskflow WITH LOGIN PASSWORD 'taskflow123';
  ELSE
    ALTER ROLE taskflow WITH PASSWORD 'taskflow123';
  END IF;
END
$$;

-- Step 2: Create the database if it doesn't exist
SELECT 'CREATE DATABASE taskflow OWNER taskflow'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'taskflow')\gexec

-- Step 3: Grant privileges
GRANT ALL PRIVILEGES ON DATABASE taskflow TO taskflow;
