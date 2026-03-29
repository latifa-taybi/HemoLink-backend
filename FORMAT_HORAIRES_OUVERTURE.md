# 📋 FORMAT DES HORAIRES D'OUVERTURE

## 🎯 Format JSON par défaut (Automatique)

Lors de la création d'un centre, les horaires sont initialisés automatiquement :

```json
[
  {
    "jour": "LUNDI",
    "ouvert": true,
    "ouverture": "08:00",
    "fermeture": "17:00"
  },
  {
    "jour": "MARDI",
    "ouvert": true,
    "ouverture": "08:00",
    "fermeture": "17:00"
  },
  {
    "jour": "MERCREDI",
    "ouvert": true,
    "ouverture": "08:00",
    "fermeture": "17:00"
  },
  {
    "jour": "JEUDI",
    "ouvert": true,
    "ouverture": "08:00",
    "fermeture": "17:00"
  },
  {
    "jour": "VENDREDI",
    "ouvert": true,
    "ouverture": "08:00",
    "fermeture": "17:00"
  },
  {
    "jour": "SAMEDI",
    "ouvert": true,
    "ouverture": "08:00",
    "fermeture": "17:00"
  },
  {
    "jour": "DIMANCHE",
    "ouvert": false,
    "ouverture": "09:00",
    "fermeture": "13:00"
  }
]
```

---

## 📝 Exemple 1: POST - Créer un centre

### Request
```bash
curl -X POST http://localhost:8082/api/centres-collecte \
  -H "Content-Type: application/json" \
  -d '{
    "nom": "centre1",
    "adresse": "centre1",
    "ville": "centre1",
    "latitude": 11,
    "longitude": 12,
    "telephone": "09999"
  }'
```

### Response (201 Created)
```json
{
  "id": 7,
  "nom": "centre1",
  "adresse": "centre1",
  "ville": "centre1",
  "latitude": 11,
  "longitude": 12,
  "horairesOuverture": "[{\"jour\":\"LUNDI\",\"ouvert\":true,\"ouverture\":\"08:00\",\"fermeture\":\"17:00\"},...7 jours]",
  "telephone": "09999",
  "localisationGps": "POINT (12 11)"
}
```

✅ **Les horaires par défaut sont créés automatiquement!**

---

## 📝 Exemple 2: PUT - Modifier les horaires

### Request (Modifier les horaires du Dimanche)
```bash
curl -X PUT http://localhost:8082/api/centres-collecte/7 \
  -H "Content-Type: application/json" \
  -d '{
    "nom": "centre1",
    "adresse": "centre1",
    "ville": "centre1",
    "latitude": 11,
    "longitude": 12,
    "telephone": "09999",
    "horairesOuverture": "[{\"jour\":\"LUNDI\",\"ouvert\":true,\"ouverture\":\"08:00\",\"fermeture\":\"17:00\"},{\"jour\":\"MARDI\",\"ouvert\":true,\"ouverture\":\"08:00\",\"fermeture\":\"17:00\"},{\"jour\":\"MERCREDI\",\"ouvert\":true,\"ouverture\":\"08:00\",\"fermeture\":\"17:00\"},{\"jour\":\"JEUDI\",\"ouvert\":true,\"ouverture\":\"08:00\",\"fermeture\":\"17:00\"},{\"jour\":\"VENDREDI\",\"ouvert\":true,\"ouverture\":\"08:00\",\"fermeture\":\"17:00\"},{\"jour\":\"SAMEDI\",\"ouvert\":true,\"ouverture\":\"08:00\",\"fermeture\":\"17:00\"},{\"jour\":\"DIMANCHE\",\"ouvert\":true,\"ouverture\":\"10:00\",\"fermeture\":\"18:00\"}]"
  }'
```

### Response (200 OK)
```json
{
  "id": 7,
  "nom": "centre1",
  "horairesOuverture": "[...horaires modifiés...]",
  ...
}
```

✅ **Les horaires sont mis à jour correctement!**

---

## 🛠️ Validation

### ✅ Format Valide
```json
"horairesOuverture": "[{\"jour\":\"LUNDI\",...}]"
```

### ❌ Format Invalide
```json
"horairesOuverture": "open"  // ❌ Pas un JSON!
"horairesOuverture": "{...}"  // ❌ Doit commencer par [
```

---

## 📊 Structure des champs

| Champ | Type | Exemple | Requis |
|-------|------|---------|--------|
| `jour` | String | "LUNDI" | ✅ Oui |
| `ouvert` | Boolean | true | ✅ Oui |
| `ouverture` | String (HH:mm) | "08:00" | ✅ Oui |
| `fermeture` | String (HH:mm) | "17:00" | ✅ Oui |

---

## 🎯 En Résumé

| Action | Horaires | Résultat |
|--------|----------|----------|
| **POST** (création) | Pas envoyés | ✅ Créés par défaut (8h-17h, sauf Dim 9h-13h) |
| **PUT** (modif) | Envoyés | ✅ Mis à jour si JSON valide |
| **PUT** (modif) | Non envoyés | ✅ Garde les horaires actuels |
| **PUT** (modif) | Format invalide | ⚠️ Garde les horaires actuels |

---

## 🚀 Pour Tester via Swagger

1. Ouvrir: `http://localhost:8082/swagger-ui.html`
2. POST /api/centres-collecte - Créer (horaires auto)
3. GET /api/centres-collecte/{id} - Voir les horaires
4. PUT /api/centres-collecte/{id} - Modifier les horaires


