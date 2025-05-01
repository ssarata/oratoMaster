package com.bestprogramer.bestprogramer.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.bestprogramer.bestprogramer.models.Certificat;
import com.bestprogramer.bestprogramer.models.Cours;
import com.bestprogramer.bestprogramer.models.Progression;
import com.bestprogramer.bestprogramer.models.User;
import com.bestprogramer.bestprogramer.repositories.CertificatRepository;
import com.bestprogramer.bestprogramer.repositories.CoursRepository;
import com.bestprogramer.bestprogramer.repositories.ProgressionRepository;
import com.bestprogramer.bestprogramer.services.CertificatService;
import com.bestprogramer.bestprogramer.services.UserService;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/certificats")
public class CertificatController {

    @Autowired
    private CertificatRepository certificatRepository;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private ProgressionRepository progressionRepository;
    
    @Autowired
    private CoursRepository coursRepository;
    
    @Autowired
    private CertificatService certificatService;

    @GetMapping
    public String mesCertificats(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Optional<User> userOpt = userService.getUserByEmail(email);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            List<Certificat> certificats = certificatRepository.findByUser(user);
            model.addAttribute("certificats", certificats);
            
            // Vérifier si l'utilisateur est éligible à un certificat
            if (certificats.isEmpty() && "apprenant".equals(user.getRole())) {
                List<Cours> tousLesCours = coursRepository.findAll();
                List<Progression> progressions = progressionRepository.findByUserAndCoursIn(user, tousLesCours);
                
                boolean tousTermines = !tousLesCours.isEmpty() && progressions.size() == tousLesCours.size() &&
                                      progressions.stream().allMatch(Progression::isTermine);
                
                model.addAttribute("eligibleCertificat", tousTermines);
            }
            
            return "certificats/index";
        } else {
            return "redirect:/login";
        }
    }

    @GetMapping("/generer")
    public String genererCertificat(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Optional<User> userOpt = userService.getUserByEmail(email);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            
            // Vérifier si l'utilisateur a déjà un certificat
            if (certificatRepository.existsByUser(user)) {
                return "redirect:/certificats";
            }
            
            // Vérifier si l'utilisateur a terminé tous les cours
            List<Cours> tousLesCours = coursRepository.findAll();
            List<Progression> progressions = progressionRepository.findByUserAndCoursIn(user, tousLesCours);
            
            boolean tousTermines = !tousLesCours.isEmpty() && progressions.size() == tousLesCours.size() &&
                                  progressions.stream().allMatch(Progression::isTermine);
            
            if (tousTermines) {
                Certificat certificat = certificatService.genererCertificat(user);
                return "redirect:/certificats";
            } else {
                return "redirect:/formation";
            }
        } else {
            return "redirect:/login";
        }
    }
}
