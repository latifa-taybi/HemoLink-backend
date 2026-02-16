package com.example.hemolinkbackend.dto.request;

import com.example.hemolinkbackend.enums.GroupeSanguin;

import java.time.LocalDate;

public record DonneurDto(
        Long utilisateurId,
        GroupeSanguin groupeSanguin,
        LocalDate dateNaissance,
        Double poids,
        LocalDate dateDernierDon,
        Integer nombreDonsAnnuel
) {
}

