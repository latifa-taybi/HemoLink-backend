package com.example.hemolinkbackend.dto.response;

public record ElementCommandeResponseDto(
        Long id,
        Long commandeId,
        Long pocheSangId
) {
}

