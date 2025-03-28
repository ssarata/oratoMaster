package com.bestprogramer.bestprogramer.models;

import java.util.List;

import jakarta.persistence.*;

@Table(name = "question")
@Entity
public class Question {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String texte;
    private int point;


    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public String getTexte() {
        return texte;
    }

    public void setTexte(String texte) {
        this.texte = texte;
    }

    @ManyToOne
    private Examen examen;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    private List<Reponse> reponses;


    public void setExamen(Examen examen2) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setExamen'");
    }

    public Reponse[] getReponses() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getReponses'");
    }

    // public Object getExamen() {
    //     // TODO Auto-generated method stub
    //     throw new UnsupportedOperationException("Unimplemented method 'getExamen'");
    // }
}
