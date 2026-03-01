package com.example.hemolinkbackend.service.impl;

import com.example.hemolinkbackend.dto.request.DonneurDto;
import com.example.hemolinkbackend.dto.response.DonneurResponseDto;
import com.example.hemolinkbackend.entity.Donneur;
import com.example.hemolinkbackend.entity.Utilisateur;
import com.example.hemolinkbackend.enums.GroupeSanguin;
import com.example.hemolinkbackend.mapper.DonneurMapper;
import com.example.hemolinkbackend.repository.DonRepository;
import com.example.hemolinkbackend.repository.DonneurRepository;
import com.example.hemolinkbackend.repository.UtilisateurRepository;
import com.example.hemolinkbackend.service.DonneurService;
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
public class DonneurServiceImpl implements DonneurService {

    private static final int DELAI_CARENCE_SEMAINES = 8;
    private static final int QUOTA_ANNUEL = 4;

    private final DonneurRepository donneurRepository;
    private final DonRepository donRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final DonneurMapper donneurMapper;

    @Override
    public DonneurResponseDto creer(DonneurDto dto) {
        Utilisateur utilisateur = getUtilisateurById(dto.utilisateurId());
        donneurRepository.findByUtilisateurId(dto.utilisateurId()).ifPresent(existing -> {
            throw new RegleMetierException("Ce profil utilisateur possede deja un dossier donneur.");
        });
        Donneur donneur = donneurMapper.toEntity(dto);
        donneur.setUtilisateur(utilisateur);
        donneur.setNombreDonsAnnuel(dto.nombreDonsAnnuel() != null ? dto.nombreDonsAnnuel() : 0);
        return donneurMapper.toResponseDto(donneurRepository.save(donneur));
    }

    @Override
    public DonneurResponseDto mettreAJour(Long id, DonneurDto dto) {
        Donneur donneur = getEntityById(id);
        Integer nombreDonsActuel = donneur.getNombreDonsAnnuel();
        if (dto.utilisateurId() != null && !dto.utilisateurId().equals(donneur.getUtilisateur().getId())) {
            donneurRepository.findByUtilisateurId(dto.utilisateurId()).ifPresent(existing -> {
                throw new RegleMetierException("Ce profil utilisateur est deja rattache a un autre donneur.");
            });
        }
        donneurMapper.updateEntity(dto, donneur);
        if (dto.utilisateurId() != null) {
            donneur.setUtilisateur(getUtilisateurById(dto.utilisateurId()));
        }
        if (dto.nombreDonsAnnuel() == null) {
            donneur.setNombreDonsAnnuel(nombreDonsActuel);
        }
        return donneurMapper.toResponseDto(donneurRepository.save(donneur));
    }

    @Override
    @Transactional(readOnly = true)
    public DonneurResponseDto getById(Long id) {
        return donneurMapper.toResponseDto(getEntityById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DonneurResponseDto> getAll() {
        return donneurRepository.findAll().stream().map(donneurMapper::toResponseDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public DonneurResponseDto getByUtilisateurId(Long utilisateurId) {
        return donneurMapper.toResponseDto(donneurRepository.findByUtilisateurId(utilisateurId)
                .orElseThrow(() -> new RessourceNonTrouveeException("Donneur introuvable pour l'utilisateur : " + utilisateurId)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DonneurResponseDto> getByGroupeSanguin(GroupeSanguin groupeSanguin) {
        return donneurRepository.findByGroupeSanguin(groupeSanguin).stream().map(donneurMapper::toResponseDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DonneurResponseDto> rechercherParNomOuPrenom(String motCle) {
        return donneurRepository.findByUtilisateurNomContainingIgnoreCaseOrUtilisateurPrenomContainingIgnoreCase(motCle, motCle)
                .stream()
                .map(donneurMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DonneurResponseDto> getDonneursEligiblesParGroupe(GroupeSanguin groupeSanguin) {
        return donneurRepository.findByGroupeSanguin(groupeSanguin).stream()
                .filter(donneur -> verifierEligibilite(donneur.getId()))
                .map(donneurMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean verifierEligibilite(Long donneurId) {
        Donneur donneur = getEntityById(donneurId);
        if (compterDonsAnneeEnCours(donneurId) >= QUOTA_ANNUEL) {
            return false;
        }
        return donneur.getDateDernierDon() == null || !donneur.getDateDernierDon().plusWeeks(DELAI_CARENCE_SEMAINES).isAfter(LocalDate.now());
    }

    @Override
    @Transactional(readOnly = true)
    public LocalDate calculerProchaineDateEligible(Long donneurId) {
        Donneur donneur = getEntityById(donneurId);
        if (donneur.getDateDernierDon() == null) {
            return LocalDate.now();
        }
        return donneur.getDateDernierDon().plusWeeks(DELAI_CARENCE_SEMAINES);
    }

    @Override
    public void supprimer(Long id) {
        donneurRepository.delete(getEntityById(id));
    }

    private long compterDonsAnneeEnCours(Long donneurId) {
        LocalDateTime debut = LocalDate.now().withDayOfYear(1).atStartOfDay();
        LocalDateTime fin = debut.plusYears(1);
        return donRepository.countByDonneurIdAndDateDonBetween(donneurId, debut, fin);
    }

    private Donneur getEntityById(Long id) {
        return donneurRepository.findById(id)
                .orElseThrow(() -> new RessourceNonTrouveeException("Donneur introuvable : " + id));
    }

    private Utilisateur getUtilisateurById(Long id) {
        return utilisateurRepository.findById(id)
                .orElseThrow(() -> new RessourceNonTrouveeException("Utilisateur introuvable : " + id));
    }
}
