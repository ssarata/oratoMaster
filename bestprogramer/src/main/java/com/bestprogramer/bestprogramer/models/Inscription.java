package com.bestprogramer.bestprogramer.models;

import java.time.LocalDate;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "inscriptions")
public class Inscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate dateInscription;

    @ManyToOne
    @JoinColumn(name = "apprenant_id")
    private User apprenant;

    @ManyToOne
    @JoinColumn(name = "formation_id")
    private Formation formation;

    @OneToOne(mappedBy = "inscription", cascade = CascadeType.ALL, orphanRemoval = true)
    private Paiement paiement;

    @Column(nullable = false)
    private String statut; // "En attente", "Confirmé", "Annulé"

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDateInscription() {
        return dateInscription;
    }

    public void setDateInscription(LocalDate dateInscription) {
        this.dateInscription = dateInscription;
    }

    public User getApprenant() {
        return apprenant;
    }

    public void setApprenant(User apprenant) {
        this.apprenant = apprenant;
    }

    public Formation getFormation() {
        return formation;
    }

    public void setFormation(Formation formation) {
        this.formation = formation;
    }

    public Paiement getPaiement() {
        return paiement;
    }

    public void setPaiement(Paiement paiement) {
        this.paiement = paiement;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }
    
    // Méthode utilitaire pour vérifier si le paiement est effectué
    public boolean isPaiementEffectue() {
        return paiement != null && "Confirmé".equals(paiement.getStatut());
    }
}
