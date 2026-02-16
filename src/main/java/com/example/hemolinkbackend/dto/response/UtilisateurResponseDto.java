package com.example.hemolinkbackend.dto.response;

import com.example.hemolinkbackend.enums.RoleUtilisateur;

import java.time.LocalDateTime;

public record UtilisateurResponseDto(
        Long id,
        String prenom,
        String nom,
        String email,
        String telephone,
        RoleUtilisateur role,
        Boolean actif,
        LocalDateTime creeLe
) {
}

