package com.example.hemolinkbackend.service.impl;

import com.example.hemolinkbackend.dto.request.StatistiquesStockDto;
import com.example.hemolinkbackend.dto.response.StatistiquesStockResponseDto;
import com.example.hemolinkbackend.entity.PocheSang;
import com.example.hemolinkbackend.entity.StatistiquesStock;
import com.example.hemolinkbackend.enums.GroupeSanguin;
import com.example.hemolinkbackend.enums.StatutSang;
import com.example.hemolinkbackend.mapper.StatistiquesStockMapper;
import com.example.hemolinkbackend.repository.PocheSangRepository;
import com.example.hemolinkbackend.repository.StatistiquesStockRepository;
import com.example.hemolinkbackend.service.StatistiquesStockService;
import com.example.hemolinkbackend.service.exception.RessourceNonTrouveeException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class StatistiquesStockServiceImpl implements StatistiquesStockService {

    private final StatistiquesStockRepository statistiquesStockRepository;
    private final PocheSangRepository pocheSangRepository;
    private final StatistiquesStockMapper statistiquesStockMapper;

    @Override
    public List<StatistiquesStockResponseDto> genererSnapshotJournalier() {
        LocalDate aujourdHui = LocalDate.now();
        List<StatistiquesStock> existants = statistiquesStockRepository.findByDateGeneration(aujourdHui);
        if (!existants.isEmpty()) {
            return existants.stream().map(statistiquesStockMapper::toResponseDto).toList();
        }

        List<PocheSang> poches = pocheSangRepository.findAll();
        return Arrays.stream(GroupeSanguin.values())
                .map(groupe -> construireSnapshot(aujourdHui, groupe, poches))
                .map(statistiquesStockRepository::save)
                .map(statistiquesStockMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public StatistiquesStockResponseDto getById(Long id) {
        return statistiquesStockMapper.toResponseDto(statistiquesStockRepository.findById(id)
                .orElseThrow(() -> new RessourceNonTrouveeException("Statistique introuvable : " + id)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<StatistiquesStockResponseDto> getAll() {
        return statistiquesStockRepository.findAll().stream().map(statistiquesStockMapper::toResponseDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<StatistiquesStockResponseDto> getParDate(LocalDate date) {
        return statistiquesStockRepository.findByDateGeneration(date).stream().map(statistiquesStockMapper::toResponseDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public StatistiquesStockResponseDto getDernierParGroupe(GroupeSanguin groupeSanguin) {
        return statistiquesStockMapper.toResponseDto(statistiquesStockRepository.findTopByGroupeSanguinOrderByDateGenerationDesc(groupeSanguin)
                .orElseThrow(() -> new RessourceNonTrouveeException("Aucune statistique disponible pour le groupe : " + groupeSanguin)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<StatistiquesStockResponseDto> getDernierGlobal() {
        return statistiquesStockRepository.trouverDernierSnapshotGlobal().stream().map(statistiquesStockMapper::toResponseDto).toList();
    }

    private StatistiquesStock construireSnapshot(LocalDate date, GroupeSanguin groupe, List<PocheSang> poches) {
        long total = poches.stream()
                .filter(poche -> poche.getGroupeSanguin() == groupe)
                .filter(poche -> poche.getStatut() != StatutSang.ECARTEE && poche.getStatut() != StatutSang.TRANSFUSEE)
                .count();
        long expirees = poches.stream()
                .filter(poche -> poche.getGroupeSanguin() == groupe)
                .filter(poche -> poche.getDateExpiration() != null && poche.getDateExpiration().isBefore(date))
                .count();
        long transfusees = poches.stream()
                .filter(poche -> poche.getGroupeSanguin() == groupe)
                .filter(poche -> poche.getStatut() == StatutSang.TRANSFUSEE)
                .count();

        StatistiquesStockDto dto = new StatistiquesStockDto(
                groupe,
                (int) total,
                (int) expirees,
                (int) transfusees,
                date
        );
        return statistiquesStockMapper.toEntity(dto);
    }
}
