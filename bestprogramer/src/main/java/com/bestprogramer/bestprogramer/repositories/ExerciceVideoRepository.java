package com.bestprogramer.bestprogramer.repositories;

import com.bestprogramer.bestprogramer.models.ExerciceVideo;
import com.bestprogramer.bestprogramer.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExerciceVideoRepository extends JpaRepository<ExerciceVideo, Long> {
    
    // Trouver tous les exercices vidéo d'un utilisateur
    List<ExerciceVideo> findByUser(User user);
    
    // Trouver tous les exercices vidéo publics
    List<ExerciceVideo> findByPubliqueTrue();
    
    // Trouver les exercices vidéo publics avec pagination
    Page<ExerciceVideo> findByPubliqueTrue(Pageable pageable);
    
    // Trouver les exercices vidéo d'un utilisateur avec pagination
    Page<ExerciceVideo> findByUser(User user, Pageable pageable);
    
    // Rechercher des exercices vidéo par titre ou description
    Page<ExerciceVideo> findByPubliqueTrueAndTitreContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            String titre, String description, Pageable pageable);
    
    // Trouver les exercices vidéo par catégorie
    Page<ExerciceVideo> findByPubliqueTrueAndCategorieIgnoreCase(String categorie, Pageable pageable);
    
    // Trouver les exercices vidéo les plus vus
    @Query("SELECT e FROM ExerciceVideo e WHERE e.publique = true ORDER BY e.vues DESC")
    Page<ExerciceVideo> findMostViewed(Pageable pageable);
    
    // Trouver les exercices vidéo les mieux notés
    @Query("SELECT e FROM ExerciceVideo e WHERE e.publique = true AND e.noteGemini IS NOT NULL ORDER BY e.noteGemini DESC")
    Page<ExerciceVideo> findBestRated(Pageable pageable);
    
    // Trouver les exercices vidéo récents
    @Query("SELECT e FROM ExerciceVideo e WHERE e.publique = true ORDER BY e.dateCreation DESC")
    Page<ExerciceVideo> findMostRecent(Pageable pageable);
}
