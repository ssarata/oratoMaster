package com.bestprogramer.bestprogramer.services;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bestprogramer.bestprogramer.models.Inscription;
import com.bestprogramer.bestprogramer.models.Paiement;
import com.bestprogramer.bestprogramer.repositories.PaiementRepository;

@Service
public class PaiementService {

    @Autowired
    private PaiementRepository paiementRepository;

    public Optional<Paiement> getPaiementById(Long id) {
        return paiementRepository.findById(id);
    }

    public Paiement getPaiementByInscription(Inscription inscription) {
        return paiementRepository.findByInscription(inscription);
    }

    public Paiement initierPaiement(Inscription inscription, Double montant, String methode) {
        Paiement paiement = new Paiement();
        paiement.setDatePaiement(LocalDate.now());
        paiement.setMontant(montant);
        paiement.setMethode(methode);
        paiement.setReference(genererReference());
        paiement.setStatut("En attente");
        paiement.setInscription(inscription);
        
        return paiementRepository.save(paiement);
    }

    public Paiement confirmerPaiement(Long paiementId) {
        Optional<Paiement> paiementOpt = paiementRepository.findById(paiementId);
        if (paiementOpt.isPresent()) {
            Paiement paiement = paiementOpt.get();
            paiement.setStatut("Confirmé");
            
            // Mettre à jour le statut de l'inscription
            Inscription inscription = paiement.getInscription();
            inscription.setStatut("Confirmé");
            
            return paiementRepository.save(paiement);
        }
        return null;
    }

    public Paiement annulerPaiement(Long paiementId) {
        Optional<Paiement> paiementOpt = paiementRepository.findById(paiementId);
        if (paiementOpt.isPresent()) {
            Paiement paiement = paiementOpt.get();
            paiement.setStatut("Échoué");
            return paiementRepository.save(paiement);
        }
        return null;
    }

    private String genererReference() {
        return "PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
