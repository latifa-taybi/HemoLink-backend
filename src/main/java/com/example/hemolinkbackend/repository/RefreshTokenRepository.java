package com.example.hemolinkbackend.repository;

import com.example.hemolinkbackend.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    Optional<RefreshToken> findByUtilisateurId(Long utilisateurId);
    void deleteByUtilisateurId(Long utilisateurId);
}

