package com.example.hemolinkbackend.service.impl;

import com.example.hemolinkbackend.dto.request.UtilisateurDto;
import com.example.hemolinkbackend.dto.response.UtilisateurResponseDto;
import com.example.hemolinkbackend.entity.Utilisateur;
import com.example.hemolinkbackend.enums.RoleUtilisateur;
import com.example.hemolinkbackend.mapper.UtilisateurMapper;
import com.example.hemolinkbackend.repository.UtilisateurRepository;
import com.example.hemolinkbackend.service.UtilisateurService;
import com.example.hemolinkbackend.service.exception.RegleMetierException;
import com.example.hemolinkbackend.service.exception.RessourceNonTrouveeException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class UtilisateurServiceImpl implements UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;
    private final UtilisateurMapper utilisateurMapper;

    @Override
    public UtilisateurResponseDto creer(UtilisateurDto dto) {
        verifierEmailUnique(dto.email(), null);
        Utilisateur utilisateur = utilisateurMapper.toEntity(dto);
        utilisateur.setActif(dto.actif() == null || dto.actif());
        utilisateur.setCreeLe(LocalDateTime.now());
        return utilisateurMapper.toResponseDto(utilisateurRepository.save(utilisateur));
    }

    @Override
    public UtilisateurResponseDto mettreAJour(Long id, UtilisateurDto dto) {
        Utilisateur utilisateur = getEntityById(id);
        boolean actifActuel = utilisateur.isActif();
        LocalDateTime creeLeActuel = utilisateur.getCreeLe();
        verifierEmailUnique(dto.email(), id);
        utilisateurMapper.updateEntity(dto, utilisateur);
        if (dto.actif() == null) {
            utilisateur.setActif(actifActuel);
        }
        utilisateur.setCreeLe(creeLeActuel);
        return utilisateurMapper.toResponseDto(utilisateurRepository.save(utilisateur));
    }

    @Override
    @Transactional(readOnly = true)
    public UtilisateurResponseDto getById(Long id) {
        return utilisateurMapper.toResponseDto(getEntityById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UtilisateurResponseDto> getAll() {
        return utilisateurRepository.findAll().stream().map(utilisateurMapper::toResponseDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public UtilisateurResponseDto getByEmail(String email) {
        return utilisateurMapper.toResponseDto(utilisateurRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new RessourceNonTrouveeException("Utilisateur introuvable pour l'email : " + email)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UtilisateurResponseDto> getActifs() {
        return utilisateurRepository.findByActifTrue().stream().map(utilisateurMapper::toResponseDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UtilisateurResponseDto> getByRole(RoleUtilisateur role) {
        return utilisateurRepository.findByRole(role).stream().map(utilisateurMapper::toResponseDto).toList();
    }

    @Override
    public void supprimer(Long id) {
        utilisateurRepository.delete(getEntityById(id));
    }

    private Utilisateur getEntityById(Long id) {
        return utilisateurRepository.findById(id)
                .orElseThrow(() -> new RessourceNonTrouveeException("Utilisateur introuvable : " + id));
    }

    private void verifierEmailUnique(String email, Long utilisateurId) {
        if (email == null || email.isBlank()) {
            return;
        }
        utilisateurRepository.findByEmailIgnoreCase(email)
                .filter(utilisateur -> !utilisateur.getId().equals(utilisateurId))
                .ifPresent(utilisateur -> {
                    throw new RegleMetierException("Un utilisateur existe deja avec cet email.");
                });
    }
}
