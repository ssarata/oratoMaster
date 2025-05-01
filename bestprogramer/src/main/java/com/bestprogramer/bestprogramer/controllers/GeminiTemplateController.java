package com.bestprogramer.bestprogramer.controllers;

import com.bestprogramer.bestprogramer.services.GeminiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

@Controller
public class GeminiTemplateController {

    @Autowired
    private GeminiService geminiService;

    @GetMapping("/template/cours-et-examens")
    public String getCoursEtExamens(Model model) {
        String module = "art oratoire"; // Définir le module pour lequel générer les données

        // Récupérer les cours
        List<Map<String, Object>> cours = geminiService.generateCourse(module);

        // Récupérer les examens
        List<Map<String, Object>> examens = geminiService.generateExam(module);

        // Ajouter les données au modèle
        model.addAttribute("cours", cours);
        model.addAttribute("examens", examens);

        // Retourner le nom du template
        return "cours-et-examens"; // Assurez-vous que le fichier cours-et-examens.html existe dans templates/
    }
    //   @PostMapping("/corriger")
    // public ResponseEntity<Map<String, Object>> correctExam(@RequestBody Map<Integer, String> answers) {
    //     String module = "art oratoire"; // Définir le module pour lequel corriger l'examen
    //     List<Map<String, Object>> examQuestions = geminiService.generateExam(module); // Générer les questions
    //     Map<String, Object> correctionReport = geminiService.correctExam(answers, examQuestions);
    //     return new ResponseEntity<>(correctionReport, HttpStatus.OK);
    // }
}
