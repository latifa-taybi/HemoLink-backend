package com.example.hemolinkbackend.repository;

import com.example.hemolinkbackend.entity.Donneur;
import com.example.hemolinkbackend.enums.GroupeSanguin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DonneurRepository extends JpaRepository<Donneur, Long> {

    Optional<Donneur> findByUtilisateurId(Long utilisateurId);

    List<Donneur> findByGroupeSanguin(GroupeSanguin groupeSanguin);

    List<Donneur> findByDateDernierDonBefore(LocalDate date);

    List<Donneur> findByNombreDonsAnnuelLessThan(Integer limite);

    List<Donneur> findByUtilisateurNomContainingIgnoreCaseOrUtilisateurPrenomContainingIgnoreCase(String nom, String prenom);
}

