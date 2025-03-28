
package com.bestprogramer.bestprogramer.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bestprogramer.bestprogramer.models.Examen;
import com.bestprogramer.bestprogramer.models.Question;
import com.bestprogramer.bestprogramer.models.Reponse;
import com.bestprogramer.bestprogramer.repositories.ExamenRepository;
import com.bestprogramer.bestprogramer.repositories.QuestionRepository;
import com.bestprogramer.bestprogramer.repositories.ReponseRepository;

@Service
public class ExamenService {
    @Autowired
    private ExamenRepository examenRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private ReponseRepository reponseRepository;

    public void saveExamen(Examen examen) {
        // Associer chaque question à l'examen et ses réponses à chaque question
        for (Question question : examen.getQuestions()) {
            question.setExamen(examen);  // Associer la question à l'examen
            for (Reponse reponse : question.getReponses()) {
                reponse.setQuestion(question);  // Associer la réponse à la question
            }
        }
        examenRepository.save(examen); // Sauvegarde l'examen avec ses questions et réponses

    }
    public List<Examen> getAllExamens() {
        return examenRepository.findAll();
    }
 
    public Examen getExamenById(Long id) {
        return examenRepository.findById(id).orElse(null);
    }
}
