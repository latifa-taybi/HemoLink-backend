package com.example.hemolinkbackend.dto.response;

import com.example.hemolinkbackend.enums.GroupeSanguin;

import java.time.LocalDate;

public record StatistiquesStockResponseDto(
        Long id,
        GroupeSanguin groupeSanguin,
        Integer unitesTotales,
        Integer unitesExpirees,
        Integer unitesTransfusees,
        LocalDate dateGeneration
) {
}

