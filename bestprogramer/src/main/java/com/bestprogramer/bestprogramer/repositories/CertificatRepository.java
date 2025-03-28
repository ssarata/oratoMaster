package com.bestprogramer.bestprogramer.repositories;

import com.bestprogramer.bestprogramer.models.Certificat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CertificatRepository extends JpaRepository<Certificat, Long> {
    // Méthodes de requête personnalisées peuvent être ajoutées ici
}
