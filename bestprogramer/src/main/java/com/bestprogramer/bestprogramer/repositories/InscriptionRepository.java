package com.bestprogramer.bestprogramer.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bestprogramer.bestprogramer.models.Formation;
import com.bestprogramer.bestprogramer.models.Inscription;
import com.bestprogramer.bestprogramer.models.User;

@Repository
public interface InscriptionRepository extends JpaRepository<Inscription, Long> {
    List<Inscription> findByFormation(Formation formation);
    List<Inscription> findByApprenant(User apprenant);
    boolean existsByApprenantAndFormation(User apprenant, Formation formation);
}
