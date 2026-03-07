package com.example.hemolinkbackend.controller;

import com.example.hemolinkbackend.dto.request.DonDto;
import com.example.hemolinkbackend.dto.response.DonResponseDto;
import com.example.hemolinkbackend.service.DonService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dons")
@RequiredArgsConstructor
public class DonController {

    private final DonService donService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DonResponseDto enregistrer(@RequestBody DonDto dto) {
        return donService.enregistrerDon(dto);
    }

    @GetMapping("/{id}")
    public DonResponseDto getById(@PathVariable Long id) {
        return donService.getById(id);
    }

    @GetMapping
    public List<DonResponseDto> getAll(@RequestParam(required = false) Long centreId,
                                       @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime debut,
                                       @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        if (centreId != null && debut != null && fin != null) {
            return donService.getParCentreEtPeriode(centreId, debut, fin);
        }
        return donService.getAll();
    }

    @GetMapping("/donneur/{donneurId}")
    public List<DonResponseDto> historiqueDonneur(@PathVariable Long donneurId) {
        return donService.getHistoriqueDonneur(donneurId);
    }

    @GetMapping("/donneur/{donneurId}/compteur-annuel")
    public Map<String, Object> compteurAnnuel(@PathVariable Long donneurId) {
        return Map.of("donneurId", donneurId, "count", donService.compterDonsAnneeEnCours(donneurId));
    }

    @GetMapping("/donneur/{donneurId}/eligibilite")
    public Map<String, Object> eligibilite(@PathVariable Long donneurId) {
        return Map.of("donneurId", donneurId, "eligible", donService.verifierEligibilite(donneurId));
    }

    @GetMapping("/donneur/{donneurId}/prochaine-date-eligible")
    public Map<String, Object> prochaineDateEligible(@PathVariable Long donneurId) {
        return Map.of("donneurId", donneurId, "date", donService.calculerProchaineDateEligible(donneurId));
    }
}

