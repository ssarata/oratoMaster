package com.bestprogramer.bestprogramer.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bestprogramer.bestprogramer.models.*;
import com.bestprogramer.bestprogramer.models.Module;
import com.bestprogramer.bestprogramer.repositories.*;
import com.bestprogramer.bestprogramer.services.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/formation")
public class FormationController {

    @Autowired
    private ModuleRepository moduleRepository;
    
    @Autowired
    private CoursRepository coursRepository;
    
    @Autowired
    private ExamenRepository examenRepository;
    
    @Autowired
    private SoumissionRepository soumissionRepository;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private StorageService storageService;
    
    @Autowired
    private ProgressionRepository progressionRepository;
    
    @Autowired
    private AIService aiService;

    @GetMapping
    public String index(Model model) {
        List<Module> modules = moduleRepository.findAllByOrderByOrdreAsc();
        model.addAttribute("modules", modules);
        return "formation/index";
    }


    @GetMapping("/module/{id}")
    public String viewModule(@PathVariable Long id, Model model) {
        Optional<Module> moduleOpt = moduleRepository.findById(id);
        
        if (moduleOpt.isPresent()) {
            Module module = moduleOpt.get();
            List<Cours> cours = coursRepository.findByModuleOrderByOrdreAsc(module);
            
            model.addAttribute("module", module);
            model.addAttribute("cours", cours);
            
            // Récupérer la progression de l'utilisateur
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            Optional<User> userOpt = userService.getUserByEmail(email);
            
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                List<Progression> progressions = progressionRepository.findByUserAndCoursIn(user, cours);
                model.addAttribute("progressions", progressions);
            }
            
            return "formation/module";
        } else {
            return "redirect:/formation";
        }
    }

    @GetMapping("/cours/{id}")
    public String viewCours(@PathVariable Long id, Model model) {
        Optional<Cours> coursOpt = coursRepository.findById(id);
        
        if (coursOpt.isPresent()) {
            Cours cours = coursOpt.get();
            List<Examen> examens = examenRepository.findByCours(cours);
            
            model.addAttribute("cours", cours);
            model.addAttribute("examens", examens);
            
            // Marquer le cours comme vu
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            Optional<User> userOpt = userService.getUserByEmail(email);
            
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                Optional<Progression> progressionOpt = progressionRepository.findByUserAndCours(user, cours);
                
                if (progressionOpt.isEmpty()) {
                    Progression progression = new Progression();
                    progression.setUser(user);
                    progression.setCours(cours);
                    progression.setTermine(false);
                    progressionRepository.save(progression);
                }
                
                // Récupérer les soumissions de l'utilisateur pour ce cours
                List<Soumission> soumissions = soumissionRepository.findByUserAndExamenIn(user, examens);
                model.addAttribute("soumissions", soumissions);
            }
            
            return "formation/cours";
        } else {
            return "redirect:/formation";
        }
    }

    @GetMapping("/examen/{id}")
    public String viewExamen(@PathVariable Long id, Model model) {
        Optional<Examen> examenOpt = examenRepository.findById(id);
        
        if (examenOpt.isPresent()) {
            Examen examen = examenOpt.get();
            model.addAttribute("examen", examen);
            
            // Vérifier si l'utilisateur a déjà soumis cet examen
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            Optional<User> userOpt = userService.getUserByEmail(email);
            
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                Optional<Soumission> soumissionOpt = soumissionRepository.findByUserAndExamen(user, examen);
                soumissionOpt.ifPresent(soumission -> model.addAttribute("soumission", soumission));
            }
            
            return "formation/examen";
        } else {
            return "redirect:/formation";
        }
    }

    @PostMapping("/examen/{id}/soumettre")
    public String soumettreExamen(@PathVariable Long id, 
                                 @RequestParam("videoFile") MultipartFile videoFile,
                                 RedirectAttributes redirectAttributes) {
        
        Optional<Examen> examenOpt = examenRepository.findById(id);
        
        if (examenOpt.isPresent() && !videoFile.isEmpty()) {
            Examen examen = examenOpt.get();
            
            // Récupérer l'utilisateur actuel
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            Optional<User> userOpt = userService.getUserByEmail(email);
            
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                
                // Stocker la vidéo
                String videoFilename = storageService.store(videoFile);
                
                // Créer la soumission
                Soumission soumission = new Soumission();
                soumission.setUser(user);
                soumission.setExamen(examen);
                soumission.setVideoUrl(videoFilename);
                soumission.setDateCreation(LocalDateTime.now());
                
                // Analyser la vidéo avec l'IA
                String feedback = aiService.analyserVideo(videoFilename, examen.getConsigne());
                Double note = aiService.evaluerVideo(videoFilename, examen.getConsigne());
                
                soumission.setFeedback(feedback);
                soumission.setNote(note);
                
                soumissionRepository.save(soumission);
                
                // Marquer le cours comme terminé
                Optional<Progression> progressionOpt = progressionRepository.findByUserAndCours(user, examen.getCours());
                if (progressionOpt.isPresent()) {
                    Progression progression = progressionOpt.get();
                    progression.setTermine(true);
                    progression.setDateCompletion(LocalDateTime.now());
                    progressionRepository.save(progression);
                }
                
                redirectAttributes.addFlashAttribute("success", "Votre examen a été soumis avec succès !");
                return "redirect:/formation/cours/" + examen.getCours().getId();
            }
        }
        
        redirectAttributes.addFlashAttribute("error", "Une erreur s'est produite lors de la soumission de l'examen.");
        return "redirect:/formation/examen/" + id;
    }
}
