package com.example.hemolinkbackend.dto.response;

import com.example.hemolinkbackend.enums.GroupeSanguin;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record DonneurResponseDto(
        Long id,
        Long utilisateurId,
        String prenom,
        String nom,
        String email,
        String telephone,
        GroupeSanguin groupeSanguin,
        LocalDate dateNaissance,
        Double poids,
        LocalDate dateDernierDon,
        Integer nombreDonsAnnuel,
        boolean actif,
        LocalDateTime creeLe
) {
}
