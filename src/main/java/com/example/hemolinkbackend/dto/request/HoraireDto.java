package com.example.hemolinkbackend.dto.request;
import java.time.LocalTime;
public record HoraireDto(
        String jour,
        LocalTime ouverture,
        LocalTime fermeture,
        Boolean ouvert
) {
}
