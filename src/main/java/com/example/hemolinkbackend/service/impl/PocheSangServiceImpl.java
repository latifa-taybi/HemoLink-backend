package com.example.hemolinkbackend.service.impl;

import com.example.hemolinkbackend.dto.request.PocheSangDto;
import com.example.hemolinkbackend.dto.response.PocheSangResponseDto;
import com.example.hemolinkbackend.entity.Don;
import com.example.hemolinkbackend.entity.Donneur;
import com.example.hemolinkbackend.entity.PocheSang;
import com.example.hemolinkbackend.enums.GroupeSanguin;
import com.example.hemolinkbackend.enums.StatutSang;
import com.example.hemolinkbackend.mapper.PocheSangMapper;
import com.example.hemolinkbackend.repository.DonRepository;
import com.example.hemolinkbackend.repository.PocheSangRepository;
import com.example.hemolinkbackend.service.PocheSangService;
import com.example.hemolinkbackend.service.exception.RegleMetierException;
import com.example.hemolinkbackend.service.exception.RessourceNonTrouveeException;
import com.example.hemolinkbackend.service.support.CompatibiliteSanguineUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class PocheSangServiceImpl implements PocheSangService {

    private static final int DUREE_CONSERVATION_JOURS = 42;

    private final PocheSangRepository pocheSangRepository;
    private final DonRepository donRepository;
    private final PocheSangMapper pocheSangMapper;

    @Override
    public PocheSangResponseDto creer(PocheSangDto dto) {
        Don don = getDonById(dto.donId());
        PocheSang pocheSang = pocheSangMapper.toEntity(dto);
        pocheSang.setDon(don);
        pocheSang.setStatut(dto.statut() != null ? dto.statut() : StatutSang.EN_ATTENTE_TEST);
        return pocheSangMapper.toResponseDto(pocheSangRepository.save(pocheSang));
    }

    @Override
    public PocheSangResponseDto creerDepuisDon(Long donId) {
        Don don = getDonById(donId);
        Donneur donneur = don.getDonneur();
        if (donneur == null || donneur.getGroupeSanguin() == null) {
            throw new RegleMetierException("Impossible de creer une poche sans groupe sanguin donneur.");
        }
        PocheSang pocheSang = new PocheSang();
        pocheSang.setDon(don);
        pocheSang.setGroupeSanguin(donneur.getGroupeSanguin());
        pocheSang.setDateCollecte(don.getDateDon().toLocalDate());
        pocheSang.setDateExpiration(don.getDateDon().toLocalDate().plusDays(DUREE_CONSERVATION_JOURS));
        pocheSang.setStatut(StatutSang.EN_ATTENTE_TEST);
        return pocheSangMapper.toResponseDto(pocheSangRepository.save(pocheSang));
    }

    @Override
    @Transactional(readOnly = true)
    public PocheSangResponseDto getById(Long id) {
        return pocheSangMapper.toResponseDto(getEntityById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PocheSangResponseDto> getAll() {
        return pocheSangRepository.findAll().stream().map(pocheSangMapper::toResponseDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PocheSangResponseDto> getDisponiblesParGroupe(GroupeSanguin groupeSanguin) {
        return pocheSangRepository.findByStatutAndGroupeSanguinOrderByDateExpirationAsc(StatutSang.DISPONIBLE, groupeSanguin)
                .stream()
                .map(pocheSangMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PocheSangResponseDto> getPochesSansTest() {
        return pocheSangRepository.trouverSansTestParStatut(StatutSang.EN_ATTENTE_TEST).stream()
                .map(pocheSangMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PocheSangResponseDto> getPochesExpirees() {
        LocalDate aujourdHui = LocalDate.now();
        return pocheSangRepository.findAll().stream()
                .filter(poche -> poche.getDateExpiration() != null && poche.getDateExpiration().isBefore(aujourdHui))
                .map(pocheSangMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PocheSangResponseDto> getCompatiblesFefo(GroupeSanguin groupeReceveur, Integer quantite) {
        return trouverCompatiblesDisponibles(groupeReceveur, quantite).stream().map(pocheSangMapper::toResponseDto).toList();
    }

    @Override
    public List<PocheSangResponseDto> reserverCompatiblesFefo(GroupeSanguin groupeReceveur, Integer quantite) {
        List<PocheSang> poches = trouverCompatiblesDisponibles(groupeReceveur, quantite);
        if (quantite != null && poches.size() < quantite) {
            throw new RegleMetierException("Stock insuffisant pour couvrir la demande.");
        }
        poches.forEach(poche -> poche.setStatut(StatutSang.RESERVEE));
        return pocheSangRepository.saveAll(poches).stream().map(pocheSangMapper::toResponseDto).toList();
    }

    @Override
    public PocheSangResponseDto changerStatut(Long id, StatutSang statut) {
        PocheSang pocheSang = getEntityById(id);
        pocheSang.setStatut(statut);
        return pocheSangMapper.toResponseDto(pocheSangRepository.save(pocheSang));
    }

    @Override
    public PocheSangResponseDto marquerCommeTransfusee(Long id) {
        return changerStatut(id, StatutSang.TRANSFUSEE);
    }

    @Override
    public PocheSangResponseDto marquerCommeEcartee(Long id) {
        return changerStatut(id, StatutSang.ECARTEE);
    }

    private List<PocheSang> trouverCompatiblesDisponibles(GroupeSanguin groupeReceveur, Integer quantite) {
        List<PocheSang> poches = pocheSangRepository.trouverCompatiblesFefo(
                StatutSang.DISPONIBLE,
                CompatibiliteSanguineUtils.groupesCompatiblesPourReceveur(groupeReceveur),
                LocalDate.now()
        );
        if (quantite == null || quantite <= 0 || poches.size() <= quantite) {
            return poches;
        }
        return poches.subList(0, quantite);
    }

    private PocheSang getEntityById(Long id) {
        return pocheSangRepository.findById(id)
                .orElseThrow(() -> new RessourceNonTrouveeException("Poche de sang introuvable : " + id));
    }

    private Don getDonById(Long id) {
        return donRepository.findById(id)
                .orElseThrow(() -> new RessourceNonTrouveeException("Don introuvable : " + id));
    }
}
