package com.bestprogramer.bestprogramer.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "exercices_video")
public class ExerciceVideo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titre;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String videoUrl;

    @Column(nullable = true, length = 2000)
    private String analyseGemini;

    @Column(nullable = true)
    private Double noteGemini;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDateTime dateCreation;

    @Column(nullable = false)
    private boolean publique;

    @Column(nullable = true)
    private String consigne;

    @Column(nullable = true)
    private String categorie;

    @Column(nullable = true)
    private Integer duree;

    @Column(nullable = true)
    private Integer vues;

    @Column(nullable = true)
    private Integer likes;

    // Constructeurs
    public ExerciceVideo() {
        this.dateCreation = LocalDateTime.now();
        this.vues = 0;
        this.likes = 0;
    }

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getAnalyseGemini() {
        return analyseGemini;
    }

    public void setAnalyseGemini(String analyseGemini) {
        this.analyseGemini = analyseGemini;
    }

    public Double getNoteGemini() {
        return noteGemini;
    }

    public void setNoteGemini(Double noteGemini) {
        this.noteGemini = noteGemini;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public boolean isPublique() {
        return publique;
    }

    public void setPublique(boolean publique) {
        this.publique = publique;
    }

    public String getConsigne() {
        return consigne;
    }

    public void setConsigne(String consigne) {
        this.consigne = consigne;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public Integer getDuree() {
        return duree;
    }

    public void setDuree(Integer duree) {
        this.duree = duree;
    }

    public Integer getVues() {
        return vues;
    }

    public void setVues(Integer vues) {
        this.vues = vues;
    }

    public Integer getLikes() {
        return likes;
    }

    public void setLikes(Integer likes) {
        this.likes = likes;
    }

    public void incrementVues() {
        this.vues = (this.vues == null) ? 1 : this.vues + 1;
    }

    public void incrementLikes() {
        this.likes = (this.likes == null) ? 1 : this.likes + 1;
    }
}
