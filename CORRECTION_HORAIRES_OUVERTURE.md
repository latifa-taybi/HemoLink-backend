# 🔧 CORRECTION - Erreur 500 sur horairesOuverture

**Date:** 27/03/2026  
**Problème:** Erreur 500 lors de la mise à jour des horaires d'un centre de collecte  
**Cause:** Troncature de données (Data Truncation) - VARCHAR(255) insuffisant

---

## ✅ SOLUTION APPLIQUÉE

### Problème Identifié
Le champ `horairesOuverture` était limité à **VARCHAR(255)** par défaut dans PostgreSQL, mais le JSON envoyé par Angular contient les horaires de 7 jours (≈800 caractères).

### Correction Effectuée
Modifié l'entité `CentreCollecte.java` :

```java
// AVANT (❌ Limite à 255 caractères)
private String horairesOuverture;

// APRÈS (✅ Limité maintenant à TEXT)
@Column(columnDefinition = "TEXT")
private String horairesOuverture;
```

---

## 📋 FICHIERS MODIFIÉS

| Fichier | Modification |
|---------|--------------|
| `src/main/java/com/example/hemolinkbackend/entity/CentreCollecte.java` | Ajout `@Column(columnDefinition = "TEXT")` |

---

## 🧪 COMMENT TESTER

### 1. Démarrer l'application
```bash
mvn spring-boot:run
```

### 2. Accéder à Swagger UI
```
http://localhost:8082/swagger-ui.html
```

### 3. Tester l'endpoint PUT
**Endpoint:** `PUT /api/centres-collecte/{id}`

**Payload (JSON):**
```json
{
  "nom": "Centre de Rabat",
  "adresse": "Avenue Hassan II",
  "ville": "Rabat",
  "latitude": 34.020882,
  "longitude": -6.84165,
  "telephone": "0537000000",
  "horairesOuverture": "[{\"jourSemaine\":\"LUNDI\",\"heureOuverture\":\"08:00\",\"heureFermeture\":\"17:00\",\"ouvert\":true},{\"jourSemaine\":\"MARDI\",\"heureOuverture\":\"08:00\",\"heureFermeture\":\"17:00\",\"ouvert\":true},{\"jourSemaine\":\"MERCREDI\",\"heureOuverture\":\"08:00\",\"heureFermeture\":\"17:00\",\"ouvert\":true},{\"jourSemaine\":\"JEUDI\",\"heureOuverture\":\"08:00\",\"heureFermeture\":\"17:00\",\"ouvert\":true},{\"jourSemaine\":\"VENDREDI\",\"heureOuverture\":\"08:00\",\"heureFermeture\":\"17:00\",\"ouvert\":true},{\"jourSemaine\":\"SAMEDI\",\"heureOuverture\":\"08:00\",\"heureFermeture\":\"17:00\",\"ouvert\":true},{\"jourSemaine\":\"DIMANCHE\",\"heureOuverture\":\"08:00\",\"heureFermeture\":\"17:00\",\"ouvert\":false}]"
}
```

### 4. Résultat attendu
- ✅ **Status 200 OK** (avant c'était 500 Internal Server Error)
- ✅ Le centre de collecte est mis à jour avec les horaires complets

---

## 🔍 RAISON DE L'ERREUR 500

**PostgreSQL Error:**
```
ERROR: value too long for type character varying(255)
```

C'est une **Data Truncation Error** - le JSON des horaires dépasse la limite VARCHAR(255).

---

## 🎯 CE QUE VOUS DEVIEZ VÉRIFIER

✅ **Entité CentreCollecte** - Correctif appliqué  
✅ **DTO CentreCollecteDto** - Pas de problème (String horairesOuverture)  
✅ **DTO CentreCollecteResponseDto** - Pas de problème  
✅ **Sécurité** - Désactivée pour test (voir SecurityConfig.java)  

---

## 💡 BONNES PRATIQUES

Pour les champs JSON longs en base de données, utilisez toujours :

```java
@Column(columnDefinition = "TEXT")  // PostgreSQL, MySQL
// OU
@Lob                                // Approche JPA portable
// OU
@Column(length = 5000)              // Si vous connaissez la limite max
```

---

**Status:** ✅ CORRIGÉ ET PRÊT À TESTER


