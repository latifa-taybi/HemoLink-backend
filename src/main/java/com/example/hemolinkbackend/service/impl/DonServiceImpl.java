package com.example.hemolinkbackend.service.impl;

import com.example.hemolinkbackend.dto.request.DonDto;
import com.example.hemolinkbackend.dto.response.DonResponseDto;
import com.example.hemolinkbackend.entity.Don;
import com.example.hemolinkbackend.mapper.DonMapper;
import com.example.hemolinkbackend.repository.DonRepository;
import com.example.hemolinkbackend.service.DonService;
import com.example.hemolinkbackend.service.exception.RessourceNonTrouveeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class DonServiceImpl implements DonService {

    private static final int DELAI_CARENCE_SEMAINES = 8;
    private static final int QUOTA_ANNUEL = 4;

    private final DonRepository donRepository;
    private final DonMapper donMapper;

    @Override
    public DonResponseDto enregistrerDon(DonDto dto) {
        log.info("Enregistrement d'un don");
        Don don = donMapper.toEntity(dto);
        Don saved = donRepository.save(don);
        return donMapper.toResponseDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public DonResponseDto getById(Long id) {
        log.debug("Récupération du don ID: {}", id);
        return donMapper.toResponseDto(getEntityById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DonResponseDto> getAll() {
        log.debug("Récupération de tous les dons");
        return donRepository.findAll().stream().map(donMapper::toResponseDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DonResponseDto> getHistoriqueDonneur(Long donneurId) {
        log.debug("Récupération de l'historique des dons du donneur ID: {}", donneurId);
        return donRepository.findByDonneurIdOrderByDateDonDesc(donneurId)
                .stream()
                .map(donMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DonResponseDto> getDonsByCentre(Long centreId) {
        log.debug("Récupération des dons du centre ID: {}", centreId);
        return donRepository.findByCentreId(centreId)
                .stream()
                .map(donMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DonResponseDto> getParCentreEtPeriode(Long centreId, LocalDateTime debut, LocalDateTime fin) {
        log.debug("Récupération des dons du centre ID: {}", centreId);
        return donRepository.findAll()
                .stream()
                .map(donMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public long compterDonsAnneeEnCours(Long donneurId) {
        log.debug("Comptage des dons de l'année en cours pour donneur ID: {}", donneurId);
        LocalDateTime debut = LocalDate.now().withDayOfYear(1).atStartOfDay();
        LocalDateTime fin = debut.plusYears(1);
        return donRepository.countByDonneurIdAndDateDonBetween(donneurId, debut, fin);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean verifierEligibilite(Long donneurId) {
        log.debug("Vérification d'éligibilité du donneur ID: {}", donneurId);
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public LocalDate calculerProchaineDateEligible(Long donneurId) {
        log.debug("Calcul de la prochaine date éligible pour donneur ID: {}", donneurId);
        return LocalDate.now();
    }

    private Don getEntityById(Long id) {
        return donRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Don non trouvé avec ID: {}", id);
                    return new RessourceNonTrouveeException("Don introuvable : " + id);
                });
    }
}

