-- Migration: Augmenter la limite de horairesOuverture
-- Date: 2026-03-27
-- Description: Change VARCHAR(255) to TEXT pour accepter les horaires JSON complets des 7 jours

ALTER TABLE centre_collecte
MODIFY COLUMN horaires_ouverture TEXT;

-- Vérifier le changement
DESCRIBE centre_collecte;

