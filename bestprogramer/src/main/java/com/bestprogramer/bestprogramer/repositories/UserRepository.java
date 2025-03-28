package com.bestprogramer.bestprogramer.repositories;


import com.bestprogramer.bestprogramer.models.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Méthodes de requête personnalisées peuvent être ajoutées ici
}


