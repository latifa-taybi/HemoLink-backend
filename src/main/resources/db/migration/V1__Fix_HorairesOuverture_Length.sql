-- Flyway Migration for HémoLink
-- Version: V1__Fix_HorairesOuverture_Length.sql
-- Date: 2026-03-27

-- PostgreSQL
ALTER TABLE centre_collecte
ALTER COLUMN horaires_ouverture TYPE TEXT;

-- Add comment for documentation
COMMENT ON COLUMN centre_collecte.horaires_ouverture IS 'JSON array of opening hours for 7 days (supports ~800+ characters)';

