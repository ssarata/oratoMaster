package com.bestprogramer.bestprogramer.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.bestprogramer.bestprogramer.models.Reponse;

@Repository
public interface ReponseRepository extends JpaRepository<Reponse, Long> {

    @Query("SELECT r FROM Reponse r WHERE r.question.id = :questionId AND r.estCorrecte = true")
    List<Reponse> findCorrectAnswersByQuestionId(@Param("questionId") Long questionId);
}
