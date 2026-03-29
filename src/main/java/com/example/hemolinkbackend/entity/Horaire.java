package com.example.hemolinkbackend.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalTime;
@Entity
@Getter
@Setter
public class Horaire {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String jour;
    private LocalTime ouverture;
    private LocalTime fermeture;
    private Boolean ouvert;
    @ManyToOne
    @JoinColumn(name = "centre_collecte_id")
    private CentreCollecte centreCollecte;
}
