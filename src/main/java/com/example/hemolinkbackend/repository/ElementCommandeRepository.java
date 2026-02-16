package com.example.hemolinkbackend.repository;

import com.example.hemolinkbackend.entity.ElementCommande;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ElementCommandeRepository extends JpaRepository<ElementCommande, Long> {

    List<ElementCommande> findByCommandeId(Long commandeId);

    boolean existsByPocheSangId(Long pocheSangId);
}

