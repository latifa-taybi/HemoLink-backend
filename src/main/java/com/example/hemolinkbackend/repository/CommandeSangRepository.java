package com.example.hemolinkbackend.repository;

import com.example.hemolinkbackend.entity.CommandeSang;
import com.example.hemolinkbackend.enums.StatutCommande;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommandeSangRepository extends JpaRepository<CommandeSang, Long> {

    List<CommandeSang> findByHopitalIdOrderByDateCommandeDesc(Long hopitalId);

    List<CommandeSang> findByCentreCollecteIdOrderByDateCommandeDesc(Long centreCollecteId);

    @Query("""
            select c
            from CommandeSang c
            where c.urgence = true
              and c.statut in :statutsActifs
            order by c.dateCommande asc
            """)
    List<CommandeSang> trouverUrgencesActives(@Param("statutsActifs") List<StatutCommande> statutsActifs);
}

