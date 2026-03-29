package com.example.hemolinkbackend.dto.response;
import java.time.LocalTime;
public record HoraireResponseDto(
        Long id,
        String jour,
        LocalTime ouverture,
        LocalTime fermeture,
        Boolean ouvert
) {
}
