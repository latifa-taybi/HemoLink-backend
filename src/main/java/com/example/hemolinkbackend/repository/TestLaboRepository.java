package com.example.hemolinkbackend.repository;

import com.example.hemolinkbackend.entity.TestLabo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TestLaboRepository extends JpaRepository<TestLabo, Long> {

    Optional<TestLabo> findByPocheSangId(Long pocheSangId);

    boolean existsByPocheSangId(Long pocheSangId);

    List<TestLabo> findByTechnicienLaboIdAndDateTestBetween(Long technicienLaboId, LocalDateTime debut, LocalDateTime fin);
}

