#!/usr/bin/env pwsh

# Script pour corriger l'erreur PostgreSQL: valeur trop longue pour character varying(255)
# Solution: Changer horaires_ouverture de VARCHAR(255) à TEXT

Write-Host "🔧 FIX: Modifier horaires_ouverture VARCHAR(255) → TEXT" -ForegroundColor Cyan
Write-Host "========================================================" -ForegroundColor Cyan
Write-Host ""

# Vérifier que psql est accessible
if (-not (Get-Command psql -ErrorAction SilentlyContinue)) {
    Write-Host "❌ ERREUR: psql non trouvé. Assurez-vous que PostgreSQL est installé." -ForegroundColor Red
    exit 1
}

# Configuration
$pgHost = "localhost"
$pgPort = "5432"
$pgDatabase = "HemoLink"
$pgUser = "postgres"

Write-Host "📋 Configuration:" -ForegroundColor Green
Write-Host "  Host: $pgHost"
Write-Host "  Port: $pgPort"
Write-Host "  Database: $pgDatabase"
Write-Host ""

# Demander le mot de passe
$password = Read-Host "Entrez le mot de passe PostgreSQL"

# SQL à exécuter
$sqlCommand = @"
ALTER TABLE centre_collecte ALTER COLUMN horaires_ouverture TYPE TEXT;
"@

Write-Host "📝 Exécution de la migration..." -ForegroundColor Yellow

# Exécuter la commande SQL
$env:PGPASSWORD = $password

try {
    $sqlCommand | psql -h $pgHost -p $pgPort -U $pgUser -d $pgDatabase -v ON_ERROR_STOP=1

    if ($LASTEXITCODE -eq 0) {
        Write-Host ""
        Write-Host "✅ Migration réussie!" -ForegroundColor Green
        Write-Host ""

        # Vérifier le changement
        Write-Host "🔍 Vérification:" -ForegroundColor Cyan
        $verifySQL = @"
SELECT column_name, data_type FROM information_schema.columns
WHERE table_name = 'centre_collecte' AND column_name = 'horaires_ouverture';
"@
        $verifySQL | psql -h $pgHost -p $pgPort -U $pgUser -d $pgDatabase

        Write-Host ""
        Write-Host "📌 Prochaines étapes:" -ForegroundColor Green
        Write-Host "1. Redémarrer l'application: mvn spring-boot:run"
        Write-Host "2. Tester POST /api/centres-collecte"
        Write-Host "3. Vérifier que Status 201 Created (pas 500)"
    } else {
        Write-Host ""
        Write-Host "❌ Erreur lors de l'exécution" -ForegroundColor Red
        exit 1
    }
} catch {
    Write-Host "❌ Erreur: $_" -ForegroundColor Red
    exit 1
} finally {
    $env:PGPASSWORD = ""
}

