package com.bestprogramer.bestprogramer.controllers;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bestprogramer.bestprogramer.models.Formation;
import com.bestprogramer.bestprogramer.models.Inscription;
import com.bestprogramer.bestprogramer.models.User;
import com.bestprogramer.bestprogramer.services.FormationService;
import com.bestprogramer.bestprogramer.services.InscriptionService;
import com.bestprogramer.bestprogramer.services.UserService;

@Controller
@RequestMapping("/inscriptions")
public class InscriptionController {

    @Autowired
    private InscriptionService inscriptionService;
    
    @Autowired
    private FormationService formationService;
    
    @Autowired
    private UserService userService;

    @GetMapping
    public String listInscriptions(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Optional<User> userOpt = userService.getUserByEmail(email);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            model.addAttribute("user", user);
            
            if ("apprenant".equals(user.getRole())) {
                model.addAttribute("inscriptions", inscriptionService.getInscriptionsByApprenant(user));
            } else if ("formateur".equals(user.getRole())) {
                // Pour un formateur, afficher les inscriptions à ses formations
                model.addAttribute("formations", formationService.getFormationsByFormateur(user));
            } else if ("admin".equals(user.getRole())) {
                // Pour un admin, afficher toutes les inscriptions
                model.addAttribute("inscriptions", inscriptionService.getAllInscriptions());
            }
        }
        
        return "inscriptions/index";
    }

    @GetMapping("/formation/{id}")
    public String showInscriptionForm(@PathVariable Long id, Model model) {
        Optional<Formation> formationOpt = formationService.getFormationById(id);
        
        if (formationOpt.isPresent()) {
            Formation formation = formationOpt.get();
            model.addAttribute("formation", formation);
            
            // Vérifier si l'utilisateur est déjà inscrit
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            Optional<User> userOpt = userService.getUserByEmail(email);
            
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                if ("apprenant".equals(user.getRole())) {
                    boolean dejaInscrit = inscriptionService.isDejaInscrit(user, formation);
                    if (dejaInscrit) {
                        return "redirect:/formations/" + id + "?dejaInscrit=true";
                    }
                    
                    return "inscriptions/create";
                }
            }
        }
        
        return "redirect:/formations";
    }

    @PostMapping("/formation/{id}")
    public String processInscription(@PathVariable Long id, 
                                    @RequestParam(required = false) String telephone,
                                    RedirectAttributes redirectAttributes) {
        Optional<Formation> formationOpt = formationService.getFormationById(id);
        
        if (formationOpt.isPresent()) {
            Formation formation = formationOpt.get();
            
            // Récupérer l'utilisateur connecté
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            Optional<User> userOpt = userService.getUserByEmail(email);
            
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                if ("apprenant".equals(user.getRole())) {
                    // Vérifier si l'utilisateur est déjà inscrit
                    boolean dejaInscrit = inscriptionService.isDejaInscrit(user, formation);
                    if (dejaInscrit) {
                        redirectAttributes.addFlashAttribute("error", "Vous êtes déjà inscrit à cette formation.");
                        return "redirect:/formations/" + id;
                    }
                    
                    // Créer l'inscription
                    Inscription inscription = new Inscription();
                    inscription.setApprenant(user);
                    inscription.setFormation(formation);
                    inscription.setDateInscription(LocalDate.now());
                    inscription.setStatut("En attente");
                    
                    Inscription savedInscription = inscriptionService.saveInscription(inscription);
                    
                    // Rediriger vers le paiement
                    return "redirect:/paiements/initier/" + savedInscription.getId() + "?telephone=" + telephone;
                }
            }
        }
        
        return "redirect:/formations";
    }

    @GetMapping("/annuler/{id}")
    public String annulerInscription(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Optional<Inscription> inscriptionOpt = inscriptionService.getInscriptionById(id);
        
        if (inscriptionOpt.isPresent()) {
            Inscription inscription = inscriptionOpt.get();
            
            // Vérifier si l'utilisateur est l'apprenant de l'inscription
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            Optional<User> userOpt = userService.getUserByEmail(email);
            
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                if (inscription.getApprenant().getId().equals(user.getId()) || "admin".equals(user.getRole())) {
                    inscription.setStatut("Annulé");
                    inscriptionService.saveInscription(inscription);
                    redirectAttributes.addFlashAttribute("success", "Inscription annulée avec succès !");
                }
            }
        }
        
        return "redirect:/inscriptions";
    }
}
