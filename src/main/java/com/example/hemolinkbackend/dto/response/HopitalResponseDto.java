package com.example.hemolinkbackend.dto.response;

public record HopitalResponseDto(
        Long id,
        String nom,
        String adresse,
        String ville,
        String telephone
) {
}

