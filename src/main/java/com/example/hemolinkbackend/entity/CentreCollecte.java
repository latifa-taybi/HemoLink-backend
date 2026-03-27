package com.example.hemolinkbackend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

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

    @Column(columnDefinition = "TEXT", nullable = true)
    private String horairesOuverture;

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

    // ✅ Initialiser horairesOuverture avec un tableau vide par défaut
    public void initializeHorairesOuverture() {
        if (this.horairesOuverture == null) {
            this.horairesOuverture = "[]";
        }
    }
}

