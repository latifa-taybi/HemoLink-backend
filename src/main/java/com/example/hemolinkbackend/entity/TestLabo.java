package com.example.hemolinkbackend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;

import java.time.LocalDateTime;

@Entity
public class TestLabo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private PocheSang pocheSang;

    private Boolean vih;

    private Boolean hepatiteB;

    private Boolean hepatiteC;

    private Boolean syphilis;

    private LocalDateTime dateTest;

    @ManyToOne
    private Utilisateur technicienLabo;
}

