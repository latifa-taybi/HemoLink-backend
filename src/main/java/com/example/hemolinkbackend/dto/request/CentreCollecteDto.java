package com.example.hemolinkbackend.dto.request;

public record CentreCollecteDto(
        String nom,
        String adresse,
        String ville,
        Double latitude,
        Double longitude,
        String horairesOuverture,
        String telephone
) {
}

