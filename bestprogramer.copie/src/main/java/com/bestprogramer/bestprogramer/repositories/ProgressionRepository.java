package com.bestprogramer.bestprogramer.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bestprogramer.bestprogramer.models.Cours;
import com.bestprogramer.bestprogramer.models.Progression;
import com.bestprogramer.bestprogramer.models.User;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProgressionRepository extends JpaRepository<Progression, Long> {
    Optional<Progression> findByUserAndCours(User user, Cours cours);

    List<Progression> findByUserAndCoursIn(User user, List<Cours> cours);

    List<Progression> findByUser(User user);
}
