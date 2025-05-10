package com.bestprogramer.bestprogramer.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "livres")
public class Livre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titre;
    
    @Column
    private String auteur;
    
    @Column(length = 1000)
    private String description;
    
    @Column
    private String categorie;
    
    @Column
    private String origine;

    @Column(nullable = false)
    private String fichierPdf;
    
    @Column
    private LocalDateTime dateAjout = LocalDateTime.now();

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }
    
    public String getAuteur() {
        return auteur;
    }
    
    public void setAuteur(String auteur) {
        this.auteur = auteur;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getCategorie() {
        return categorie;
    }
    
    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }
    
    public String getOrigine() {
        return origine;
    }
    
    public void setOrigine(String origine) {
        this.origine = origine;
    }

    public String getFichierPdf() {
        return fichierPdf;
    }

    public void setFichierPdf(String fichierPdf) {
        this.fichierPdf = fichierPdf;
    }
    
    public LocalDateTime getDateAjout() {
        return dateAjout;
    }
    
    public void setDateAjout(LocalDateTime dateAjout) {
        this.dateAjout = dateAjout;
    }
}
