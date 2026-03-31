package com.example.hemolinkbackend.service;

import com.example.hemolinkbackend.dto.request.RendezVousDto;
import com.example.hemolinkbackend.dto.response.RendezVousResponseDto;

import java.time.LocalDate;
import java.util.List;

public interface RendezVousService {

    RendezVousResponseDto planifier(RendezVousDto dto);

    RendezVousResponseDto annuler(Long id);

    RendezVousResponseDto terminer(Long id);

    RendezVousResponseDto getById(Long id);

    List<RendezVousResponseDto> getAll();

    List<RendezVousResponseDto> getByDonneur(Long donneurId);

    List<RendezVousResponseDto> getByCentre(Long centreId);
    
    List<RendezVousResponseDto> getByCentreEtJour(Long centreId, LocalDate jour);
}
