package com.bestprogramer.bestprogramer.repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bestprogramer.bestprogramer.models.Formation;
import com.bestprogramer.bestprogramer.models.User;

@Repository
public interface FormationRepository extends JpaRepository<Formation, Long> {
    List<Formation> findByDateDebutAfterOrderByDateDebutAsc(LocalDate date);
    List<Formation> findByFormateur(User formateur);
}
