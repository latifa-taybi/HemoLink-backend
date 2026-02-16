package com.example.hemolinkbackend.dto.response;

import com.example.hemolinkbackend.enums.GroupeSanguin;
import com.example.hemolinkbackend.enums.StatutSang;

import java.time.LocalDate;

public record PocheSangResponseDto(
        Long id,
        Long donId,
        GroupeSanguin groupeSanguin,
        LocalDate dateCollecte,
        LocalDate dateExpiration,
        StatutSang statut
) {
}

