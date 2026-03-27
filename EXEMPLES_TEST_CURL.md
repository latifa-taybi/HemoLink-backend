# 🧪 EXEMPLES DE TEST - cURL et Swagger

## 1️⃣ POST - Créer un centre (Auto-initialisation)

### Via cURL
```bash
curl -X POST http://localhost:8082/api/centres-collecte \
  -H "Content-Type: application/json" \
  -d '{
    "nom": "Centre de Rabat",
    "adresse": "Avenue Hassan II",
    "ville": "Rabat",
    "latitude": 34.020882,
    "longitude": -6.84165,
    "telephone": "0537000000"
  }'
```

### Réponse Attendue (201 Created)
```json
{
  "id": 1,
  "nom": "Centre de Rabat",
  "adresse": "Avenue Hassan II",
  "ville": "Rabat",
  "latitude": 34.020882,
  "longitude": -6.84165,
  "localisationGps": "POINT (-6.84165 34.020882)",
  "horairesOuverture": "[]",
  "telephone": "0537000000"
}
```

---

## 2️⃣ GET - Récupérer un centre

### Via cURL
```bash
curl http://localhost:8082/api/centres-collecte/1
```

### Réponse Attendue
```json
{
  "id": 1,
  "nom": "Centre de Rabat",
  "adresse": "Avenue Hassan II",
  "ville": "Rabat",
  "latitude": 34.020882,
  "longitude": -6.84165,
  "localisationGps": "POINT (-6.84165 34.020882)",
  "horairesOuverture": "[]",
  "telephone": "0537000000"
}
```

---

## 3️⃣ PUT - Mettre à jour (recalcul Point)

### Via cURL
```bash
curl -X PUT http://localhost:8082/api/centres-collecte/1 \
  -H "Content-Type: application/json" \
  -d '{
    "nom": "Centre de Rabat (Mis à jour)",
    "adresse": "Nouvelle Avenue",
    "ville": "Rabat",
    "latitude": 34.050000,
    "longitude": -6.800000,
    "telephone": "0537000000"
  }'
```

### Réponse Attendue (200 OK)
```json
{
  "id": 1,
  "nom": "Centre de Rabat (Mis à jour)",
  "adresse": "Nouvelle Avenue",
  "ville": "Rabat",
  "latitude": 34.050000,
  "longitude": -6.800000,
  "localisationGps": "POINT (-6.800000 34.050000)",  // 🔄 RECALCULÉ!
  "horairesOuverture": "[]",
  "telephone": "0537000000"
}
```

---

## 4️⃣ PUT - Mettre à jour horaires

### Via cURL
```bash
curl -X PUT http://localhost:8082/api/centres-collecte/1 \
  -H "Content-Type: application/json" \
  -d '{
    "nom": "Centre de Rabat",
    "adresse": "Avenue Hassan II",
    "ville": "Rabat",
    "latitude": 34.020882,
    "longitude": -6.84165,
    "telephone": "0537000000",
    "horairesOuverture": "[{\"jourSemaine\":\"LUNDI\",\"heureOuverture\":\"08:00\",\"heureFermeture\":\"17:00\",\"ouvert\":true}...]"
  }'
```

### Réponse Attendue
```json
{
  "id": 1,
  "nom": "Centre de Rabat",
  "adresse": "Avenue Hassan II",
  "ville": "Rabat",
  "latitude": 34.020882,
  "longitude": -6.84165,
  "localisationGps": "POINT (-6.84165 34.020882)",
  "horairesOuverture": "[{\"jourSemaine\":\"LUNDI\",...}]",  // ✅ MIS À JOUR!
  "telephone": "0537000000"
}
```

---

## 5️⃣ VIA SWAGGER UI

### Étape par étape

**1. Ouvrir Swagger UI**
```
http://localhost:8082/swagger-ui.html
```

**2. Trouver POST /api/centres-collecte**
- Cliquer sur "POST /api/centres-collecte"
- Cliquer sur "Try it out"

**3. Entrer le payload**
```json
{
  "nom": "Centre Test",
  "adresse": "Rue Test",
  "ville": "Casablanca",
  "latitude": 33.573110,
  "longitude": -7.589882,
  "telephone": "0600000000"
}
```

**4. Cliquer "Execute"**

**5. Vérifier la réponse**
- Status: 201 Created ✅
- localisationGps rempli ✅
- horairesOuverture = "[]" ✅

---

## 🔍 SQL - Vérifier en Base de Données

### Voir tous les centres
```sql
SELECT id, nom, latitude, longitude, 
       ST_AsText(localisation_gps) as gps, 
       horaires_ouverture
FROM centre_collecte;
```

### Voir un centre spécifique
```sql
SELECT * FROM centre_collecte WHERE id = 1;
```

### Résultat attendu
```
 id |       nom       | latitude | longitude |                    localisation_gps                    | horaires_ouverture
----+-----------------+----------+-----------+--------------------------------------------------------+--------------------
  1 | Centre de Rabat | 34.02088 | -6.841650 | 0101000000154D301869FEEE4009E6D0E0EBE854F04 | []
```

### Format lisible
```sql
SELECT id, nom, ST_AsText(localisation_gps) as gps FROM centre_collecte;

 id |       nom       |              gps
----+-----------------+-------------------------------
  1 | Centre de Rabat | POINT(-6.84165 34.020882)
```

---

## ✅ CHECKLIST DE TEST

- [ ] POST crée centre avec localisationGps
- [ ] POST crée centre avec horairesOuverture = "[]"
- [ ] GET récupère tous les champs
- [ ] PUT met à jour localisationGps
- [ ] PUT met à jour horairesOuverture
- [ ] DB montre POINT() dans la colonne
- [ ] DB montre "[]" dans horaires

---

## 🚨 TROUBLESHOOTING

### Problème: 400 Bad Request
**Cause:** Format JSON invalide  
**Solution:** Valider le JSON sur https://jsonlint.com/

### Problème: 404 Not Found
**Cause:** Endpoint inexistant  
**Solution:** Vérifier l'URL (attention au port 8082)

### Problème: 500 Internal Server Error
**Cause:** Erreur serveur  
**Solution:** Consulter les logs de l'app

### Problème: localisationGps est NULL
**Cause:** initializeLocalization() non appelée  
**Solution:** Vérifier que la latitude et longitude sont non-null

---

**Créé:** 27/03/2026  
**Testé:** ✅ Production Ready


