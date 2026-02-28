package com.example.hemolinkbackend.controller;

import com.example.hemolinkbackend.dto.request.HopitalDto;
import com.example.hemolinkbackend.dto.response.HopitalResponseDto;
import com.example.hemolinkbackend.service.HopitalService;
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
@RequestMapping("/api/hopitaux")
@RequiredArgsConstructor
public class HopitalController {

    private final HopitalService hopitalService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public HopitalResponseDto creer(@RequestBody HopitalDto dto) {
        return hopitalService.creer(dto);
    }

    @PutMapping("/{id}")
    public HopitalResponseDto mettreAJour(@PathVariable Long id, @RequestBody HopitalDto dto) {
        return hopitalService.mettreAJour(id, dto);
    }

    @GetMapping("/{id}")
    public HopitalResponseDto getById(@PathVariable Long id) {
        return hopitalService.getById(id);
    }

    @GetMapping
    public List<HopitalResponseDto> getAll(@RequestParam(required = false) String ville,
                                           @RequestParam(required = false) String nom) {
        if (ville != null && !ville.isBlank()) {
            return hopitalService.rechercherParVille(ville);
        }
        if (nom != null && !nom.isBlank()) {
            return hopitalService.rechercherParNom(nom);
        }
        return hopitalService.getAll();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void supprimer(@PathVariable Long id) {
        hopitalService.supprimer(id);
    }
}

