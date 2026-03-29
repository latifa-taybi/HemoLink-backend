# 🚨 ERREUR 500 - "valeur trop longue pour character varying(255)"

**Date:** 28/03/2026  
**Cause:** Column `horaires_ouverture` en VARCHAR(255) au lieu de TEXT

---

## 🎯 PROBLÈME

```
Hibernate Error: ERREUR: valeur trop longue pour le type character varying(255)
POST http://localhost:8082/api/centres-collecte 500 (Internal Server Error)
```

### Raison
- Le JSON des 7 jours = ~800 caractères
- Column `horaires_ouverture` = VARCHAR(255) ← Trop petit!
- Hibernate essaie d'insérer 800 chars dans 255 chars max = ERREUR!

---

## ✅ SOLUTION (3 ÉTAPES)

### Étape 1: Exécuter la migration SQL

**Option A - Utiliser le script PowerShell (Automatique)**
```powershell
.\FIX_HORAIRES_OUVERTURE.ps1
```

**Option B - Exécuter manuellement via pgAdmin/DBeaver**
```sql
ALTER TABLE centre_collecte 
ALTER COLUMN horaires_ouverture TYPE TEXT;
```

**Option C - Via ligne de commande PostgreSQL**
```bash
psql -h localhost -U postgres -d HemoLink -c "ALTER TABLE centre_collecte ALTER COLUMN horaires_ouverture TYPE TEXT;"
```

### Étape 2: Redémarrer l'application
```bash
mvn spring-boot:run
```

### Étape 3: Tester le POST

**Swagger UI:**
```
http://localhost:8082/swagger-ui.html
→ POST /api/centres-collecte
```

**cURL:**
```bash
curl -X POST http://localhost:8082/api/centres-collecte \


**Status:** ✅ **PRÊT À EXÉCUTER LA MIGRATION**

---

| Impossible créer centres | Créer et modifier horaires |
| VARCHAR(255) → Erreur 500 ❌ | TEXT → Status 201 ✅ |
|-------|-------|
| Avant | Après |

## 🎯 EN RÉSUMÉ

---

   ```
   POST /api/centres-collecte
   ```bash
3. **Tester:**

   ```
   mvn spring-boot:run
   ```bash
2. **Redémarrer:**

   ```
   .\FIX_HORAIRES_OUVERTURE.ps1
   ```bash
1. **Exécuter:**

## ⚡ QUICK FIX (2 minutes)

---

| `FIX_HORAIRES_OUVERTURE_VARCHAR_TEXT.sql` | SQL direct |
| `FIX_HORAIRES_OUVERTURE.ps1` | Script PowerShell automatique |
|---------|-------------|
| Fichier | Description |

## 📋 FICHIERS CRÉÉS

---

```
horaires_ouverture   | varchar   | 255   ❌ (trop petit)
column_name          | data_type | character_maximum_length
```
**Avant (FAUX):**

```
horaires_ouverture   | text      | NULL  ✅ (pas de limite!)
column_name          | data_type | character_maximum_length
```
**Résultat attendu:**

```
WHERE table_name = 'centre_collecte' AND column_name = 'horaires_ouverture';
FROM information_schema.columns 
SELECT column_name, data_type, character_maximum_length
```sql
### Via SQL

## 🔍 VÉRIFIER QUE C'EST RÉGLÉ

---

```
}
  ...
  "horairesOuverture": "[{\"jour\":\"LUNDI\",...}]",
{
Status 201 Created ✅
```
**Résultat attendu:**

```
  }'
    "telephone": "09999"
    "longitude": 12,
    "latitude": 11,
    "ville": "centre1",
    "adresse": "centre1",
    "nom": "centre1",
  -d '{
  -H "Content-Type: application/json" \
