package com.example.hemolinkbackend.service;

import com.example.hemolinkbackend.dto.request.PocheSangDto;
import com.example.hemolinkbackend.dto.response.PocheSangResponseDto;
import com.example.hemolinkbackend.enums.GroupeSanguin;
import com.example.hemolinkbackend.enums.StatutSang;

import java.util.List;

public interface PocheSangService {

    PocheSangResponseDto creer(PocheSangDto dto);

    PocheSangResponseDto creerDepuisDon(Long donId);

    PocheSangResponseDto getById(Long id);

    List<PocheSangResponseDto> getAll();

    List<PocheSangResponseDto> getDisponiblesParGroupe(GroupeSanguin groupeSanguin);

    List<PocheSangResponseDto> getPochesSansTest();

    List<PocheSangResponseDto> getPochesExpirees();

    List<PocheSangResponseDto> getCompatiblesFefo(GroupeSanguin groupeReceveur, Integer quantite);

    List<PocheSangResponseDto> reserverCompatiblesFefo(GroupeSanguin groupeReceveur, Integer quantite);

    PocheSangResponseDto changerStatut(Long id, StatutSang statut);

    PocheSangResponseDto marquerCommeTransfusee(Long id);

    PocheSangResponseDto marquerCommeEcartee(Long id);
}
