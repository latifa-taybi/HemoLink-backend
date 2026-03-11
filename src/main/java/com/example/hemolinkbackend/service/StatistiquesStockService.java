package com.example.hemolinkbackend.service;

import com.example.hemolinkbackend.dto.response.StatistiquesStockResponseDto;
import com.example.hemolinkbackend.enums.GroupeSanguin;

import java.time.LocalDate;
import java.util.List;

public interface StatistiquesStockService {

    List<StatistiquesStockResponseDto> genererSnapshotJournalier();

    StatistiquesStockResponseDto getById(Long id);

    List<StatistiquesStockResponseDto> getAll();

    List<StatistiquesStockResponseDto> getParDate(LocalDate date);

    StatistiquesStockResponseDto getDernierParGroupe(GroupeSanguin groupeSanguin);

    List<StatistiquesStockResponseDto> getDernierGlobal();
}
