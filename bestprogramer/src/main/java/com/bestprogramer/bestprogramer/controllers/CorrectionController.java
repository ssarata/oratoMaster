package com.bestprogramer.bestprogramer.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.bestprogramer.bestprogramer.models.Reponse;
import com.bestprogramer.bestprogramer.repositories.ReponseRepository;

@Controller
@RequestMapping("/examens")
public class CorrectionController {
    @Autowired
    private ReponseRepository reponseRepository;

    @PostMapping("/submit")
    public String corrigerExamen(@RequestParam Map<Long, List<Long>> reponsesEleve, Model model) {
        int totalQuestions = reponsesEleve.size();
        int bonnesReponses = 0;

        for (Long questionId : reponsesEleve.keySet()) {
            List<Long> reponsesSelectionnees = reponsesEleve.get(questionId);
            List<Reponse> reponsesCorrectes = reponseRepository.findCorrectAnswersByQuestionId(questionId);

            if (reponsesSelectionnees.containsAll(reponsesCorrectes) && reponsesCorrectes.containsAll(reponsesSelectionnees)) {
                bonnesReponses++;
            }
        }

        int scorePourcentage = (bonnesReponses * 100) / totalQuestions;
        model.addAttribute("score", scorePourcentage);
        return "examen/resultat";
    }
}
