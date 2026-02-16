package com.example.hemolinkbackend.dto.response;

import com.example.hemolinkbackend.enums.GroupeSanguin;

import java.time.LocalDate;

public record DonneurResponseDto(
        Long id,
        Long utilisateurId,
        GroupeSanguin groupeSanguin,
        LocalDate dateNaissance,
        Double poids,
        LocalDate dateDernierDon,
        Integer nombreDonsAnnuel
) {
}

