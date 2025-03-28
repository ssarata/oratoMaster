package com.bestprogramer.bestprogramer.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.bestprogramer.bestprogramer.models.Examen;
import com.bestprogramer.bestprogramer.services.ExamenService;

@Controller
@RequestMapping("/examens")
public class ExamenController {
    @Autowired
    private ExamenService examenService;

    @GetMapping
    public String listeExamens(Model model) {
        model.addAttribute("examens", examenService.getAllExamens());
        return "examen/liste";
    }
    @GetMapping("/create")
    public String afficherFormulaire(Model model) {
        model.addAttribute("examen", new Examen());
        return "examen/create";
    }

    @PostMapping("/save")
    public String sauvegarderExamen(@ModelAttribute Examen examen) {
        System.out.println(examen);
        //examenService.saveExamen(examen);  // Enregistrer l'examen avec les questions et réponses
        return "redirect:/examens";  // Rediriger vers la liste des examens ou la page souhaitée
    }
}

