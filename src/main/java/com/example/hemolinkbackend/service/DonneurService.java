package com.example.hemolinkbackend.service;

import com.example.hemolinkbackend.dto.request.DonneurDto;
import com.example.hemolinkbackend.dto.response.DonneurResponseDto;
import com.example.hemolinkbackend.enums.GroupeSanguin;

import java.time.LocalDate;
import java.util.List;

public interface DonneurService {

    DonneurResponseDto creer(DonneurDto dto);

    DonneurResponseDto mettreAJour(Long id, DonneurDto dto);

    DonneurResponseDto getById(Long id);

    List<DonneurResponseDto> getAll();

    DonneurResponseDto getByUtilisateurId(Long utilisateurId);

    List<DonneurResponseDto> getByGroupeSanguin(GroupeSanguin groupeSanguin);

    List<DonneurResponseDto> rechercherParNomOuPrenom(String motCle);

    List<DonneurResponseDto> getDonneursEligiblesParGroupe(GroupeSanguin groupeSanguin);

    boolean verifierEligibilite(Long donneurId);

    LocalDate calculerProchaineDateEligible(Long donneurId);

    void supprimer(Long id);
}
