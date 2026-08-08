-- CinemaSeat PostgreSQL Microservices Central Database & Schema Setup
-- Database: team42 | User: root | Password: Quanfey

-- Create root superuser if not exists
DO $$
BEGIN
   IF NOT EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = 'root') THEN
      CREATE ROLE root WITH SUPERUSER LOGIN PASSWORD 'Quanfey';
   ELSE
      ALTER ROLE root WITH SUPERUSER PASSWORD 'Quanfey';
   END IF;
END
$$;

-- Create team42 main database
SELECT 'CREATE DATABASE team42 OWNER root'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'team42')\gexec

-- Connect to team42 database and create service-wise schemas
\c team42

DROP SCHEMA IF EXISTS auth_schema CASCADE;
DROP SCHEMA IF EXISTS product_schema CASCADE;
DROP SCHEMA IF EXISTS inventory_schema CASCADE;
DROP SCHEMA IF EXISTS payment_schema CASCADE;
DROP SCHEMA IF EXISTS user_schema CASCADE;

CREATE SCHEMA auth_schema AUTHORIZATION root;
CREATE SCHEMA product_schema AUTHORIZATION root;
CREATE SCHEMA inventory_schema AUTHORIZATION root;
CREATE SCHEMA payment_schema AUTHORIZATION root;
CREATE SCHEMA user_schema AUTHORIZATION root;

GRANT ALL PRIVILEGES ON SCHEMA auth_schema TO root;
GRANT ALL PRIVILEGES ON SCHEMA product_schema TO root;
GRANT ALL PRIVILEGES ON SCHEMA inventory_schema TO root;
GRANT ALL PRIVILEGES ON SCHEMA payment_schema TO root;
GRANT ALL PRIVILEGES ON SCHEMA user_schema TO root;
