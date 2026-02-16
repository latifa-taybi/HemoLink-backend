package com.example.hemolinkbackend.repository;

import com.example.hemolinkbackend.entity.StatistiquesStock;
import com.example.hemolinkbackend.enums.GroupeSanguin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface StatistiquesStockRepository extends JpaRepository<StatistiquesStock, Long> {

    Optional<StatistiquesStock> findTopByGroupeSanguinOrderByDateGenerationDesc(GroupeSanguin groupeSanguin);

    List<StatistiquesStock> findByDateGeneration(LocalDate dateGeneration);

    @Query("""
            select s
            from StatistiquesStock s
            where s.dateGeneration = (
                select max(ss.dateGeneration)
                from StatistiquesStock ss
            )
            """)
    List<StatistiquesStock> trouverDernierSnapshotGlobal();
}

