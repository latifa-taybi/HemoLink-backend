package com.example.hemolinkbackend.entity;
import jakarta.persistence.*;

@Entity
public class Donneur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;

    private String groupeSanguin;

}