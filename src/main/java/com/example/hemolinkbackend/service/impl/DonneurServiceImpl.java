package com.example.hemolinkbackend.service.impl;

import com.example.hemolinkbackend.dto.request.DonneurDto;
import com.example.hemolinkbackend.dto.response.DonneurResponseDto;
import com.example.hemolinkbackend.entity.Donneur;
import com.example.hemolinkbackend.enums.GroupeSanguin;
import com.example.hemolinkbackend.mapper.DonneurMapper;
import com.example.hemolinkbackend.repository.DonneurRepository;
import com.example.hemolinkbackend.repository.UtilisateurRepository;
import com.example.hemolinkbackend.entity.Utilisateur;
import com.example.hemolinkbackend.enums.RoleUtilisateur;
import com.example.hemolinkbackend.service.DonService;
import com.example.hemolinkbackend.service.DonneurService;
import com.example.hemolinkbackend.service.exception.RessourceNonTrouveeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class DonneurServiceImpl implements DonneurService {

    private static final int DELAI_CARENCE_SEMAINES = 8;
    private static final int QUOTA_ANNUEL = 4;

    private final DonneurRepository donneurRepository;
    private final DonneurMapper donneurMapper;
    private final DonService donService;
    private final UtilisateurRepository utilisateurRepository;

    @Override
    public DonneurResponseDto creer(DonneurDto dto) {
        log.info("Création d'un donneur");
        Donneur donneur = donneurMapper.toEntity(dto);
        Donneur saved = donneurRepository.save(donneur);
        return donneurMapper.toResponseDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public DonneurResponseDto getById(Long id) {
        log.debug("Récupération du donneur ID: {}", id);
        return donneurMapper.toResponseDto(getEntityById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DonneurResponseDto> getAll() {
        log.debug("Récupération de tous les donneurs");
        return donneurRepository.findAll().stream().map(donneurMapper::toResponseDto).toList();
    }

    @Override
    public DonneurResponseDto mettreAJour(Long id, DonneurDto dto) {
        log.info("Mise à jour du donneur ID: {}", id);
        Donneur donneur = getEntityById(id);
        donneurMapper.updateEntity(dto, donneur);
        Donneur updated = donneurRepository.save(donneur);
        return donneurMapper.toResponseDto(updated);
    }

    @Override
    public void supprimer(Long id) {
        log.warn("Suppression du donneur ID: {}", id);
        donneurRepository.delete(getEntityById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean verifierEligibilite(Long donneurId) {
        log.debug("Vérification d'éligibilité du donneur ID: {}", donneurId);
        return donService.verifierEligibilite(donneurId);
    }

    @Override
    @Transactional(readOnly = true)
    public LocalDate calculerProchaineDateEligible(Long donneurId) {
        log.debug("Calcul de la prochaine date éligible pour donneur ID: {}", donneurId);
        return donService.calculerProchaineDateEligible(donneurId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DonneurResponseDto> getDonneursEligiblesParGroupe(GroupeSanguin groupeSanguin) {
        log.debug("Récupération des donneurs éligibles du groupe: {}", groupeSanguin);
        return donneurRepository.findAll()
                .stream()
                .map(donneurMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DonneurResponseDto> rechercherParNomOuPrenom(String terme) {
        log.debug("Recherche de donneurs par nom ou prénom: {}", terme);
        return donneurRepository.findAll()
                .stream()
                .map(donneurMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DonneurResponseDto> getByGroupeSanguin(GroupeSanguin groupeSanguin) {
        log.debug("Récupération des donneurs du groupe: {}", groupeSanguin);
        return donneurRepository.findAll()
                .stream()
                .map(donneurMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional
    public DonneurResponseDto getByUtilisateurId(Long utilisateurId) {
        log.debug("Récupération du donneur par utilisateur ID: {}", utilisateurId);
        return donneurRepository.findByUtilisateurId(utilisateurId)
                .map(donneurMapper::toResponseDto)
                .orElseGet(() -> {
                    Utilisateur u = utilisateurRepository.findById(utilisateurId)
                            .orElseThrow(() -> new RessourceNonTrouveeException("Utilisateur non trouvé : " + utilisateurId));
                    if (u.getRole() == RoleUtilisateur.DONNEUR) {
                        log.info("Création automatique du profil donneur manquant pour l'utilisateur ID: {}", utilisateurId);
                        Donneur newDonneur = new Donneur();
                        newDonneur.setUtilisateur(u);
                        newDonneur.setNombreDonsAnnuel(0);
                        return donneurMapper.toResponseDto(donneurRepository.save(newDonneur));
                    }
                    throw new RessourceNonTrouveeException("Donneur non trouvé pour l'utilisateur : " + utilisateurId);
                });
    }

    private Donneur getEntityById(Long id) {
        return donneurRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Donneur non trouvé avec ID: {}", id);
                    return new RessourceNonTrouveeException("Donneur introuvable : " + id);
                });
    }
}

