package com.example.hemolinkbackend.entity;

import com.example.hemolinkbackend.enums.GroupeSanguin;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDate;

@Entity
public class StatistiquesStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private GroupeSanguin groupeSanguin;

    private Integer unitesTotales;

    private Integer unitesExpirees;

    private Integer unitesTransfusees;

    private LocalDate dateGeneration;
}

