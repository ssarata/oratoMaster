package com.bestprogramer.bestprogramer.repositories;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import com.bestprogramer.bestprogramer.models.CoursModel;
import com.bestprogramer.bestprogramer.models.Quiz;
@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {
    // List<Quiz> findByQuizs(CoursModel cours);

    List<Quiz> findByCours(CoursModel cours);
}
