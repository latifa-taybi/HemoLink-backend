package com.example.hemolinkbackend.dto.response;
import java.util.List;

public record CentreCollecteResponseDto(
        Long id,
        String nom,
        String adresse,
        String ville,
        Double latitude,
        Double longitude,
        List<HoraireResponseDto> horaires,
        String telephone
) {
}

