package com.example.hemolinkbackend.service;

import com.example.hemolinkbackend.entity.RefreshToken;
import com.example.hemolinkbackend.entity.Utilisateur;

import java.util.Optional;

public interface RefreshTokenService {
    RefreshToken createRefreshToken(Utilisateur utilisateur);
    Optional<RefreshToken> findByToken(String token);
    RefreshToken verifyExpiration(RefreshToken token);
    void deleteByUserId(Long utilisateurId);
}

