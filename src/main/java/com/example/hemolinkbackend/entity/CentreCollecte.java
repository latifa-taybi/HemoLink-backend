package com.example.hemolinkbackend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.awt.Point;

@Entity
public class CentreCollecte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;

    private String adresse;

    private String ville;

    private Double latitude;

    private Double longitude;

    @Column(columnDefinition = "geography(Point,4326)")
    private Point localisationGps;

    private String horairesOuverture;

    private String telephone;
}

