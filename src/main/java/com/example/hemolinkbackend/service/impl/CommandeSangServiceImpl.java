package com.example.hemolinkbackend.service.impl;

import com.example.hemolinkbackend.dto.request.CommandeSangDto;
import com.example.hemolinkbackend.dto.response.CommandeSangResponseDto;
import com.example.hemolinkbackend.entity.CentreCollecte;
import com.example.hemolinkbackend.entity.CommandeSang;
import com.example.hemolinkbackend.entity.Hopital;
import com.example.hemolinkbackend.entity.Utilisateur;
import com.example.hemolinkbackend.enums.RoleUtilisateur;
import com.example.hemolinkbackend.enums.StatutCommande;
import com.example.hemolinkbackend.mapper.CommandeSangMapper;
import com.example.hemolinkbackend.repository.CentreCollecteRepository;
import com.example.hemolinkbackend.repository.CommandeSangRepository;
import com.example.hemolinkbackend.repository.HopitalRepository;
import com.example.hemolinkbackend.repository.UtilisateurRepository;
import com.example.hemolinkbackend.service.CommandeSangService;
import com.example.hemolinkbackend.service.NotificationService;
import com.example.hemolinkbackend.service.exception.RegleMetierException;
import com.example.hemolinkbackend.service.exception.RessourceNonTrouveeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CommandeSangServiceImpl implements CommandeSangService {

    private final CommandeSangRepository commandeSangRepository;
    private final CommandeSangMapper commandeSangMapper;
    private final HopitalRepository hopitalRepository;
    private final CentreCollecteRepository centreCollecteRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final NotificationService notificationService;

    @Override
    public CommandeSangResponseDto creer(CommandeSangDto dto) {
        log.info("Création d'une commande de sang par l'hôpital ID: {}", dto.hopitalId());

        // Valider l'hôpital
        Hopital hopital = hopitalRepository.findById(dto.hopitalId())
                .orElseThrow(() -> {
                    log.error("Hôpital non trouvé avec ID: {}", dto.hopitalId());
                    return new RessourceNonTrouveeException("Hôpital introuvable avec l'ID : " + dto.hopitalId());
                });

        // Valider le centre de collecte cible (obligatoire)
        if (dto.centreCollecteId() == null) {
            throw new RegleMetierException("Un centre de collecte doit être spécifié pour traiter la commande.");
        }
        CentreCollecte centre = centreCollecteRepository.findById(dto.centreCollecteId())
                .orElseThrow(() -> {
                    log.error("Centre de collecte non trouvé avec ID: {}", dto.centreCollecteId());
                    return new RessourceNonTrouveeException("Centre de collecte introuvable : " + dto.centreCollecteId());
                });

        CommandeSang commande = new CommandeSang();
        commande.setHopital(hopital);
        commande.setCentreCollecte(centre);
        commande.setGroupeSanguin(dto.groupeSanguin());
        commande.setQuantite(dto.quantite());
        commande.setUrgence(dto.urgence() != null && dto.urgence());
        commande.setStatut(StatutCommande.EN_ATTENTE);
        commande.setDateCommande(dto.dateCommande() != null ? dto.dateCommande() : LocalDateTime.now());

        CommandeSang saved = commandeSangRepository.save(commande);
        log.info("Commande {} créée → envoyée au centre {} ({})", saved.getId(), centre.getNom(), centre.getId());

        // Notifier tous les TECHNICIEN_LABO
        String urgenceTag = Boolean.TRUE.equals(saved.getUrgence()) ? "🚨 URGENT - " : "";
        String message = urgenceTag + "Nouvelle commande de sang reçue de l'hôpital « " + hopital.getNom()
                + " » : " + saved.getGroupeSanguin() + " x" + saved.getQuantite()
                + " unité(s). Centre concerné : " + centre.getNom() + ".";

        List<Utilisateur> techniciens = utilisateurRepository.findByRole(RoleUtilisateur.TECHNICIEN_LABO);
        for (Utilisateur tech : techniciens) {
            notificationService.notifierUtilisateur(tech.getId(), message);
        }
        log.info("{} technicien(s) notifié(s) pour la commande {}", techniciens.size(), saved.getId());

        return commandeSangMapper.toResponseDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public CommandeSangResponseDto getById(Long id) {
        log.debug("Récupération de la commande ID: {}", id);
        return commandeSangMapper.toResponseDto(getEntityById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommandeSangResponseDto> getAll() {
        log.debug("Récupération de toutes les commandes");
        return commandeSangRepository.findAll().stream().map(commandeSangMapper::toResponseDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommandeSangResponseDto> getByHopital(Long hopitalId) {
        log.debug("Récupération des commandes de l'hôpital ID: {}", hopitalId);
        return commandeSangRepository.findByHopitalIdOrderByDateCommandeDesc(hopitalId)
                .stream()
                .map(commandeSangMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommandeSangResponseDto> getParCentre(Long centreCollecteId) {
        log.debug("Récupération des commandes du centre ID: {}", centreCollecteId);
        return commandeSangRepository.findByCentreCollecteIdOrderByDateCommandeDesc(centreCollecteId)
                .stream()
                .map(commandeSangMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommandeSangResponseDto> getUrgencesActives() {
        log.debug("Récupération des commandes urgentes actives");
        List<StatutCommande> statutsActifs = List.of(StatutCommande.EN_ATTENTE, StatutCommande.EN_PREPARATION);
        return commandeSangRepository.trouverUrgencesActives(statutsActifs)
                .stream()
                .map(commandeSangMapper::toResponseDto)
                .toList();
    }

    @Override
    public CommandeSangResponseDto preparerCommande(Long id) {
        log.info("Préparation de la commande ID: {}", id);
        CommandeSang commande = getEntityById(id);
        validerTransition(commande.getStatut(), StatutCommande.EN_ATTENTE, "préparer");
        commande.setStatut(StatutCommande.EN_PREPARATION);
        CommandeSang saved = commandeSangRepository.save(commande);
        notifierHopital(saved, "Votre commande #" + saved.getId() + " est en cours de préparation.");
        return commandeSangMapper.toResponseDto(saved);
    }

    @Override
    public CommandeSangResponseDto expedierCommande(Long id) {
        log.info("Expédition de la commande ID: {}", id);
        CommandeSang commande = getEntityById(id);
        validerTransition(commande.getStatut(), StatutCommande.EN_PREPARATION, "expédier");
        commande.setStatut(StatutCommande.EN_LIVRAISON);
        CommandeSang saved = commandeSangRepository.save(commande);
        notifierHopital(saved, "Votre commande #" + saved.getId() + " est en cours de livraison.");
        return commandeSangMapper.toResponseDto(saved);
    }

    @Override
    public CommandeSangResponseDto livrerCommande(Long id) {
        log.info("Livraison de la commande ID: {}", id);
        CommandeSang commande = getEntityById(id);
        validerTransition(commande.getStatut(), StatutCommande.EN_LIVRAISON, "livrer");
        commande.setStatut(StatutCommande.LIVREE);
        CommandeSang saved = commandeSangRepository.save(commande);
        notifierHopital(saved, "✅ Votre commande #" + saved.getId() + " a été livrée avec succès.");
        return commandeSangMapper.toResponseDto(saved);
    }

    @Override
    public CommandeSangResponseDto annulerCommande(Long id) {
        log.warn("Annulation de la commande ID: {}", id);
        CommandeSang commande = getEntityById(id);
        if (commande.getStatut() == StatutCommande.LIVREE) {
            throw new RegleMetierException("Impossible d'annuler une commande déjà livrée.");
        }
        commande.setStatut(StatutCommande.ANNULEE);
        CommandeSang saved = commandeSangRepository.save(commande);
        notifierHopital(saved, "❌ Votre commande #" + saved.getId() + " a été annulée.");
        return commandeSangMapper.toResponseDto(saved);
    }

    // ─── Helpers privés ──────────────────────────────────────────────

    private CommandeSang getEntityById(Long id) {
        return commandeSangRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Commande non trouvée avec ID: {}", id);
                    return new RessourceNonTrouveeException("Commande introuvable : " + id);
                });
    }

    /** Vérifie que la commande est dans le statut requis avant une transition. */
    private void validerTransition(StatutCommande actuel, StatutCommande requis, String action) {
        if (actuel != requis) {
            throw new RegleMetierException(
                    "Impossible de " + action + " une commande en statut « " + actuel + " ». Statut requis : « " + requis + " ».");
        }
    }

    /** Notifie le personnel de l'hôpital lié à la commande, si disponible. */
    private void notifierHopital(CommandeSang commande, String message) {
        if (commande.getHopital() == null) return;
        List<Utilisateur> personnel = utilisateurRepository.findByRole(RoleUtilisateur.PERSONNEL_HOPITAL);
        for (Utilisateur u : personnel) {
            notificationService.notifierUtilisateur(u.getId(), message);
        }
    }
}
