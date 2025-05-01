package com.bestprogramer.bestprogramer.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "livres")
public class Livre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titre;

    @Column(nullable = false)
    private String auteur;

    @Column(nullable = false)
    private String origine; // "africain" ou "europeen"

    @Column(nullable = false)
    private String couverture;

    @Column(nullable = false)
    private String fichierPdf;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;
}
