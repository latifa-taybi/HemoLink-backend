package com.example.hemolinkbackend.dto.request;
import java.util.List;
import com.example.hemolinkbackend.dto.request.HoraireDto;

public record CentreCollecteDto(
        String nom,
        String adresse,
        String ville,
        Double latitude,
        Double longitude,
        List<HoraireDto> horaires,
        String telephone
) {
}

