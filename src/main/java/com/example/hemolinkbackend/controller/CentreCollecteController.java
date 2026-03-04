package com.example.hemolinkbackend.controller;

import com.example.hemolinkbackend.dto.request.CentreCollecteDto;
import com.example.hemolinkbackend.dto.response.CentreCollecteResponseDto;
import com.example.hemolinkbackend.service.CentreCollecteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/centres-collecte")
@RequiredArgsConstructor
public class CentreCollecteController {

    private final CentreCollecteService centreCollecteService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CentreCollecteResponseDto creer(@RequestBody CentreCollecteDto dto) {
        return centreCollecteService.creer(dto);
    }

    @PutMapping("/{id}")
    public CentreCollecteResponseDto mettreAJour(@PathVariable Long id, @RequestBody CentreCollecteDto dto) {
        return centreCollecteService.mettreAJour(id, dto);
    }

    @GetMapping("/{id}")
    public CentreCollecteResponseDto getById(@PathVariable Long id) {
        return centreCollecteService.getById(id);
    }

    @GetMapping
    public List<CentreCollecteResponseDto> getAll(@RequestParam(required = false) String ville,
                                                  @RequestParam(required = false) String nom) {
        if (ville != null && !ville.isBlank()) {
            return centreCollecteService.rechercherParVille(ville);
        }
        if (nom != null && !nom.isBlank()) {
            return centreCollecteService.rechercherParNom(nom);
        }
        return centreCollecteService.getAll();
    }

    @GetMapping("/proches")
    public List<CentreCollecteResponseDto> rechercherPlusProches(@RequestParam Double latitude,
                                                                 @RequestParam Double longitude) {
        return centreCollecteService.rechercherPlusProches(latitude, longitude);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void supprimer(@PathVariable Long id) {
        centreCollecteService.supprimer(id);
    }
}

