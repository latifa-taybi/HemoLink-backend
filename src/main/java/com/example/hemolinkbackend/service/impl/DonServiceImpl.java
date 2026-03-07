package com.example.hemolinkbackend.service.impl;

import com.example.hemolinkbackend.dto.request.DonDto;
import com.example.hemolinkbackend.dto.response.DonResponseDto;
import com.example.hemolinkbackend.entity.CentreCollecte;
import com.example.hemolinkbackend.entity.Don;
import com.example.hemolinkbackend.entity.Donneur;
import com.example.hemolinkbackend.entity.PocheSang;
import com.example.hemolinkbackend.enums.StatutSang;
import com.example.hemolinkbackend.mapper.DonMapper;
import com.example.hemolinkbackend.repository.CentreCollecteRepository;
import com.example.hemolinkbackend.repository.DonRepository;
import com.example.hemolinkbackend.repository.DonneurRepository;
import com.example.hemolinkbackend.repository.PocheSangRepository;
import com.example.hemolinkbackend.service.DonService;
import com.example.hemolinkbackend.service.exception.RegleMetierException;
import com.example.hemolinkbackend.service.exception.RessourceNonTrouveeException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class DonServiceImpl implements DonService {

    private static final int DELAI_CARENCE_SEMAINES = 8;
    private static final int QUOTA_ANNUEL = 4;
    private static final int DUREE_CONSERVATION_JOURS = 42;

    private final DonRepository donRepository;
    private final DonneurRepository donneurRepository;
    private final CentreCollecteRepository centreCollecteRepository;
    private final PocheSangRepository pocheSangRepository;
    private final DonMapper donMapper;

    @Override
    public DonResponseDto enregistrerDon(DonDto dto) {
        Donneur donneur = getDonneurById(dto.donneurId());
        CentreCollecte centre = getCentreById(dto.centreId());
        if (!verifierEligibilite(donneur.getId())) {
            throw new RegleMetierException("Le donneur n'est pas eligible a un nouveau don.");
        }
        if (donneur.getGroupeSanguin() == null) {
            throw new RegleMetierException("Le groupe sanguin du donneur doit etre renseigne.");
        }

        Don don = donMapper.toEntity(dto);
        don.setDonneur(donneur);
        don.setCentre(centre);
        don.setDateDon(dto.dateDon() != null ? dto.dateDon() : LocalDateTime.now());

        Don saved = donRepository.save(don);

        long totalAnnee = compterDonsAnneeEnCours(donneur.getId());
        donneur.setDateDernierDon(saved.getDateDon().toLocalDate());
        donneur.setNombreDonsAnnuel((int) totalAnnee);
        donneurRepository.save(donneur);

        PocheSang poche = new PocheSang();
        poche.setDon(saved);
        poche.setGroupeSanguin(donneur.getGroupeSanguin());
        poche.setDateCollecte(saved.getDateDon().toLocalDate());
        poche.setDateExpiration(saved.getDateDon().toLocalDate().plusDays(DUREE_CONSERVATION_JOURS));
        poche.setStatut(StatutSang.EN_ATTENTE_TEST);
        pocheSangRepository.save(poche);

        return donMapper.toResponseDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public DonResponseDto getById(Long id) {
        return donMapper.toResponseDto(getEntityById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DonResponseDto> getAll() {
        return donRepository.findAll().stream().map(donMapper::toResponseDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DonResponseDto> getHistoriqueDonneur(Long donneurId) {
        return donRepository.findByDonneurIdOrderByDateDonDesc(donneurId).stream().map(donMapper::toResponseDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DonResponseDto> getParCentreEtPeriode(Long centreId, LocalDateTime debut, LocalDateTime fin) {
        return donRepository.findByCentreIdAndDateDonBetween(centreId, debut, fin).stream().map(donMapper::toResponseDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public long compterDonsAnneeEnCours(Long donneurId) {
        LocalDateTime debut = LocalDate.now().withDayOfYear(1).atStartOfDay();
        LocalDateTime fin = debut.plusYears(1);
        return donRepository.countByDonneurIdAndDateDonBetween(donneurId, debut, fin);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean verifierEligibilite(Long donneurId) {
        Donneur donneur = getDonneurById(donneurId);
        if (compterDonsAnneeEnCours(donneurId) >= QUOTA_ANNUEL) {
            return false;
        }
        return donneur.getDateDernierDon() == null || !donneur.getDateDernierDon().plusWeeks(DELAI_CARENCE_SEMAINES).isAfter(LocalDate.now());
    }

    @Override
    @Transactional(readOnly = true)
    public LocalDate calculerProchaineDateEligible(Long donneurId) {
        Donneur donneur = getDonneurById(donneurId);
        if (donneur.getDateDernierDon() == null) {
            return LocalDate.now();
        }
        return donneur.getDateDernierDon().plusWeeks(DELAI_CARENCE_SEMAINES);
    }

    private Don getEntityById(Long id) {
        return donRepository.findById(id)
                .orElseThrow(() -> new RessourceNonTrouveeException("Don introuvable : " + id));
    }

    private Donneur getDonneurById(Long id) {
        return donneurRepository.findById(id)
                .orElseThrow(() -> new RessourceNonTrouveeException("Donneur introuvable : " + id));
    }

    private CentreCollecte getCentreById(Long id) {
        return centreCollecteRepository.findById(id)
                .orElseThrow(() -> new RessourceNonTrouveeException("Centre de collecte introuvable : " + id));
    }
}
