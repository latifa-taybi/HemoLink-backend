package com.example.hemolinkbackend.controller;

import com.example.hemolinkbackend.dto.request.PocheSangDto;
import com.example.hemolinkbackend.dto.response.PocheSangResponseDto;
import com.example.hemolinkbackend.enums.GroupeSanguin;
import com.example.hemolinkbackend.enums.StatutSang;
import com.example.hemolinkbackend.service.PocheSangService;
import lombok.RequiredArgsConstructor;
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

import java.util.List;

@RestController
@RequestMapping("/api/poches-sang")
@RequiredArgsConstructor
public class PocheSangController {

    private final PocheSangService pocheSangService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PocheSangResponseDto creer(@RequestBody PocheSangDto dto) {
        return pocheSangService.creer(dto);
    }

    @PostMapping("/depuis-don/{donId}")
    @ResponseStatus(HttpStatus.CREATED)
    public PocheSangResponseDto creerDepuisDon(@PathVariable Long donId) {
        return pocheSangService.creerDepuisDon(donId);
    }

    @GetMapping("/{id}")
    public PocheSangResponseDto getById(@PathVariable Long id) {
        return pocheSangService.getById(id);
    }

    @GetMapping
    public List<PocheSangResponseDto> getAll() {
        return pocheSangService.getAll();
    }

    @GetMapping("/disponibles")
    public List<PocheSangResponseDto> getDisponibles(@RequestParam GroupeSanguin groupeSanguin) {
        return pocheSangService.getDisponiblesParGroupe(groupeSanguin);
    }

    @GetMapping("/sans-test")
    public List<PocheSangResponseDto> getSansTest() {
        return pocheSangService.getPochesSansTest();
    }

    @GetMapping("/expirees")
    public List<PocheSangResponseDto> getExpirees() {
        return pocheSangService.getPochesExpirees();
    }

    @GetMapping("/compatibles")
    public List<PocheSangResponseDto> getCompatibles(@RequestParam GroupeSanguin groupeReceveur,
                                                     @RequestParam(required = false) Integer quantite) {
        return pocheSangService.getCompatiblesFefo(groupeReceveur, quantite);
    }

    @PostMapping("/reservations")
    public List<PocheSangResponseDto> reserverCompatibles(@RequestParam GroupeSanguin groupeReceveur,
                                                          @RequestParam Integer quantite) {
        return pocheSangService.reserverCompatiblesFefo(groupeReceveur, quantite);
    }

    @PatchMapping("/{id}/statut")
    public PocheSangResponseDto changerStatut(@PathVariable Long id, @RequestParam StatutSang statut) {
        return pocheSangService.changerStatut(id, statut);
    }

    @PatchMapping("/{id}/transfuser")
    public PocheSangResponseDto transfuser(@PathVariable Long id) {
        return pocheSangService.marquerCommeTransfusee(id);
    }

    @PatchMapping("/{id}/ecarter")
    public PocheSangResponseDto ecarter(@PathVariable Long id) {
        return pocheSangService.marquerCommeEcartee(id);
    }
}

