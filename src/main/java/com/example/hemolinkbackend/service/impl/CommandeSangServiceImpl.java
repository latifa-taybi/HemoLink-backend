package com.example.hemolinkbackend.service.impl;

import com.example.hemolinkbackend.dto.request.CommandeSangDto;
import com.example.hemolinkbackend.dto.response.CommandeSangResponseDto;
import com.example.hemolinkbackend.entity.CommandeSang;
import com.example.hemolinkbackend.mapper.CommandeSangMapper;
import com.example.hemolinkbackend.repository.CommandeSangRepository;
import com.example.hemolinkbackend.service.CommandeSangService;
import com.example.hemolinkbackend.service.exception.RessourceNonTrouveeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CommandeSangServiceImpl implements CommandeSangService {

    private final CommandeSangRepository commandeSangRepository;
    private final CommandeSangMapper commandeSangMapper;

    @Override
    public CommandeSangResponseDto creer(CommandeSangDto dto) {
        log.info("Création d'une commande de sang");
        CommandeSang commande = commandeSangMapper.toEntity(dto);
        CommandeSang saved = commandeSangRepository.save(commande);
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
    public List<CommandeSangResponseDto> getUrgencesActives() {
        log.debug("Récupération des commandes urgentes");
        return commandeSangRepository.findAll().stream()
                .map(commandeSangMapper::toResponseDto)
                .toList();
    }

    @Override
    public CommandeSangResponseDto preparerCommande(Long id) {
        log.info("Préparation de la commande ID: {}", id);
        CommandeSang commande = getEntityById(id);
        return commandeSangMapper.toResponseDto(commande);
    }

    @Override
    public CommandeSangResponseDto expedierCommande(Long id) {
        log.info("Expédition de la commande ID: {}", id);
        CommandeSang commande = getEntityById(id);
        return commandeSangMapper.toResponseDto(commande);
    }

    @Override
    public CommandeSangResponseDto livrerCommande(Long id) {
        log.info("Livraison de la commande ID: {}", id);
        CommandeSang commande = getEntityById(id);
        return commandeSangMapper.toResponseDto(commande);
    }

    @Override
    public CommandeSangResponseDto annulerCommande(Long id) {
        log.warn("Annulation de la commande ID: {}", id);
        CommandeSang commande = getEntityById(id);
        return commandeSangMapper.toResponseDto(commande);
    }

    private CommandeSang getEntityById(Long id) {
        return commandeSangRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Commande non trouvée avec ID: {}", id);
                    return new RessourceNonTrouveeException("Commande introuvable : " + id);
                });
    }
}

