package com.bestprogramer.bestprogramer.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bestprogramer.bestprogramer.models.Examen;
import com.bestprogramer.bestprogramer.models.Question;
import com.bestprogramer.bestprogramer.repositories.ExamenRepository;
import com.bestprogramer.bestprogramer.repositories.QuestionRepository;

@Service
public class QuestionService {
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private ExamenRepository examenRepository;


    public List<Question> getQuestionsByExamen(Long examenId) {
            Examen examen= examenRepository.findById(examenId).orElseThrow(()->new RuntimeException("cour non trouvée"));
    
         
        return questionRepository.findByExamen(examen);
    }
}
