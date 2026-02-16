package com.example.hemolinkbackend.dto.request;

import com.example.hemolinkbackend.enums.GroupeSanguin;
import com.example.hemolinkbackend.enums.StatutSang;

import java.time.LocalDate;

public record PocheSangDto(
        Long donId,
        GroupeSanguin groupeSanguin,
        LocalDate dateCollecte,
        LocalDate dateExpiration,
        StatutSang statut
) {
}

