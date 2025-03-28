package com.bestprogramer.bestprogramer.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bestprogramer.bestprogramer.models.Paiement;

public interface PaiementRepository extends JpaRepository<Paiement, Long> {
}
