package com.example.hemolinkbackend.dto.response;

import com.example.hemolinkbackend.enums.GroupeSanguin;
import com.example.hemolinkbackend.enums.StatutCommande;

import java.time.LocalDateTime;

public record CommandeSangResponseDto(
        Long id,
        Long hopitalId,
        Long centreCollecteId,
        GroupeSanguin groupeSanguin,
        Integer quantite,
        Boolean urgence,
        StatutCommande statut,
        LocalDateTime dateCommande
) {
}

