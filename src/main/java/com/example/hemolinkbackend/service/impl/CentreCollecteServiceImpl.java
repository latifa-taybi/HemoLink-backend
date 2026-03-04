package com.example.hemolinkbackend.service.impl;

import com.example.hemolinkbackend.dto.request.CentreCollecteDto;
import com.example.hemolinkbackend.dto.response.CentreCollecteResponseDto;
import com.example.hemolinkbackend.entity.CentreCollecte;
import com.example.hemolinkbackend.mapper.CentreCollecteMapper;
import com.example.hemolinkbackend.repository.CentreCollecteRepository;
import com.example.hemolinkbackend.service.CentreCollecteService;
import com.example.hemolinkbackend.service.exception.RessourceNonTrouveeException;
import com.example.hemolinkbackend.service.support.GeoUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CentreCollecteServiceImpl implements CentreCollecteService {

    private final CentreCollecteRepository centreCollecteRepository;
    private final CentreCollecteMapper centreCollecteMapper;

    @Override
    public CentreCollecteResponseDto creer(CentreCollecteDto dto) {
        CentreCollecte entity = centreCollecteMapper.toEntity(dto);
        return centreCollecteMapper.toResponseDto(centreCollecteRepository.save(entity));
    }

    @Override
    public CentreCollecteResponseDto mettreAJour(Long id, CentreCollecteDto dto) {
        CentreCollecte entity = getEntityById(id);
        centreCollecteMapper.updateEntity(dto, entity);
        return centreCollecteMapper.toResponseDto(centreCollecteRepository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public CentreCollecteResponseDto getById(Long id) {
        return centreCollecteMapper.toResponseDto(getEntityById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CentreCollecteResponseDto> getAll() {
        return centreCollecteRepository.findAll().stream().map(centreCollecteMapper::toResponseDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CentreCollecteResponseDto> rechercherParVille(String ville) {
        return centreCollecteRepository.findByVilleIgnoreCase(ville).stream().map(centreCollecteMapper::toResponseDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CentreCollecteResponseDto> rechercherParNom(String nom) {
        return centreCollecteRepository.findByNomContainingIgnoreCase(nom).stream().map(centreCollecteMapper::toResponseDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CentreCollecteResponseDto> rechercherPlusProches(Double latitude, Double longitude) {
        return centreCollecteRepository.findAll().stream()
                .filter(centre -> centre.getLatitude() != null && centre.getLongitude() != null)
                .sorted(Comparator.comparingDouble(centre -> GeoUtils.distanceKm(latitude, longitude, centre.getLatitude(), centre.getLongitude())))
                .map(centreCollecteMapper::toResponseDto)
                .toList();
    }

    @Override
    public void supprimer(Long id) {
        centreCollecteRepository.delete(getEntityById(id));
    }

    private CentreCollecte getEntityById(Long id) {
        return centreCollecteRepository.findById(id)
                .orElseThrow(() -> new RessourceNonTrouveeException("Centre de collecte introuvable : " + id));
    }
}
