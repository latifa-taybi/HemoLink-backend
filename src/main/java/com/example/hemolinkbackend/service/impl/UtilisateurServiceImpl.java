package com.example.hemolinkbackend.service.impl;

import com.example.hemolinkbackend.dto.request.InscriptionDto;
import com.example.hemolinkbackend.dto.request.UtilisateurDto;
import com.example.hemolinkbackend.dto.response.UtilisateurResponseDto;
import com.example.hemolinkbackend.entity.Utilisateur;
import com.example.hemolinkbackend.entity.Donneur;
import com.example.hemolinkbackend.enums.RoleUtilisateur;
import com.example.hemolinkbackend.mapper.UtilisateurMapper;
import com.example.hemolinkbackend.repository.DonneurRepository;
import com.example.hemolinkbackend.repository.NotificationRepository;
import com.example.hemolinkbackend.repository.RefreshTokenRepository;
import com.example.hemolinkbackend.repository.UtilisateurRepository;
import com.example.hemolinkbackend.service.UtilisateurService;
import com.example.hemolinkbackend.service.exception.RegleMetierException;
import com.example.hemolinkbackend.service.exception.RessourceNonTrouveeException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class UtilisateurServiceImpl implements UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;
    private final DonneurRepository donneurRepository;
    private final NotificationRepository notificationRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UtilisateurMapper utilisateurMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UtilisateurResponseDto sinscrire(InscriptionDto dto) {
        verifierEmailUnique(dto.email(), null);
        if (dto.motDePasse() == null || dto.motDePasse().isBlank()) {
            throw new RegleMetierException("Le mot de passe est obligatoire.");
        }
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setPrenom(dto.prenom());
        utilisateur.setNom(dto.nom());
        utilisateur.setEmail(dto.email());
        utilisateur.setTelephone(dto.telephone());
        utilisateur.setMotDePasse(passwordEncoder.encode(dto.motDePasse()));
        utilisateur.setRole(RoleUtilisateur.DONNEUR);
        utilisateur.setActif(true);
        utilisateur.setCreeLe(LocalDateTime.now());
        Utilisateur savedUser = utilisateurRepository.save(utilisateur);
        
        // Automatiquement créer un profil donneur pour le rôle DONNEUR
        if (savedUser.getRole() == RoleUtilisateur.DONNEUR) {
            Donneur donneur = new Donneur();
            donneur.setUtilisateur(savedUser);
            donneurRepository.save(donneur);
        }
        
        return utilisateurMapper.toResponseDto(savedUser);
    }

    @Override
    public UtilisateurResponseDto creer(UtilisateurDto dto) {
        verifierEmailUnique(dto.email(), null);
        if (dto.motDePasse() == null || dto.motDePasse().isBlank()) {
            throw new RegleMetierException("Le mot de passe est obligatoire.");
        }
        Utilisateur utilisateur = utilisateurMapper.toEntity(dto);
        utilisateur.setMotDePasse(passwordEncoder.encode(dto.motDePasse()));
        utilisateur.setActif(dto.actif() == null || dto.actif());
        utilisateur.setCreeLe(LocalDateTime.now());
        Utilisateur savedUser = utilisateurRepository.save(utilisateur);
        
        // Automatiquement créer un profil donneur pour le rôle DONNEUR
        if (savedUser.getRole() == RoleUtilisateur.DONNEUR) {
            Donneur donneur = new Donneur();
            donneur.setUtilisateur(savedUser);
            donneurRepository.save(donneur);
        }
        
        return utilisateurMapper.toResponseDto(savedUser);
    }

    @Override
    public UtilisateurResponseDto mettreAJour(Long id, UtilisateurDto dto) {
        Utilisateur utilisateur = getEntityById(id);
        boolean actifActuel = utilisateur.isActif();
        LocalDateTime creeLeActuel = utilisateur.getCreeLe();
        String motDePasseActuel = utilisateur.getMotDePasse();

        verifierEmailUnique(dto.email(), id);
        utilisateurMapper.updateEntity(dto, utilisateur);

        if (dto.actif() == null) {
            utilisateur.setActif(actifActuel);
        }
        utilisateur.setCreeLe(creeLeActuel);

        if (dto.motDePasse() == null || dto.motDePasse().isBlank()) {
            utilisateur.setMotDePasse(motDePasseActuel);
        } else {
            utilisateur.setMotDePasse(passwordEncoder.encode(dto.motDePasse()));
        }

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
        Utilisateur utilisateur = getEntityById(id);
        try {
            // Supprimer les dépendances inoffensives
            refreshTokenRepository.deleteByUtilisateurId(id);
            notificationRepository.deleteByUtilisateurId(id);
            
            // Essayer de supprimer le profil Donneur s'il existe et s'il n'a pas de dons
            donneurRepository.findByUtilisateurId(id).ifPresent(donneur -> {
                donneurRepository.delete(donneur);
            });
            
            utilisateurRepository.delete(utilisateur);
            utilisateurRepository.flush(); // Pour forcer l'exception si contrainte FK non respectée
        } catch (DataIntegrityViolationException e) {
            throw new RegleMetierException("Cet utilisateur possède des données indélébiles (Dons, Rendez-vous). Veuillez plutôt désactiver le compte.");
        }
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
