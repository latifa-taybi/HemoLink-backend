package com.example.hemolinkbackend.dto.request;

public record InscriptionDto(
        String prenom,
        String nom,
        String email,
        String motDePasse,
        String telephone
) {
}

