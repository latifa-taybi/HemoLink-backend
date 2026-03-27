#!/bin/bash
# Test script for POST /api/centres-collecte with auto-initialization

echo "🚀 Test POST /api/centres-collecte"
echo "===================================="
echo ""

# Créer un centre de collecte
echo "📝 Création d'un centre avec latitude/longitude..."
RESPONSE=$(curl -s -X POST http://localhost:8082/api/centres-collecte \
  -H "Content-Type: application/json" \
  -d '{
    "nom": "Centre de Rabat",
    "adresse": "Avenue Hassan II",
    "ville": "Rabat",
    "latitude": 34.020882,
    "longitude": -6.84165,
    "telephone": "0537000000"
  }')

echo "📦 Réponse:"
echo "$RESPONSE" | jq '.'

# Extraire l'ID
ID=$(echo "$RESPONSE" | jq -r '.id')
echo ""
echo "✅ Centre créé avec ID: $ID"
echo ""

# Vérifier les champs auto-initialisés
echo "🔍 Vérification des champs auto-initialisés:"
echo "$RESPONSE" | jq '{
  nom: .nom,
  latitude: .latitude,
  longitude: .longitude,
  localisationGps: .localisationGps,
  horairesOuverture: .horairesOuverture
}'

echo ""
echo "✅ Test terminé!"
echo ""
echo "🧪 Commandes utiles:"
echo "  GET:  curl http://localhost:8082/api/centres-collecte/$ID"
echo "  PUT:  curl -X PUT http://localhost:8082/api/centres-collecte/$ID ..."

