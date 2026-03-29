package com.example.hemolinkbackend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

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

    @Column(columnDefinition = "geography(Point,4326)", nullable = true)
    private Point localisationGps;

    // ✅ Nouvelle relation OneToMany avec Horaire
    @OneToMany(mappedBy = "centreCollecte", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Horaire> horaires = new ArrayList<>();

    private String telephone;

    // ✅ Méthode pour initialiser localisationGps à partir de latitude/longitude
    public void initializeLocalization() {
        if (this.latitude != null && this.longitude != null) {
            GeometryFactory geometryFactory = new GeometryFactory();
            this.localisationGps = geometryFactory.createPoint(
                    new Coordinate(this.longitude, this.latitude)
            );
        }
    }

    // ✅ Méthode pour initialiser les horaires par défaut (7 jours)
    public void initializeHorairesParDefaut() {
        if (this.horaires == null || this.horaires.isEmpty()) {
            String[] jours = {"LUNDI", "MARDI", "MERCREDI", "JEUDI", "VENDREDI", "SAMEDI", "DIMANCHE"};

            for (String jour : jours) {
                Horaire horaire = new Horaire();
                horaire.setJour(jour);
                horaire.setCentreCollecte(this);

                if (jour.equals("DIMANCHE")) {
                    // Dimanche: fermé
                    horaire.setOuvert(false);
                    horaire.setOuverture(java.time.LocalTime.of(9, 0));
                    horaire.setFermeture(java.time.LocalTime.of(13, 0));
                } else {
                    // Autres jours: 8h-17h
                    horaire.setOuvert(true);
                    horaire.setOuverture(java.time.LocalTime.of(8, 0));
                    horaire.setFermeture(java.time.LocalTime.of(17, 0));
                }

                this.horaires.add(horaire);
            }
        }
    }
}

