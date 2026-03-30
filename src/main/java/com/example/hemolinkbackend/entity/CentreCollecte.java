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

    public void addHoraire(Horaire horaire) {
        horaires.add(horaire);
        horaire.setCentreCollecte(this);
    }

    public void removeHoraire(Horaire horaire) {
        horaires.remove(horaire);
        horaire.setCentreCollecte(null);
    }
}

