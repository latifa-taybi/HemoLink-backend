package com.example.hemolinkbackend.repository;

import com.example.hemolinkbackend.entity.RendezVous;
import com.example.hemolinkbackend.enums.StatutRendezVous;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface RendezVousRepository extends JpaRepository<RendezVous, Long> {

    List<RendezVous> findByDonneurIdOrderByDateRendezVousDesc(Long donneurId);

    List<RendezVous> findByCentreIdAndDateRendezVousBetween(Long centreId, LocalDateTime debut, LocalDateTime fin);


    boolean existsByDonneurIdAndDateRendezVousBetween(Long donneurId, LocalDateTime debut, LocalDateTime fin);
}

