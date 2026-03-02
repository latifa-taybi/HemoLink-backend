package com.example.hemolinkbackend.service.impl;

import com.example.hemolinkbackend.dto.request.CommandeSangDto;
import com.example.hemolinkbackend.dto.response.CommandeSangResponseDto;
import com.example.hemolinkbackend.entity.CommandeSang;
import com.example.hemolinkbackend.entity.ElementCommande;
import com.example.hemolinkbackend.entity.Hopital;
import com.example.hemolinkbackend.entity.PocheSang;
import com.example.hemolinkbackend.enums.StatutCommande;
import com.example.hemolinkbackend.enums.StatutSang;
import com.example.hemolinkbackend.mapper.CommandeSangMapper;
import com.example.hemolinkbackend.repository.CommandeSangRepository;
import com.example.hemolinkbackend.repository.ElementCommandeRepository;
import com.example.hemolinkbackend.repository.HopitalRepository;
import com.example.hemolinkbackend.repository.PocheSangRepository;
import com.example.hemolinkbackend.service.CommandeSangService;
import com.example.hemolinkbackend.service.exception.RegleMetierException;
import com.example.hemolinkbackend.service.exception.RessourceNonTrouveeException;
import com.example.hemolinkbackend.service.support.CompatibiliteSanguineUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CommandeSangServiceImpl implements CommandeSangService {

    private final CommandeSangRepository commandeSangRepository;
    private final HopitalRepository hopitalRepository;
    private final ElementCommandeRepository elementCommandeRepository;
    private final PocheSangRepository pocheSangRepository;
    private final CommandeSangMapper commandeSangMapper;

    @Override
    public CommandeSangResponseDto creer(CommandeSangDto dto) {
        Hopital hopital = getHopitalById(dto.hopitalId());
        CommandeSang commande = commandeSangMapper.toEntity(dto);
        commande.setHopital(hopital);
        commande.setUrgence(dto.urgence() != null && dto.urgence());
        commande.setDateCommande(dto.dateCommande() != null ? dto.dateCommande() : LocalDateTime.now());
        commande.setStatut(dto.statut() != null ? dto.statut() : StatutCommande.EN_ATTENTE);
        return commandeSangMapper.toResponseDto(commandeSangRepository.save(commande));
    }

    @Override
    @Transactional(readOnly = true)
    public CommandeSangResponseDto getById(Long id) {
        return commandeSangMapper.toResponseDto(getEntityById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommandeSangResponseDto> getAll() {
        return commandeSangRepository.findAll().stream().map(commandeSangMapper::toResponseDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommandeSangResponseDto> getByHopital(Long hopitalId) {
        return commandeSangRepository.findByHopitalIdOrderByDateCommandeDesc(hopitalId)
                .stream()
                .map(commandeSangMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommandeSangResponseDto> getUrgencesActives() {
        return commandeSangRepository.trouverUrgencesActives(List.of(
                        StatutCommande.EN_ATTENTE,
                        StatutCommande.EN_PREPARATION,
                        StatutCommande.EN_LIVRAISON))
                .stream()
                .map(commandeSangMapper::toResponseDto)
                .toList();
    }

    @Override
    public CommandeSangResponseDto preparerCommande(Long id) {
        CommandeSang commande = getEntityById(id);
        if (commande.getStatut() != StatutCommande.EN_ATTENTE) {
            throw new RegleMetierException("Seules les commandes en attente peuvent etre preparees.");
        }

        List<PocheSang> disponibles = pocheSangRepository.trouverCompatiblesFefo(
                StatutSang.DISPONIBLE,
                CompatibiliteSanguineUtils.groupesCompatiblesPourReceveur(commande.getGroupeSanguin()),
                LocalDate.now());

        if (disponibles.size() < commande.getQuantite()) {
            throw new RegleMetierException("Stock insuffisant pour preparer la commande.");
        }

        List<PocheSang> selection = disponibles.subList(0, commande.getQuantite());
        for (PocheSang pocheSang : selection) {
            if (elementCommandeRepository.existsByPocheSangId(pocheSang.getId())) {
                throw new RegleMetierException("Une des poches selectionnees est deja affectee a une autre commande.");
            }
            pocheSang.setStatut(StatutSang.RESERVEE);
            ElementCommande elementCommande = new ElementCommande();
            elementCommande.setCommande(commande);
            elementCommande.setPocheSang(pocheSang);
            elementCommandeRepository.save(elementCommande);
        }
        pocheSangRepository.saveAll(selection);
        commande.setStatut(StatutCommande.EN_PREPARATION);
        return commandeSangMapper.toResponseDto(commandeSangRepository.save(commande));
    }

    @Override
    public CommandeSangResponseDto expedierCommande(Long id) {
        CommandeSang commande = getEntityById(id);
        if (commande.getStatut() != StatutCommande.EN_PREPARATION) {
            throw new RegleMetierException("La commande doit etre en preparation avant expedition.");
        }
        commande.setStatut(StatutCommande.EN_LIVRAISON);
        return commandeSangMapper.toResponseDto(commandeSangRepository.save(commande));
    }

    @Override
    public CommandeSangResponseDto livrerCommande(Long id) {
        CommandeSang commande = getEntityById(id);
        if (commande.getStatut() != StatutCommande.EN_LIVRAISON && commande.getStatut() != StatutCommande.EN_PREPARATION) {
            throw new RegleMetierException("La commande doit etre en preparation ou en livraison pour etre livree.");
        }
        List<ElementCommande> elements = elementCommandeRepository.findByCommandeId(id);
        elements.forEach(element -> {
            PocheSang pocheSang = element.getPocheSang();
            pocheSang.setStatut(StatutSang.TRANSFUSEE);
            pocheSangRepository.save(pocheSang);
        });
        commande.setStatut(StatutCommande.LIVREE);
        return commandeSangMapper.toResponseDto(commandeSangRepository.save(commande));
    }

    @Override
    public CommandeSangResponseDto annulerCommande(Long id) {
        CommandeSang commande = getEntityById(id);
        if (commande.getStatut() == StatutCommande.LIVREE) {
            throw new RegleMetierException("Une commande livree ne peut plus etre annulee.");
        }
        elementCommandeRepository.findByCommandeId(id).forEach(element -> {
            PocheSang pocheSang = element.getPocheSang();
            if (pocheSang.getStatut() == StatutSang.RESERVEE) {
                pocheSang.setStatut(StatutSang.DISPONIBLE);
                pocheSangRepository.save(pocheSang);
            }
        });
        commande.setStatut(StatutCommande.ANNULEE);
        return commandeSangMapper.toResponseDto(commandeSangRepository.save(commande));
    }

    private CommandeSang getEntityById(Long id) {
        return commandeSangRepository.findById(id)
                .orElseThrow(() -> new RessourceNonTrouveeException("Commande de sang introuvable : " + id));
    }

    private Hopital getHopitalById(Long id) {
        return hopitalRepository.findById(id)
                .orElseThrow(() -> new RessourceNonTrouveeException("Hopital introuvable : " + id));
    }
}
