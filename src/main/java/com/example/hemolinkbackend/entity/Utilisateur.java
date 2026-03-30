package com.example.hemolinkbackend.entity;

import com.example.hemolinkbackend.enums.RoleUtilisateur;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String prenom;

    private String nom;

    @Column(unique = true)
    private String email;

    private String motDePasse;

    private String telephone;

    @Enumerated(EnumType.STRING)
    private RoleUtilisateur role;

    @ManyToOne
    private CentreCollecte centreCollecte;

    @ManyToOne
    private Hopital hopital;

    private boolean actif;

    private LocalDateTime creeLe;
}
