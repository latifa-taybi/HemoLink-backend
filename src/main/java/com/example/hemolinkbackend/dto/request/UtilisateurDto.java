package com.example.hemolinkbackend.dto.request;

import com.example.hemolinkbackend.enums.RoleUtilisateur;

public record UtilisateurDto(
        String prenom,
        String nom,
        String email,
        String motDePasse,
        String telephone,
        RoleUtilisateur role,
        Long centreCollecteId,
        Long hopitalId,
        Boolean actif
) {
}

