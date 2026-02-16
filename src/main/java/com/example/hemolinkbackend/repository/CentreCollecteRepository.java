package com.example.hemolinkbackend.repository;

import com.example.hemolinkbackend.entity.CentreCollecte;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CentreCollecteRepository extends JpaRepository<CentreCollecte, Long> {
    List<CentreCollecte> findByVilleIgnoreCase(String ville);
    List<CentreCollecte> findByNomContainingIgnoreCase(String nom);
}

