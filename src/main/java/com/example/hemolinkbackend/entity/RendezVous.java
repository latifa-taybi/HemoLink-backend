package com.example.hemolinkbackend.entity;

import com.example.hemolinkbackend.enums.StatutRendezVous;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

import java.time.LocalDateTime;

@Entity
public class RendezVous {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Donneur donneur;

    @ManyToOne
    private CentreCollecte centre;

    private LocalDateTime dateRendezVous;

    @Enumerated(EnumType.STRING)
    private StatutRendezVous statut;
}

