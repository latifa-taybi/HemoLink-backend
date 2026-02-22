package com.example.hemolinkbackend.controller;

import com.example.hemolinkbackend.dto.request.RendezVousDto;
import com.example.hemolinkbackend.dto.response.RendezVousResponseDto;
import com.example.hemolinkbackend.service.RendezVousService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/rendez-vous")
@RequiredArgsConstructor
public class RendezVousController {

    private final RendezVousService rendezVousService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RendezVousResponseDto planifier(@RequestBody RendezVousDto dto) {
        return rendezVousService.planifier(dto);
    }

    @GetMapping("/{id}")
    public RendezVousResponseDto getById(@PathVariable Long id) {
        return rendezVousService.getById(id);
    }

    @GetMapping
    public List<RendezVousResponseDto> getAll(@RequestParam(required = false) Long centreId,
                                              @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate jour) {
        if (centreId != null && jour != null) {
            return rendezVousService.getByCentreEtJour(centreId, jour);
        }
        return rendezVousService.getAll();
    }

    @GetMapping("/donneur/{donneurId}")
    public List<RendezVousResponseDto> getByDonneur(@PathVariable Long donneurId) {
        return rendezVousService.getByDonneur(donneurId);
    }

    @PatchMapping("/{id}/annuler")
    public RendezVousResponseDto annuler(@PathVariable Long id) {
        return rendezVousService.annuler(id);
    }

    @PatchMapping("/{id}/terminer")
    public RendezVousResponseDto terminer(@PathVariable Long id) {
        return rendezVousService.terminer(id);
    }
}

