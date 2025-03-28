package com.bestprogramer.bestprogramer.controllers;

import com.bestprogramer.bestprogramer.models.Cours;
import com.bestprogramer.bestprogramer.services.CoursService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;

@Controller
@RequestMapping("/cours")
public class CoursController {
    private final CoursService coursService;

    public CoursController(CoursService coursService) {
        this.coursService = coursService;
    }

    @GetMapping
    public String listCours(Model model) {
        model.addAttribute("coursList", coursService.getAllCours());
        return "cours/index";
    }

    @GetMapping("/create")
    public String createCoursForm(Model model) {
        model.addAttribute("cours", new Cours());
        return "cours/create";
    }

    @PostMapping("/save")
    public String saveCours(@ModelAttribute Cours cours, @RequestParam("file") MultipartFile file) {
        try {
            coursService.saveCours(cours, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "redirect:/cours";
    }

    @GetMapping("/edit/{id}")
    public String editCoursForm(@PathVariable Long id, Model model) {
        model.addAttribute("cours", coursService.getCoursById(id).orElseThrow());
        return "cours/update";
    }

    @PostMapping("/update/{id}")
    public String updateCours(@PathVariable Long id, @ModelAttribute Cours cours, @RequestParam("file") MultipartFile file) {
        try {
            cours.setId(id);
            coursService.saveCours(cours, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "redirect:/cours";
    }

    @GetMapping("/delete/{id}")
    public String deleteCours(@PathVariable Long id) {
        coursService.deleteCours(id);
        return "redirect:/cours";
    }

 
}
