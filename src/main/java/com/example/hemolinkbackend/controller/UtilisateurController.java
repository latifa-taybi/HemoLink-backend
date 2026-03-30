package com.example.hemolinkbackend.controller;

import com.example.hemolinkbackend.dto.request.UtilisateurDto;
import com.example.hemolinkbackend.dto.response.UtilisateurResponseDto;
import com.example.hemolinkbackend.enums.RoleUtilisateur;
import com.example.hemolinkbackend.service.UtilisateurService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
@RequestMapping("/api/utilisateurs")
@RequiredArgsConstructor
public class UtilisateurController {

    private final UtilisateurService utilisateurService;

    @GetMapping("/me")
    public ResponseEntity<UtilisateurResponseDto> getCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        var userDetails = (UserDetails) authentication.getPrincipal();
        var utilisateur = utilisateurService.getByEmail(userDetails.getUsername());
        return ResponseEntity.ok(utilisateur);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UtilisateurResponseDto creer(@RequestBody UtilisateurDto dto) {
        return utilisateurService.creer(dto);
    }

    @PutMapping("/{id}")
    public UtilisateurResponseDto mettreAJour(@PathVariable Long id, @RequestBody UtilisateurDto dto) {
        return utilisateurService.mettreAJour(id, dto);
    }

    @GetMapping("/{id}")
    public UtilisateurResponseDto getById(@PathVariable Long id) {
        return utilisateurService.getById(id);
    }

    @GetMapping
    public List<UtilisateurResponseDto> getAll() {
        return utilisateurService.getAll();
    }

    @GetMapping("/search/by-email")
    public UtilisateurResponseDto getByEmail(@RequestParam String email) {
        return utilisateurService.getByEmail(email);
    }

    @GetMapping("/actifs")
    public List<UtilisateurResponseDto> getActifs() {
        return utilisateurService.getActifs();
    }

    @GetMapping("/role/{role}")
    public List<UtilisateurResponseDto> getByRole(@PathVariable RoleUtilisateur role) {
        return utilisateurService.getByRole(role);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void supprimer(@PathVariable Long id) {
        utilisateurService.supprimer(id);
    }
}

