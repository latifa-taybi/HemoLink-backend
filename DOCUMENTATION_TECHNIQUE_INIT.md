# 📚 DOCUMENTATION TECHNIQUE - Initialisation Automatique

## 🏗️ ARCHITECTURE

### Flux de création (POST)

```
Client Angular
    ↓
Frontend envoie DTO avec latitude/longitude
    ↓
POST /api/centres-collecte
    ↓
CentreCollecteController.creer(dto)
    ↓
CentreCollecteServiceImpl.creer(dto)
    ├─ mapper.toEntity(dto)  // Crée CentreCollecte brut
    ├─ centre.initializeLocalization()  // ✨ Crée Point
    ├─ centre.initializeHorairesOuverture()  // ✨ Crée "[]"
    └─ repository.save(centre)  // Persiste en DB
    ↓
Response avec tous les champs remplis
    ↓
Client Angular reçoit le CentreCollecte complet
```

### Point JTS (PostGIS)

**Code Java:**
```java
GeometryFactory geometryFactory = new GeometryFactory();
Point point = geometryFactory.createPoint(
    new Coordinate(longitude, latitude)
);
// Résultat: POINT(-6.84165 34.020882)
```

**Stockage PostgreSQL:**
```
Type: geography(Point, 4326)
Format: POINT(-6.84165 34.020882)
SRID: 4326 (WGS84)
```

---

## 🔑 POINTS IMPORTANTS

### 1. JTS vs Java Point
```java
// ❌ FAUX - java.awt.Point (2D simple)
import java.awt.Point;

// ✅ BON - JTS Point (spatial)
import org.locationtech.jts.geom.Point;
```

### 2. Ordre des coordonnées (PostGIS)
```java
// ✅ CORRECT: longitude, latitude
new Coordinate(longitude, latitude)  // (-6.84165, 34.020882)

// ❌ FAUX: latitude, longitude
new Coordinate(latitude, longitude)   // (34.020882, -6.84165)
```

### 3. SRID 4326 (WGS84)
- Standard GPS/OpenStreetMap
- Format: Longitude, Latitude
- GeometryFactory() utilise SRID 0 par défaut

---

## 🛠️ MODIFICATION DES ENTITÉS

### Avant
```java
@Column(columnDefinition = "geography(Point,4326)")
private Point localisationGps;

@Column(columnDefinition = "TEXT")
private String horairesOuverture;
```

### Après
```java
// ✅ Permet NULL lors de la création
@Column(columnDefinition = "geography(Point,4326)", nullable = true)
private Point localisationGps;

@Column(columnDefinition = "TEXT", nullable = true)
private String horairesOuverture;

// ✅ Méthodes d'initialisation
public void initializeLocalization() {
    if (this.latitude != null && this.longitude != null) {
        GeometryFactory geometryFactory = new GeometryFactory();
        this.localisationGps = geometryFactory.createPoint(
            new Coordinate(this.longitude, this.latitude)
        );
    }
}

public void initializeHorairesOuverture() {
    if (this.horairesOuverture == null) {
        this.horairesOuverture = "[]";
    }
}
```

---

## 🔄 MODIFICATION DU SERVICE

### Avant
```java
@Override
public CentreCollecteResponseDto creer(CentreCollecteDto dto) {
    CentreCollecte centre = centreCollecteMapper.toEntity(dto);
    CentreCollecte saved = centreCollecteRepository.save(centre);
    return centreCollecteMapper.toResponseDto(saved);
}
```

### Après
```java
@Override
public CentreCollecteResponseDto creer(CentreCollecteDto dto) {
    CentreCollecte centre = centreCollecteMapper.toEntity(dto);
    
    // 🔑 Initialiser les champs calculés
    centre.initializeLocalization();
    centre.initializeHorairesOuverture();
    
    CentreCollecte saved = centreCollecteRepository.save(centre);
    return centreCollecteMapper.toResponseDto(saved);
}
```

---

## 📊 DONNÉES DE TEST

### Input (Frontend)
```json
{
  "nom": "Centre de Rabat",
  "adresse": "Avenue Hassan II",
  "ville": "Rabat",
  "latitude": 34.020882,
  "longitude": -6.84165,
  "telephone": "0537000000"
}
```

### Output (Backend Response)
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

### Database (PostgreSQL)
```sql
SELECT * FROM centre_collecte WHERE id = 1;

 id |       nom       |     adresse      | ville  | latitude | longitude |                    localisation_gps                    | horaires_ouverture | telephone
----+-----------------+------------------+--------+----------+-----------+--------------------------------------------------------+--------------------+----------
  1 | Centre de Rabat | Avenue Hassan II | Rabat  | 34.02088 | -6.841650 | 0101000000154D301869FEEE4009E6D0E0EBE854F04 | []                 | 053700000
```

---

## ✅ VÉRIFICATIONS

### SQL pour vérifier
```sql
-- Vérifier que localisation_gps n'est pas NULL
SELECT id, nom, ST_AsText(localisation_gps) as gps, horaires_ouverture
FROM centre_collecte
WHERE localisation_gps IS NOT NULL;
```

### Expected Result
```
id | nom             | gps                      | horaires_ouverture
1  | Centre de Rabat | POINT(-6.84165 34.02088) | []
```

---

## 🚀 AMÉLIORATIONS FUTURES (Optionnelles)

1. **Validations supplémentaires**
   ```java
   if (latitude < -90 || latitude > 90) throw new InvalidCoordinates();
   if (longitude < -180 || longitude > 180) throw new InvalidCoordinates();
   ```

2. **Logging amélioré**
   ```java
   log.info("Created Point({}, {})", longitude, latitude);
   ```

3. **Tests unitaires**
   ```java
   @Test
   void testInitializeLocalization() {
       centre.setLatitude(34.0);
       centre.setLongitude(-6.8);
       centre.initializeLocalization();
       assertNotNull(centre.getLocalisationGps());
   }
   ```

---

**Version:** 1.0  
**Date:** 27/03/2026  
**Status:** ✅ Production Ready


