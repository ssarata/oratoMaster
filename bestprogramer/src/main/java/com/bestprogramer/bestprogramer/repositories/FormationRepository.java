package com.bestprogramer.bestprogramer.repositories;

import com.bestprogramer.bestprogramer.models.Formation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FormationRepository extends JpaRepository<Formation, Long> {
    // Méthodes de requête personnalisées peuvent être ajoutées ici
}
