package com.example.hemolinkbackend.service.impl;

import com.example.hemolinkbackend.dto.request.RendezVousDto;
import com.example.hemolinkbackend.dto.response.RendezVousResponseDto;
import com.example.hemolinkbackend.entity.CentreCollecte;
import com.example.hemolinkbackend.entity.Donneur;
import com.example.hemolinkbackend.entity.RendezVous;
import com.example.hemolinkbackend.enums.StatutRendezVous;
import com.example.hemolinkbackend.mapper.RendezVousMapper;
import com.example.hemolinkbackend.repository.CentreCollecteRepository;
import com.example.hemolinkbackend.repository.DonneurRepository;
import com.example.hemolinkbackend.repository.RendezVousRepository;
import com.example.hemolinkbackend.service.NotificationService;
import com.example.hemolinkbackend.service.RendezVousService;
import com.example.hemolinkbackend.service.exception.RegleMetierException;
import com.example.hemolinkbackend.service.exception.RessourceNonTrouveeException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class RendezVousServiceImpl implements RendezVousService {

    private final RendezVousRepository rendezVousRepository;
    private final DonneurRepository donneurRepository;
    private final CentreCollecteRepository centreCollecteRepository;
    private final RendezVousMapper rendezVousMapper;
    private final NotificationService notificationService;

    @Override
    public RendezVousResponseDto planifier(RendezVousDto dto) {
        if (dto.dateRendezVous() == null || !dto.dateRendezVous().isAfter(LocalDateTime.now())) {
            throw new RegleMetierException("La date du rendez-vous doit etre dans le futur.");
        }
        Donneur donneur = getDonneurById(dto.donneurId());
        CentreCollecte centre = getCentreById(dto.centreId());
        if (!estEligibleAuDon(donneur)) {
            throw new RegleMetierException("Le donneur n'est pas encore eligible a un nouveau don.");
        }
        if (rendezVousRepository.existsByDonneurIdAndDateRendezVousBetween(donneur.getId(), dto.dateRendezVous(), dto.dateRendezVous())) {
            throw new RegleMetierException("Un rendez-vous existe deja a cette date pour ce donneur.");
        }

        RendezVous rendezVous = rendezVousMapper.toEntity(dto);
        rendezVous.setDonneur(donneur);
        rendezVous.setCentre(centre);
        rendezVous.setStatut(dto.statut() != null ? dto.statut() : StatutRendezVous.PLANIFIE);

        RendezVous saved = rendezVousRepository.save(rendezVous);
        if (donneur.getUtilisateur() != null) {
            notificationService.notifierUtilisateur(donneur.getUtilisateur().getId(), "Votre rendez-vous de don a ete confirme pour le " + saved.getDateRendezVous() + ".");
        }
        return rendezVousMapper.toResponseDto(saved);
    }

    @Override
    public RendezVousResponseDto annuler(Long id) {
        RendezVous rendezVous = getEntityById(id);
        rendezVous.setStatut(StatutRendezVous.ANNULE);
        RendezVous saved = rendezVousRepository.save(rendezVous);
        if (saved.getDonneur() != null && saved.getDonneur().getUtilisateur() != null) {
            notificationService.notifierUtilisateur(saved.getDonneur().getUtilisateur().getId(), "Votre rendez-vous du " + saved.getDateRendezVous() + " a ete annule.");
        }
        return rendezVousMapper.toResponseDto(saved);
    }

    @Override
    public RendezVousResponseDto terminer(Long id) {
        RendezVous rendezVous = getEntityById(id);
        rendezVous.setStatut(StatutRendezVous.TERMINE);
        return rendezVousMapper.toResponseDto(rendezVousRepository.save(rendezVous));
    }

    @Override
    @Transactional(readOnly = true)
    public RendezVousResponseDto getById(Long id) {
        return rendezVousMapper.toResponseDto(getEntityById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<RendezVousResponseDto> getAll() {
        return rendezVousRepository.findAll().stream().map(rendezVousMapper::toResponseDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RendezVousResponseDto> getByDonneur(Long donneurId) {
        return rendezVousRepository.findByDonneurIdOrderByDateRendezVousDesc(donneurId)
                .stream()
                .map(rendezVousMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RendezVousResponseDto> getByCentreEtJour(Long centreId, LocalDate jour) {
        LocalDateTime debut = LocalDateTime.of(jour, LocalTime.MIN);
        LocalDateTime fin = LocalDateTime.of(jour, LocalTime.MAX);
        return rendezVousRepository.findByCentreIdAndDateRendezVousBetween(centreId, debut, fin)
                .stream()
                .map(rendezVousMapper::toResponseDto)
                .toList();
    }

    private boolean estEligibleAuDon(Donneur donneur) {
        Integer quota = donneur.getNombreDonsAnnuel() == null ? 0 : donneur.getNombreDonsAnnuel();
        return quota < 4 && (donneur.getDateDernierDon() == null || !donneur.getDateDernierDon().plusWeeks(8).isAfter(LocalDate.now()));
    }

    private RendezVous getEntityById(Long id) {
        return rendezVousRepository.findById(id)
                .orElseThrow(() -> new RessourceNonTrouveeException("Rendez-vous introuvable : " + id));
    }

    private Donneur getDonneurById(Long id) {
        return donneurRepository.findById(id)
                .orElseThrow(() -> new RessourceNonTrouveeException("Donneur introuvable : " + id));
    }

    private CentreCollecte getCentreById(Long id) {
        return centreCollecteRepository.findById(id)
                .orElseThrow(() -> new RessourceNonTrouveeException("Centre de collecte introuvable : " + id));
    }
}
