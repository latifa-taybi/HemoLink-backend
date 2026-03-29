package com.example.hemolinkbackend.repository;
import com.example.hemolinkbackend.entity.Horaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface HoraireRepository extends JpaRepository<Horaire, Long> {
    List<Horaire> findByCentreCollecteId(Long centreId);
}
