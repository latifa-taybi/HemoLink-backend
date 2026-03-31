package com.example.hemolinkbackend.service.impl;

import com.example.hemolinkbackend.dto.request.DonDto;
import com.example.hemolinkbackend.dto.response.DonResponseDto;
import com.example.hemolinkbackend.entity.Don;
import com.example.hemolinkbackend.entity.Donneur;
import com.example.hemolinkbackend.mapper.DonMapper;
import com.example.hemolinkbackend.repository.CentreCollecteRepository;
import com.example.hemolinkbackend.repository.DonRepository;
import com.example.hemolinkbackend.repository.DonneurRepository;
import com.example.hemolinkbackend.service.DonService;
import com.example.hemolinkbackend.service.NotificationService;
import com.example.hemolinkbackend.service.PocheSangService;
import com.example.hemolinkbackend.service.exception.RessourceNonTrouveeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@Slf4j
public class DonServiceImpl implements DonService {

    private static final int DELAI_CARENCE_SEMAINES = 8;
    private static final int QUOTA_ANNUEL = 4;

    private final DonRepository donRepository;
    private final CentreCollecteRepository centreCollecteRepository;
    private final DonneurRepository donneurRepository;
    private final DonMapper donMapper;
    private final NotificationService notificationService;
    private final PocheSangService pocheSangService;

    @Autowired
    public DonServiceImpl(DonRepository donRepository,
                          CentreCollecteRepository centreCollecteRepository,
                          DonneurRepository donneurRepository,
                          DonMapper donMapper,
                          NotificationService notificationService,
                          @Lazy PocheSangService pocheSangService) {
        this.donRepository = donRepository;
        this.centreCollecteRepository = centreCollecteRepository;
        this.donneurRepository = donneurRepository;
        this.donMapper = donMapper;
        this.notificationService = notificationService;
        this.pocheSangService = pocheSangService;
    }

    @Override
    public DonResponseDto enregistrerDon(DonDto dto) {
        log.info("Enregistrement d'un don pour utilisateurId={}, centreId={}", dto.donneurId(), dto.centreId());
        
        // Validate centre exists
        if (!centreCollecteRepository.existsById(dto.centreId())) {
            throw new RessourceNonTrouveeException("Le centre de collecte (ID: " + dto.centreId() + ") n'existe pas.");
        }
        
        // The frontend passes utilisateurId - we must resolve it to the donneur profile ID
        Donneur donneur = donneurRepository.findByUtilisateurId(dto.donneurId())
                .orElseThrow(() -> new RessourceNonTrouveeException(
                        "Aucun profil donneur trouvé pour l'utilisateur ID: " + dto.donneurId() +
                        ". Vérifiez que cet utilisateur a bien le rôle DONNEUR."));
        
        // Build and save the don with the REAL donneur.id
        Don don = donMapper.toEntity(dto);
        don.setDonneur(donneur); // override the mapped ID with the real entity
        Don saved = donRepository.save(don);
        log.info("Don enregistré avec ID: {}", saved.getId());
        
        // Auto-create blood bag for the lab
        try {
            pocheSangService.creerDepuisDon(saved.getId());
            log.info("Poche de sang créée automatiquement pour le don ID: {}", saved.getId());
        } catch (Exception e) {
            log.error("Erreur lors de la création automatique de la poche de sang: {}", e.getMessage());
        }
        
        // Notify the donor
        if (donneur.getUtilisateur() != null) {
            try {
                notificationService.notifierUtilisateur(
                    donneur.getUtilisateur().getId(),
                    "Votre don de sang a été enregistré avec succès. Merci pour votre générosité !"
                );
            } catch (Exception e) {
                log.warn("Notification au donneur échouée: {}", e.getMessage());
            }
        }
        
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

