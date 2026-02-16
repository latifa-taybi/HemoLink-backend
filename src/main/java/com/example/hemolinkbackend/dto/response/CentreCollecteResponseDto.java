package com.example.hemolinkbackend.dto.response;

public record CentreCollecteResponseDto(
        Long id,
        String nom,
        String adresse,
        String ville,
        Double latitude,
        Double longitude,
        String horairesOuverture,
        String telephone
) {
}

