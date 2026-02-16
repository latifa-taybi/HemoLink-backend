package com.example.hemolinkbackend.entity;

import com.example.hemolinkbackend.enums.GroupeSanguin;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
public class Donneur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private Utilisateur utilisateur;

    @Enumerated(EnumType.STRING)
    private GroupeSanguin groupeSanguin;

    private LocalDate dateNaissance;

    private Double poids;

    private LocalDate dateDernierDon;

    private Integer nombreDonsAnnuel;
}
