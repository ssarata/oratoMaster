package com.bestprogramer.bestprogramer.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bestprogramer.bestprogramer.models.Inscription;
import com.bestprogramer.bestprogramer.models.Paiement;

@Repository
public interface PaiementRepository extends JpaRepository<Paiement, Long> {
    Paiement findByInscription(Inscription inscription);
}
