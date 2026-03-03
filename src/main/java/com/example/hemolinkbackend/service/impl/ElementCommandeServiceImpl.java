package com.example.hemolinkbackend.service.impl;

import com.example.hemolinkbackend.dto.response.ElementCommandeResponseDto;
import com.example.hemolinkbackend.entity.CommandeSang;
import com.example.hemolinkbackend.entity.ElementCommande;
import com.example.hemolinkbackend.entity.PocheSang;
import com.example.hemolinkbackend.enums.StatutCommande;
import com.example.hemolinkbackend.enums.StatutSang;
import com.example.hemolinkbackend.mapper.ElementCommandeMapper;
import com.example.hemolinkbackend.repository.CommandeSangRepository;
import com.example.hemolinkbackend.repository.ElementCommandeRepository;
import com.example.hemolinkbackend.repository.PocheSangRepository;
import com.example.hemolinkbackend.service.ElementCommandeService;
import com.example.hemolinkbackend.service.exception.RegleMetierException;
import com.example.hemolinkbackend.service.exception.RessourceNonTrouveeException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ElementCommandeServiceImpl implements ElementCommandeService {

    private final ElementCommandeRepository elementCommandeRepository;
    private final CommandeSangRepository commandeSangRepository;
    private final PocheSangRepository pocheSangRepository;
    private final ElementCommandeMapper elementCommandeMapper;

    @Override
    @Transactional(readOnly = true)
    public ElementCommandeResponseDto getById(Long id) {
        return elementCommandeMapper.toResponseDto(getEntityById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ElementCommandeResponseDto> getAll() {
        return elementCommandeRepository.findAll().stream().map(elementCommandeMapper::toResponseDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ElementCommandeResponseDto> getByCommandeId(Long commandeId) {
        return elementCommandeRepository.findByCommandeId(commandeId).stream().map(elementCommandeMapper::toResponseDto).toList();
    }

    @Override
    public ElementCommandeResponseDto affecterPoche(Long commandeId, Long pocheSangId) {
        CommandeSang commande = commandeSangRepository.findById(commandeId)
                .orElseThrow(() -> new RessourceNonTrouveeException("Commande de sang introuvable : " + commandeId));
        PocheSang pocheSang = pocheSangRepository.findById(pocheSangId)
                .orElseThrow(() -> new RessourceNonTrouveeException("Poche de sang introuvable : " + pocheSangId));
        if (commande.getStatut() == StatutCommande.ANNULEE || commande.getStatut() == StatutCommande.LIVREE) {
            throw new RegleMetierException("Impossible d'affecter une poche a cette commande.");
        }
        if (elementCommandeRepository.existsByPocheSangId(pocheSangId)) {
            throw new RegleMetierException("Cette poche est deja affectee a une commande.");
        }
        if (pocheSang.getStatut() != StatutSang.DISPONIBLE) {
            throw new RegleMetierException("Seules les poches disponibles peuvent etre affectees.");
        }

        pocheSang.setStatut(StatutSang.RESERVEE);
        pocheSangRepository.save(pocheSang);
        commande.setStatut(StatutCommande.EN_PREPARATION);
        commandeSangRepository.save(commande);

        ElementCommande elementCommande = new ElementCommande();
        elementCommande.setCommande(commande);
        elementCommande.setPocheSang(pocheSang);
        return elementCommandeMapper.toResponseDto(elementCommandeRepository.save(elementCommande));
    }

    @Override
    public void supprimer(Long id) {
        ElementCommande elementCommande = getEntityById(id);
        PocheSang pocheSang = elementCommande.getPocheSang();
        if (pocheSang != null && pocheSang.getStatut() == StatutSang.RESERVEE) {
            pocheSang.setStatut(StatutSang.DISPONIBLE);
            pocheSangRepository.save(pocheSang);
        }
        elementCommandeRepository.delete(elementCommande);
    }

    private ElementCommande getEntityById(Long id) {
        return elementCommandeRepository.findById(id)
                .orElseThrow(() -> new RessourceNonTrouveeException("Element de commande introuvable : " + id));
    }
}
