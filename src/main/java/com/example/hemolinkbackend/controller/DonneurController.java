package com.example.hemolinkbackend.controller;

import com.example.hemolinkbackend.dto.request.DonneurDto;
import com.example.hemolinkbackend.dto.response.DonneurResponseDto;
import com.example.hemolinkbackend.enums.GroupeSanguin;
import com.example.hemolinkbackend.service.DonneurService;
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
import java.util.Map;

@RestController
@RequestMapping("/api/donneurs")
@RequiredArgsConstructor
public class DonneurController {

    private final DonneurService donneurService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DonneurResponseDto creer(@RequestBody DonneurDto dto) {
        return donneurService.creer(dto);
    }

    @PutMapping("/{id}")
    public DonneurResponseDto mettreAJour(@PathVariable Long id, @RequestBody DonneurDto dto) {
        return donneurService.mettreAJour(id, dto);
    }

    @GetMapping("/{id}")
    public DonneurResponseDto getById(@PathVariable Long id) {
        return donneurService.getById(id);
    }

    @GetMapping
    public List<DonneurResponseDto> getAll(@RequestParam(required = false) GroupeSanguin groupeSanguin) {
        return groupeSanguin != null ? donneurService.getByGroupeSanguin(groupeSanguin) : donneurService.getAll();
    }

    @GetMapping("/utilisateur/{utilisateurId}")
    public DonneurResponseDto getByUtilisateurId(@PathVariable Long utilisateurId) {
        return donneurService.getByUtilisateurId(utilisateurId);
    }

    @GetMapping("/search")
    public List<DonneurResponseDto> rechercher(@RequestParam String motCle) {
        return donneurService.rechercherParNomOuPrenom(motCle);
    }

    @GetMapping("/eligibles")
    public List<DonneurResponseDto> getEligibles(@RequestParam GroupeSanguin groupeSanguin) {
        return donneurService.getDonneursEligiblesParGroupe(groupeSanguin);
    }

    @GetMapping("/{id}/eligibilite")
    public Map<String, Object> verifierEligibilite(@PathVariable Long id) {
        return Map.of("donneurId", id, "eligible", donneurService.verifierEligibilite(id));
    }

    @GetMapping("/{id}/prochaine-date-eligible")
    public Map<String, Object> prochaineDateEligible(@PathVariable Long id) {
        return Map.of("donneurId", id, "date", donneurService.calculerProchaineDateEligible(id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void supprimer(@PathVariable Long id) {
        donneurService.supprimer(id);
    }
}

