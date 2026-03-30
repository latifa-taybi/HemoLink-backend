package com.example.hemolinkbackend.dto.response;

import com.example.hemolinkbackend.enums.StatutRendezVous;

import java.time.LocalDateTime;

public record RendezVousResponseDto(
        Long id,
        Long donneurId,
        String donneurPrenom,
        String donneurNom,
        Long centreId,
        LocalDateTime dateRendezVous,
        StatutRendezVous statut
) {
}

