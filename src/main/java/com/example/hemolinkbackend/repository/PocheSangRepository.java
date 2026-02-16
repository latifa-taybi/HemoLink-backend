package com.example.hemolinkbackend.repository;

import com.example.hemolinkbackend.entity.PocheSang;
import com.example.hemolinkbackend.enums.GroupeSanguin;
import com.example.hemolinkbackend.enums.StatutSang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface PocheSangRepository extends JpaRepository<PocheSang, Long> {

    List<PocheSang> findByStatutAndGroupeSanguinOrderByDateExpirationAsc(StatutSang statut, GroupeSanguin groupeSanguin);

    List<PocheSang> findByStatutAndDateExpirationBefore(StatutSang statut, LocalDate date);

    long countByStatutAndGroupeSanguin(StatutSang statut, GroupeSanguin groupeSanguin);

    @Query("""
            select p
            from PocheSang p
            where p.statut = :statut
              and p.groupeSanguin in :groupesCompatibles
              and p.dateExpiration >= :aujourdhui
            order by p.dateExpiration asc
            """)
    List<PocheSang> trouverCompatiblesFefo(
            @Param("statut") StatutSang statut,
            @Param("groupesCompatibles") List<GroupeSanguin> groupesCompatibles,
            @Param("aujourdhui") LocalDate aujourdhui
    );

    @Query("""
            select p
            from PocheSang p
            where p.statut = :statut
              and not exists (
                    select t.id
                    from TestLabo t
                    where t.pocheSang = p
              )
            """)
    List<PocheSang> trouverSansTestParStatut(@Param("statut") StatutSang statut);
}

