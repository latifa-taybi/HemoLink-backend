package com.example.hemolinkbackend.repository;

import com.example.hemolinkbackend.entity.Donneur;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DonneurRepository extends JpaRepository<Donneur, Long> {
}
