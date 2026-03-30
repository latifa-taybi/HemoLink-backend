package com.example.hemolinkbackend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class CentreCollecte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String adresse;
    private String ville;
    private Double latitude;
    private Double longitude;

    @OneToMany(mappedBy = "centreCollecte", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Horaire> horaires = new ArrayList<>();

    private String telephone;

    public void addHoraire(Horaire horaire) {
        horaires.add(horaire);
        horaire.setCentreCollecte(this);
    }

    public void removeHoraire(Horaire horaire) {
        horaires.remove(horaire);
        horaire.setCentreCollecte(null);
    }
}

