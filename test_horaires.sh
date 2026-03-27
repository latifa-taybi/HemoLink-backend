#!/bin/bash
# Script de test de l'endpoint PUT /api/centres-collecte/{id}

echo "🚀 Test PUT /api/centres-collecte/1"
echo "======================================"

curl -X PUT http://localhost:8082/api/centres-collecte/1 \
  -H "Content-Type: application/json" \
  -d '{
    "nom": "Centre de Rabat",
    "adresse": "Avenue Hassan II",
    "ville": "Rabat",
    "latitude": 34.020882,
    "longitude": -6.84165,
    "telephone": "0537000000",
    "horairesOuverture": "[{\"jourSemaine\":\"LUNDI\",\"heureOuverture\":\"08:00\",\"heureFermeture\":\"17:00\",\"ouvert\":true},{\"jourSemaine\":\"MARDI\",\"heureOuverture\":\"08:00\",\"heureFermeture\":\"17:00\",\"ouvert\":true},{\"jourSemaine\":\"MERCREDI\",\"heureOuverture\":\"08:00\",\"heureFermeture\":\"17:00\",\"ouvert\":true},{\"jourSemaine\":\"JEUDI\",\"heureOuverture\":\"08:00\",\"heureFermeture\":\"17:00\",\"ouvert\":true},{\"jourSemaine\":\"VENDREDI\",\"heureOuverture\":\"08:00\",\"heureFermeture\":\"17:00\",\"ouvert\":true},{\"jourSemaine\":\"SAMEDI\",\"heureOuverture\":\"08:00\",\"heureFermeture\":\"17:00\",\"ouvert\":true},{\"jourSemaine\":\"DIMANCHE\",\"heureOuverture\":\"08:00\",\"heureFermeture\":\"17:00\",\"ouvert\":false}]"
  }' | json_pp

echo ""
echo "✅ Test terminé"

