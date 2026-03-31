-- Migration: Augmenter la limite de horairesOuverture
-- Date: 2026-03-27
-- Description: Standardized for PostgreSQL (ALTER COLUMN ... TYPE TEXT)

ALTER TABLE centre_collecte
ALTER COLUMN horaires_ouverture TYPE TEXT;

-- PostgreSQL doesn't use DESCRIBE, we can check via information_schema or \d if needed manually
