package com.example.hemolinkbackend.dto.request;

import com.example.hemolinkbackend.enums.GroupeSanguin;

import java.time.LocalDate;

public record StatistiquesStockDto(
        GroupeSanguin groupeSanguin,
        Integer unitesTotales,
        Integer unitesExpirees,
        Integer unitesTransfusees,
        LocalDate dateGeneration
) {
}

