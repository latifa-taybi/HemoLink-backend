package com.example.hemolinkbackend.entity;

import com.example.hemolinkbackend.enums.GroupeSanguin;
import com.example.hemolinkbackend.enums.StatutCommande;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

import java.time.LocalDateTime;

@Entity
public class CommandeSang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Hopital hopital;

    @Enumerated(EnumType.STRING)
    private GroupeSanguin groupeSanguin;

    private Integer quantite;

    private Boolean urgence;

    @Enumerated(EnumType.STRING)
    private StatutCommande statut;

    private LocalDateTime dateCommande;
}

