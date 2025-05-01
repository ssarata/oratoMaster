package com.bestprogramer.bestprogramer.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.bestprogramer.bestprogramer.models.User;
import com.bestprogramer.bestprogramer.models.Soumission;
import com.bestprogramer.bestprogramer.repositories.SoumissionRepository;
import com.bestprogramer.bestprogramer.repositories.UserRepository;
import com.bestprogramer.bestprogramer.repositories.MessageRepository;
import com.bestprogramer.bestprogramer.services.UserService;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/dashboard")
public class FormateurController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private SoumissionRepository soumissionRepository;
    
    @Autowired
    private MessageRepository messageRepository;

    @GetMapping("/formateur")
    public String dashboard(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        
        Optional<User> userOpt = userService.getUserByEmail(email);
        if (userOpt.isPresent()) {
            User formateur = userOpt.get();
            model.addAttribute("user", formateur);
            
            // Vérifier si l'utilisateur est bien un formateur
            if (!"formateur".equals(formateur.getRole())) {
                return "redirect:/dashboard";
            }
            
            // Récupérer les statistiques pour le dashboard
            List<User> apprenants = userRepository.findByRole("apprenant");
            model.addAttribute("nombreApprenants", apprenants.size());
            
            // Récupérer les soumissions à évaluer
            // List<Soumission> soumissionsRecentes = soumissionRepository.findTop5ByOrderByDateCreationDesc();
            // model.addAttribute("soumissionsRecentes", soumissionsRecentes);
            
            // // Récupérer les messages non lus
            // List<User> messagesNonLus = messageRepository.findByDestinataireAndLuOrderByDateEnvoiDesc(formateur, false);
            // model.addAttribute("nombreMessagesNonLus", messagesNonLus.size());
            
            return "dashboard/formateur";
        } else {
            return "redirect:/login";
        }
    }

}
