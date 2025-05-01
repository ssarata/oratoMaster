package com.bestprogramer.bestprogramer.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bestprogramer.bestprogramer.models.Examen;
import com.bestprogramer.bestprogramer.models.Soumission;
import com.bestprogramer.bestprogramer.models.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface SoumissionRepository extends JpaRepository<Soumission, Long> {
    Optional<Soumission> findByUserAndExamen(User user, Examen examen);
    List<Soumission> findByUserAndExamenIn(User user, List<Examen> examens);
}
