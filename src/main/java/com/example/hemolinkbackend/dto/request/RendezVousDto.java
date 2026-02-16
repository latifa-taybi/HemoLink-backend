package com.example.hemolinkbackend.dto.request;

import com.example.hemolinkbackend.enums.StatutRendezVous;

import java.time.LocalDateTime;

public record RendezVousDto(
        Long donneurId,
        Long centreId,
        LocalDateTime dateRendezVous,
        StatutRendezVous statut
) {
}

