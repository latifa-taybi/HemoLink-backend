package com.example.hemolinkbackend.service;

import com.example.hemolinkbackend.dto.request.InscriptionDto;
import com.example.hemolinkbackend.dto.request.UtilisateurDto;
import com.example.hemolinkbackend.dto.response.UtilisateurResponseDto;
import com.example.hemolinkbackend.enums.RoleUtilisateur;

import java.util.List;

public interface UtilisateurService {

    UtilisateurResponseDto sinscrire(InscriptionDto dto);

    UtilisateurResponseDto creer(UtilisateurDto dto);

    UtilisateurResponseDto mettreAJour(Long id, UtilisateurDto dto);

    UtilisateurResponseDto getById(Long id);

    List<UtilisateurResponseDto> getAll();

    UtilisateurResponseDto getByEmail(String email);

    List<UtilisateurResponseDto> getActifs();

    List<UtilisateurResponseDto> getByRole(RoleUtilisateur role);

    void supprimer(Long id);
}
