package com.example.hemolinkbackend.repository;

import com.example.hemolinkbackend.entity.Hopital;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HopitalRepository extends JpaRepository<Hopital, Long> {

    List<Hopital> findByVilleIgnoreCase(String ville);

    List<Hopital> findByNomContainingIgnoreCase(String nom);
}

