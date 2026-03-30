package com.example.hemolinkbackend.repository;

import com.example.hemolinkbackend.entity.PocheSang;
import com.example.hemolinkbackend.enums.GroupeSanguin;
import com.example.hemolinkbackend.enums.StatutSang;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PocheSangRepository extends JpaRepository<PocheSang, Long> {

    List<PocheSang> findByStatutAndGroupeSanguinOrderByDateExpirationAsc(StatutSang statut, GroupeSanguin groupeSanguin);



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

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from PocheSang p where p.id = :id")
    Optional<PocheSang> findByIdWithLock(@Param("id") Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select p
            from PocheSang p
            where p.statut = :statut
              and p.groupeSanguin in :groupesCompatibles
              and p.dateExpiration >= :aujourdhui
            order by p.dateExpiration asc
            """)
    List<PocheSang> findCompatiblesWithLock(
            @Param("statut") StatutSang statut,
            @Param("groupesCompatibles") List<GroupeSanguin> groupesCompatibles,
            @Param("aujourdhui") LocalDate aujourdhui
    );

    @Query("""
            select p
            from PocheSang p
            where p.statut = :statut
            order by p.dateCollecte asc
            """)
    List<PocheSang> trouverSansTestParStatut(@Param("statut") StatutSang statut);
}

