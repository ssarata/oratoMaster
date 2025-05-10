package com.bestprogramer.bestprogramer.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.bestprogramer.bestprogramer.models.CoursModel;
import com.bestprogramer.bestprogramer.repositories.CoursRepository;
import com.bestprogramer.bestprogramer.services.GeminiService;

@Controller
@RequestMapping("/cours")
public class CoursController {
    @Autowired
    private  GeminiService geminiService;
    @Autowired

    private  CoursRepository coursRepository;

    // public CoursController(GeminiService geminiService, CoursRepository coursRepository) {
    //     this.geminiService = geminiService;
    //     this.coursRepository = coursRepository;
    // }

    @GetMapping
    public String index(Model model) {
        // Récupérer la liste des cours
        List<CoursModel> cours = coursRepository.findAll();

    
        model.addAttribute("cours", cours);
        return "cours/index"; 
    }

    @GetMapping("/create")
    public String showCourseForm(Model model) {
        model.addAttribute("cours", new CoursModel());

        return "cours/create"; // This should correspond to the name of your HTML template
    }

    @PostMapping("/create")
    public String createCourse(CoursModel coursModel, Model model) {
        // Récupérer le TgetTitre depuis le formulaire soumis
        String titre = coursModel.getTitre(); // Assurez-vous que CoursModel a un champ "TgetTitre" avec ses getters et setters
    
        // Récupérer les cours en fonction du TgetTitre
        List<Map<String, Object>> cours = geminiService.generateCourse(titre);
        //CoursModel.setCours(cours);
        // Enregistrer le cours dans la base de données

    
        // Ajouter un message de succès
        model.addAttribute("message", "Course created successfully!");
        return "redirect:/cours"; // Rediriger vers une page pertinente après la création
    }
    
}
