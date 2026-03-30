package com.example.hemolinkbackend.service.impl;

import com.example.hemolinkbackend.entity.RefreshToken;
import com.example.hemolinkbackend.entity.Utilisateur;
import com.example.hemolinkbackend.repository.RefreshTokenRepository;
import com.example.hemolinkbackend.service.RefreshTokenService;
import com.example.hemolinkbackend.service.exception.RessourceNonTrouveeException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.refresh.expiration}")
    private long refreshTokenExpiration;

    @Override
    public RefreshToken createRefreshToken(Utilisateur utilisateur) {
        deleteByUserId(utilisateur.getId());

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUtilisateur(utilisateur);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setCreatedAt(LocalDateTime.now());
        refreshToken.setExpiryDate(LocalDateTime.now().plusSeconds(refreshTokenExpiration / 1000));

        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    @Override
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.isExpired()) {
            refreshTokenRepository.delete(token);
            throw new RessourceNonTrouveeException("Refresh token expiré");
        }
        return token;
    }

    @Override
    public void deleteByUserId(Long utilisateurId) {
        refreshTokenRepository.deleteByUtilisateurId(utilisateurId);
    }
}

