package com.example.hemolinkbackend.service;

import com.example.hemolinkbackend.dto.request.DonDto;
import com.example.hemolinkbackend.dto.response.DonResponseDto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface DonService {

    DonResponseDto enregistrerDon(DonDto dto);

    DonResponseDto getById(Long id);

    List<DonResponseDto> getAll();

    List<DonResponseDto> getHistoriqueDonneur(Long donneurId);

    List<DonResponseDto> getDonsByCentre(Long centreId);

    List<DonResponseDto> getParCentreEtPeriode(Long centreId, LocalDateTime debut, LocalDateTime fin);

    long compterDonsAnneeEnCours(Long donneurId);

    boolean verifierEligibilite(Long donneurId);

    LocalDate calculerProchaineDateEligible(Long donneurId);
    
    LocalDate getDerniereDateDon(Long donneurId);
}
