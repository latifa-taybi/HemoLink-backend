package com.example.hemolinkbackend.controller;

import com.example.hemolinkbackend.dto.request.AuthLoginDto;
import com.example.hemolinkbackend.dto.response.AuthTokenResponseDto;
import com.example.hemolinkbackend.entity.Utilisateur;
import com.example.hemolinkbackend.repository.UtilisateurRepository;
import com.example.hemolinkbackend.security.JwtTokenProvider;
import com.example.hemolinkbackend.service.exception.RessourceNonTrouveeException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public AuthTokenResponseDto login(@RequestBody AuthLoginDto dto) {
        Utilisateur utilisateur = utilisateurRepository.findByEmailIgnoreCase(dto.email())
                .orElseThrow(() -> new RessourceNonTrouveeException("Utilisateur introuvable"));

        if (!utilisateur.isActif()) {
            throw new RessourceNonTrouveeException("Compte utilisateur inactif");
        }

        if (!passwordEncoder.matches(dto.motDePasse(), utilisateur.getMotDePasse())) {
            throw new RessourceNonTrouveeException("Mot de passe incorrect");
        }

        String token = jwtTokenProvider.generateToken(utilisateur.getEmail(), utilisateur.getRole().name());

        return new AuthTokenResponseDto(token, "Bearer", jwtExpiration);
    }
}

