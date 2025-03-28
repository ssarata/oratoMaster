package com.bestprogramer.bestprogramer.models;

import java.util.List;

import jakarta.persistence.*;

@Entity

@Table(name = "examen")
public class Examen {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String titre;
    
    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    @ManyToOne
    private Cours cours; // Un examen appartient à un cours

    public Cours getCours() {
        return cours;
    }

    public void setCours(Cours cours) {
        this.cours = cours;
    }

    @OneToMany(mappedBy = "examen", cascade = CascadeType.ALL)
    private List<Question> questions;

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }
}
