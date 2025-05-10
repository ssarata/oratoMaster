package com.bestprogramer.bestprogramer.repositories;

import com.bestprogramer.bestprogramer.models.Livre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LivreRepository extends JpaRepository<Livre, Long> {
    // Méthode pour rechercher des livres par titre (insensible à la casse)
    List<Livre> findByTitreContainingIgnoreCase(String titre);
    
    // Méthode pour rechercher des livres par catégorie
    List<Livre> findByCategorieIgnoreCase(String categorie);
    
    // Méthode pour rechercher des livres par origine
    List<Livre> findByOrigineIgnoreCase(String origine);
    
    // Méthode pour rechercher des livres par auteur
    List<Livre> findByAuteurContainingIgnoreCase(String auteur);
    
    // Méthode combinant titre et auteur
    List<Livre> findByTitreContainingIgnoreCaseOrAuteurContainingIgnoreCase(String titre, String auteur);
}
