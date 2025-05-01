package com.bestprogramer.bestprogramer.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.bestprogramer.bestprogramer.models.Livre;
import com.bestprogramer.bestprogramer.repositories.LivreRepository;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/bibliotheque")
public class BibliothequeController {

    @Autowired
    private LivreRepository livreRepository;

    @GetMapping
    public String index(Model model, @RequestParam(required = false) String origine) {
        List<Livre> livres;
        
        if (origine != null && !origine.isEmpty()) {
            livres = livreRepository.findByOrigine(origine);
            model.addAttribute("filtreActif", origine);
        } else {
            livres = livreRepository.findAll();
        }
        
        model.addAttribute("livres", livres);
        return "bibliotheque/index";
    }

    @GetMapping("/{id}")
    public String lire(@PathVariable Long id, Model model) {
        Optional<Livre> livreOpt = livreRepository.findById(id);
        
        if (livreOpt.isPresent()) {
            model.addAttribute("livre", livreOpt.get());
            return "bibliotheque/lire";
        } else {
            return "redirect:/bibliotheque";
        }
    }
}
