-- FIX ERREUR 500: Change horaires_ouverture from VARCHAR(255) to TEXT
-- Date: 2026-03-28

-- PostgreSQL
ALTER TABLE centre_collecte
ALTER COLUMN horaires_ouverture TYPE TEXT;

-- Vérifier le changement
\d centre_collecte

