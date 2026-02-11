package com.example.hemolinkbackend.entity;

import com.example.hemolinkbackend.enums.GroupeSanguin;
import com.example.hemolinkbackend.enums.StatutSang;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

import java.time.LocalDate;

@Entity
public class PocheSang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Don don;

    @Enumerated(EnumType.STRING)
    private GroupeSanguin groupeSanguin;

    private LocalDate dateCollecte;

    private LocalDate dateExpiration;

    @Enumerated(EnumType.STRING)
    private StatutSang statut;
}

