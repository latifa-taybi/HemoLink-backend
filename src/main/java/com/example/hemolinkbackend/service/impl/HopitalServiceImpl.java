package com.example.hemolinkbackend.service.impl;

import com.example.hemolinkbackend.dto.request.HopitalDto;
import com.example.hemolinkbackend.dto.response.HopitalResponseDto;
import com.example.hemolinkbackend.entity.Hopital;
import com.example.hemolinkbackend.mapper.HopitalMapper;
import com.example.hemolinkbackend.repository.HopitalRepository;
import com.example.hemolinkbackend.service.HopitalService;
import com.example.hemolinkbackend.service.exception.RessourceNonTrouveeException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class HopitalServiceImpl implements HopitalService {

    private final HopitalRepository hopitalRepository;
    private final HopitalMapper hopitalMapper;

    @Override
    public HopitalResponseDto creer(HopitalDto dto) {
        Hopital entity = hopitalMapper.toEntity(dto);
        return hopitalMapper.toResponseDto(hopitalRepository.save(entity));
    }

    @Override
    public HopitalResponseDto mettreAJour(Long id, HopitalDto dto) {
        Hopital entity = getEntityById(id);
        hopitalMapper.updateEntity(dto, entity);
        return hopitalMapper.toResponseDto(hopitalRepository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public HopitalResponseDto getById(Long id) {
        return hopitalMapper.toResponseDto(getEntityById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<HopitalResponseDto> getAll() {
        return hopitalRepository.findAll().stream().map(hopitalMapper::toResponseDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<HopitalResponseDto> rechercherParVille(String ville) {
        return hopitalRepository.findByVilleIgnoreCase(ville).stream().map(hopitalMapper::toResponseDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<HopitalResponseDto> rechercherParNom(String nom) {
        return hopitalRepository.findByNomContainingIgnoreCase(nom).stream().map(hopitalMapper::toResponseDto).toList();
    }

    @Override
    public void supprimer(Long id) {
        hopitalRepository.delete(getEntityById(id));
    }

    private Hopital getEntityById(Long id) {
        return hopitalRepository.findById(id)
                .orElseThrow(() -> new RessourceNonTrouveeException("Hopital introuvable : " + id));
    }
}
