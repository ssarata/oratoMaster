package com.bestprogramer.bestprogramer.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bestprogramer.bestprogramer.models.Question;
import com.bestprogramer.bestprogramer.models.Quiz;



public interface QuestionRepository  extends JpaRepository<Question, Long> {
    List<Question> findByQuiz(Quiz quiz);

    
}
