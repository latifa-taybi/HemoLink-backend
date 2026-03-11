package com.example.hemolinkbackend.controller;

import com.example.hemolinkbackend.dto.response.StatistiquesStockResponseDto;
import com.example.hemolinkbackend.enums.GroupeSanguin;
import com.example.hemolinkbackend.service.StatistiquesStockService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/statistiques-stock")
@RequiredArgsConstructor
public class StatistiquesStockController {

    private final StatistiquesStockService statistiquesStockService;

    @PostMapping("/snapshots/generer")
    public List<StatistiquesStockResponseDto> genererSnapshotJournalier() {
        return statistiquesStockService.genererSnapshotJournalier();
    }

    @GetMapping("/{id}")
    public StatistiquesStockResponseDto getById(@PathVariable Long id) {
        return statistiquesStockService.getById(id);
    }

    @GetMapping
    public List<StatistiquesStockResponseDto> getAll(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return date != null ? statistiquesStockService.getParDate(date) : statistiquesStockService.getAll();
    }

    @GetMapping("/dernier")
    public List<StatistiquesStockResponseDto> getDernierGlobal() {
        return statistiquesStockService.getDernierGlobal();
    }

    @GetMapping("/dernier/{groupeSanguin}")
    public StatistiquesStockResponseDto getDernierParGroupe(@PathVariable GroupeSanguin groupeSanguin) {
        return statistiquesStockService.getDernierParGroupe(groupeSanguin);
    }
}

