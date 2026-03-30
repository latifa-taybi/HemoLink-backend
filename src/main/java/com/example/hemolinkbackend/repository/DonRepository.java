package com.example.hemolinkbackend.repository;

import com.example.hemolinkbackend.entity.Don;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface DonRepository extends JpaRepository<Don, Long> {

    List<Don> findByDonneurIdOrderByDateDonDesc(Long donneurId);

    List<Don> findByCentreId(Long centreId);

    List<Don> findByCentreIdAndDateDonBetween(Long centreId, LocalDateTime debut, LocalDateTime fin);

    long countByDonneurIdAndDateDonBetween(Long donneurId, LocalDateTime debut, LocalDateTime fin);
}

