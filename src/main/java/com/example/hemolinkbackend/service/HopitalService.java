package com.example.hemolinkbackend.service;

import com.example.hemolinkbackend.dto.request.HopitalDto;
import com.example.hemolinkbackend.dto.response.HopitalResponseDto;

import java.util.List;

public interface HopitalService {

    HopitalResponseDto creer(HopitalDto dto);

    HopitalResponseDto mettreAJour(Long id, HopitalDto dto);

    HopitalResponseDto getById(Long id);

    List<HopitalResponseDto> getAll();

    List<HopitalResponseDto> rechercherParVille(String ville);

    List<HopitalResponseDto> rechercherParNom(String nom);

    void supprimer(Long id);
}
