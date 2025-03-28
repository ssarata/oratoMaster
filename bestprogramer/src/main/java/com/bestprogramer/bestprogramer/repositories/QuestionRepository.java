
package com.bestprogramer.bestprogramer.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bestprogramer.bestprogramer.models.Examen;
import com.bestprogramer.bestprogramer.models.Question;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {


    List<Question> findByExamen(Examen examen); }
