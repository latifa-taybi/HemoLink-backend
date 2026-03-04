package com.example.hemolinkbackend.service;

import com.example.hemolinkbackend.dto.request.CentreCollecteDto;
import com.example.hemolinkbackend.dto.response.CentreCollecteResponseDto;

import java.util.List;

public interface CentreCollecteService {

    CentreCollecteResponseDto creer(CentreCollecteDto dto);

    CentreCollecteResponseDto mettreAJour(Long id, CentreCollecteDto dto);

    CentreCollecteResponseDto getById(Long id);

    List<CentreCollecteResponseDto> getAll();

    List<CentreCollecteResponseDto> rechercherParVille(String ville);

    List<CentreCollecteResponseDto> rechercherParNom(String nom);

    List<CentreCollecteResponseDto> rechercherPlusProches(Double latitude, Double longitude);

    void supprimer(Long id);
}
