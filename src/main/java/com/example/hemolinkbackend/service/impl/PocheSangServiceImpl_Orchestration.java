package com.example.hemolinkbackend.service.impl;

import com.example.hemolinkbackend.dto.request.PocheSangDto;
import com.example.hemolinkbackend.dto.response.PocheSangResponseDto;
import com.example.hemolinkbackend.entity.Don;
import com.example.hemolinkbackend.entity.PocheSang;
import com.example.hemolinkbackend.enums.GroupeSanguin;
import com.example.hemolinkbackend.enums.StatutSang;
import com.example.hemolinkbackend.event.StockChangeEvent;
import com.example.hemolinkbackend.mapper.PocheSangMapper;
import com.example.hemolinkbackend.repository.DonRepository;
import com.example.hemolinkbackend.repository.PocheSangRepository;
import com.example.hemolinkbackend.service.PocheSangService;
import com.example.hemolinkbackend.service.exception.RegleMetierException;
import com.example.hemolinkbackend.service.exception.RessourceNonTrouveeException;
import com.example.hemolinkbackend.service.support.CompatibiliteSanguineUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class PocheSangServiceImpl implements PocheSangService {

    private final PocheSangRepository pocheSangRepository;
    private final DonRepository donRepository;
    private final PocheSangMapper pocheSangMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public PocheSangResponseDto creer(PocheSangDto dto) {
        log.info("Création d'une poche de sang pour le don ID: {}", dto.donId());

        Don don = donRepository.findById(dto.donId())
                .orElseThrow(() -> {
                    log.error("Don non trouvé avec ID: {}", dto.donId());
                    return new RessourceNonTrouveeException("Don introuvable : " + dto.donId());
                });

        PocheSang poche = new PocheSang();
        poche.setDon(don);
        poche.setGroupeSanguin(dto.groupeSanguin());
        poche.setDateCollecte(dto.dateCollecte());
        poche.setDateExpiration(dto.dateExpiration());
        poche.setStatut(StatutSang.EN_ATTENTE_TEST);

        PocheSang pocheSaved = pocheSangRepository.save(poche);
        log.info("Poche créée avec ID: {}, Statut: EN_ATTENTE_TEST", pocheSaved.getId());

        return pocheSangMapper.toResponseDto(pocheSaved);
    }

    @Override
    @Transactional(readOnly = true)
    public PocheSangResponseDto getById(Long id) {
        log.debug("Récupération de la poche ID: {}", id);
        return pocheSangMapper.toResponseDto(getEntityById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PocheSangResponseDto> getAll() {
        log.debug("Récupération de toutes les poches");
        return pocheSangRepository.findAll().stream().map(pocheSangMapper::toResponseDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PocheSangResponseDto> getDisponiblesParGroupe(GroupeSanguin groupeSanguin) {
        log.debug("Récupération des poches disponibles du groupe: {}", groupeSanguin);
        return pocheSangRepository.findByStatutAndGroupeSanguinOrderByDateExpirationAsc(
                StatutSang.DISPONIBLE, groupeSanguin)
                .stream()
                .map(pocheSangMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PocheSangResponseDto> getPochesSansTest() {
        log.debug("Récupération des poches sans test");
        return pocheSangRepository.trouverSansTestParStatut(StatutSang.EN_ATTENTE_TEST)
                .stream()
                .map(pocheSangMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PocheSangResponseDto> getPochesExpirees() {
        log.debug("Récupération des poches expirées");
        LocalDate aujourdHui = LocalDate.now();
        return pocheSangRepository.findByStatutAndDateExpirationBefore(StatutSang.DISPONIBLE, aujourdHui)
                .stream()
                .map(pocheSangMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PocheSangResponseDto> getCompatiblesFefo(GroupeSanguin groupeReceveur, Integer quantite) {
        log.debug("Recherche des poches compatibles pour le groupe: {}, Quantité: {}", groupeReceveur, quantite);

        List<GroupeSanguin> groupesCompatibles = CompatibiliteSanguineUtils.groupesCompatiblesPourReceveur(groupeReceveur);
        List<PocheSang> poches = pocheSangRepository.trouverCompatiblesFefo(
                StatutSang.DISPONIBLE,
                groupesCompatibles,
                LocalDate.now()
        );

        if (quantite != null && poches.size() < quantite) {
            log.warn("Stock insuffisant pour le groupe {}: demandé {}, disponible {}", groupeReceveur, quantite, poches.size());
            throw new RegleMetierException("Stock insuffisant pour couvrir la demande");
        }

        return poches.stream().map(pocheSangMapper::toResponseDto).toList();
    }

    @Override
    @Transactional
    public List<PocheSangResponseDto> reserverCompatiblesFefo(GroupeSanguin groupeReceveur, Integer quantite) {
        log.info("Réservation de {} poches pour le groupe: {}", quantite, groupeReceveur);

        // ✅ VERROU PESSIMISTE: Bloquer pour éviter race condition
        List<GroupeSanguin> groupesCompatibles = CompatibiliteSanguineUtils.groupesCompatiblesPourReceveur(groupeReceveur);

        List<PocheSang> poches = pocheSangRepository.findCompatiblesWithLock(
                StatutSang.DISPONIBLE,
                groupesCompatibles,
                LocalDate.now()
        );

        if (poches.size() < quantite) {
            log.error("Stock insuffisant pour la réservation - demandé: {}, disponible: {}", quantite, poches.size());
            throw new RegleMetierException("Stock insuffisant pour couvrir la demande");
        }

        // ✅ Sélectionner les poches à réserver (FEFO)
        List<PocheSang> pochesAReserver = poches.stream()
                .limit(quantite)
                .peek(poche -> {
                    // ✅ BLOCAGE MÉTIER: Vérifier le statut
                    if (poche.getStatut() != StatutSang.DISPONIBLE) {
                        log.error("Tentative de réservation d'une poche non disponible - Statut: {}", poche.getStatut());
                        throw new RegleMetierException("La poche n'est pas disponible pour la réservation");
                    }
                    log.debug("Réservation de la poche ID: {}", poche.getId());
                })
                .toList();

        // Mettre à jour les statuts et publier événement
        List<PocheSang> pochesReservees = pochesAReserver.stream()
                .map(poche -> {
                    poche.setStatut(StatutSang.RESERVEE);
                    PocheSang result = pocheSangRepository.save(poche);

                    // ✅ Publier événement StockChangeEvent
                    StockChangeEvent event = new StockChangeEvent(
                            this,
                            poche.getGroupeSanguin(),
                            "RESERVED",
                            poche.getId()
                    );
                    eventPublisher.publishEvent(event);

                    log.info("Poche ID: {} réservée avec succès", result.getId());
                    return result;
                })
                .toList();

        log.info("{} poches réservées avec succès", pochesReservees.size());
        return pochesReservees.stream().map(pocheSangMapper::toResponseDto).toList();
    }

    @Override
    @Transactional
    public PocheSangResponseDto changerStatut(Long id, StatutSang nouveauStatut) {
        log.info("Changement du statut de la poche ID: {} vers: {}", id, nouveauStatut);

        // ✅ VERROU PESSIMISTE: Blocker la poche pour éviter modifications simultanées
        PocheSang poche = pocheSangRepository.findByIdWithLock(id)
                .orElseThrow(() -> {
                    log.error("Poche non trouvée avec ID: {}", id);
                    return new RessourceNonTrouveeException("Poche introuvable : " + id);
                });

        StatutSang ancienStatut = poche.getStatut();
        poche.setStatut(nouveauStatut);
        PocheSang pocheSaved = pocheSangRepository.save(poche);

        // ✅ Publier événement StockChangeEvent
        StockChangeEvent event = new StockChangeEvent(
                this,
                poche.getGroupeSanguin(),
                nouveauStatut.name(),
                poche.getId()
        );
        eventPublisher.publishEvent(event);

        log.info("Statut de la poche ID: {} changé de {} vers {}", id, ancienStatut, nouveauStatut);
        return pocheSangMapper.toResponseDto(pocheSaved);
    }

    private PocheSang getEntityById(Long id) {
        return pocheSangRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Poche non trouvée avec ID: {}", id);
                    return new RessourceNonTrouveeException("Poche introuvable : " + id);
                });
    }

    @Override
    @Transactional
    public PocheSangResponseDto creerDepuisDon(Long donId) {
        log.info("Création d'une poche de sang pour le don ID: {}", donId);

        Don don = donRepository.findById(donId)
                .orElseThrow(() -> {
                    log.error("Don non trouvé avec ID: {}", donId);
                    return new RessourceNonTrouveeException("Don introuvable : " + donId);
                });

        PocheSang poche = new PocheSang();
        poche.setDon(don);
        poche.setGroupeSanguin(don.getDonneur().getGroupeSanguin());
        poche.setDateCollecte(LocalDate.now());
        poche.setDateExpiration(LocalDate.now().plusDays(42));
        poche.setStatut(StatutSang.EN_ATTENTE_TEST);

        PocheSang pocheSaved = pocheSangRepository.save(poche);
        log.info("Poche créée avec ID: {}, Statut: EN_ATTENTE_TEST", pocheSaved.getId());

        return pocheSangMapper.toResponseDto(pocheSaved);
    }

    @Override
    @Transactional
    public PocheSangResponseDto marquerCommeTransfusee(Long id) {
        log.info("Marquage de la poche ID: {} comme transfusée", id);

        PocheSang poche = pocheSangRepository.findByIdWithLock(id)
                .orElseThrow(() -> {
                    log.error("Poche non trouvée avec ID: {}", id);
                    return new RessourceNonTrouveeException("Poche introuvable : " + id);
                });

        poche.setStatut(StatutSang.TRANSFUSEE);
        PocheSang pocheSaved = pocheSangRepository.save(poche);

        StockChangeEvent event = new StockChangeEvent(
                this,
                poche.getGroupeSanguin(),
                "TRANSFUSED",
                poche.getId()
        );
        eventPublisher.publishEvent(event);

        log.info("Poche ID: {} marquée comme transfusée", id);
        return pocheSangMapper.toResponseDto(pocheSaved);
    }

    @Override
    @Transactional
    public PocheSangResponseDto marquerCommeEcartee(Long id) {
        log.info("Marquage de la poche ID: {} comme écartée", id);

        PocheSang poche = pocheSangRepository.findByIdWithLock(id)
                .orElseThrow(() -> {
                    log.error("Poche non trouvée avec ID: {}", id);
                    return new RessourceNonTrouveeException("Poche introuvable : " + id);
                });

        poche.setStatut(StatutSang.ECARTEE);
        PocheSang pocheSaved = pocheSangRepository.save(poche);

        StockChangeEvent event = new StockChangeEvent(
                this,
                poche.getGroupeSanguin(),
                "REJECTED",
                poche.getId()
        );
        eventPublisher.publishEvent(event);

        log.info("Poche ID: {} marquée comme écartée", id);
        return pocheSangMapper.toResponseDto(pocheSaved);
    }
}
