package com.bestprogramer.bestprogramer.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bestprogramer.bestprogramer.models.Formation;
import com.bestprogramer.bestprogramer.models.Inscription;
import com.bestprogramer.bestprogramer.models.User;
import com.bestprogramer.bestprogramer.services.FormationService;
import com.bestprogramer.bestprogramer.services.InscriptionService;
import com.bestprogramer.bestprogramer.services.UserService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/formations")
public class FormationController {

    @Autowired
    private FormationService formationService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private InscriptionService inscriptionService;

    @GetMapping
    public String listFormations(Model model) {
        List<Formation> formations = formationService.getAllFormations();
        model.addAttribute("formations", formations);
        
        // Si l'utilisateur est connecté, ajouter ses informations
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && !auth.getName().equals("anonymousUser")) {
            String email = auth.getName();
            Optional<User> userOpt = userService.getUserByEmail(email);
        
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                model.addAttribute("user", user);
            
                // Si l'utilisateur est un apprenant, vérifier ses inscriptions
                if ("apprenant".equals(user.getRole())) {
                    List<Inscription> inscriptions = inscriptionService.getInscriptionsByApprenant(user);
                    model.addAttribute("inscriptions", inscriptions);
                }
            }
        }
    
        return "formations/index";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        // Vérifier si l'utilisateur est formateur ou admin
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Optional<User> userOpt = userService.getUserByEmail(email);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if ("formateur".equals(user.getRole()) || "admin".equals(user.getRole())) {
                model.addAttribute("formation", new Formation());
                return "formations/create";
            }
        }
        
        return "redirect:/formations";
    }

    @PostMapping("/create")
    public String createFormation(@Valid @ModelAttribute("formation") Formation formation, 
                                 BindingResult result, 
                                 RedirectAttributes redirectAttributes) {
        // Validation personnalisée
        if (formation.getDateFin() != null && formation.getDateDebut() != null && 
            formation.getDateFin().isBefore(formation.getDateDebut())) {
            result.rejectValue("dateFin", "error.formation", "La date de fin doit être postérieure à la date de début");
        }
        
        if (result.hasErrors()) {
            return "formations/create";
        }
        
        // Définir le formateur
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Optional<User> userOpt = userService.getUserByEmail(email);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            formation.setFormateur(user);
            
            formationService.saveFormation(formation);
            redirectAttributes.addFlashAttribute("success", "Formation créée avec succès !");
        }
        
        return "redirect:/formations";
    }

    @GetMapping("/{id}")
    public String viewFormation(@PathVariable Long id, Model model) {
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
                model.addAttribute("user", user);
                
                if ("apprenant".equals(user.getRole())) {
                    boolean dejaInscrit = inscriptionService.isDejaInscrit(user, formation);
                    model.addAttribute("dejaInscrit", dejaInscrit);
                }
            }
            
            return "formations/view";
        }
        
        return "redirect:/formations";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        // Vérifier si l'utilisateur est formateur ou admin
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Optional<User> userOpt = userService.getUserByEmail(email);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if ("formateur".equals(user.getRole()) || "admin".equals(user.getRole())) {
                Optional<Formation> formationOpt = formationService.getFormationById(id);
                
                if (formationOpt.isPresent()) {
                    Formation formation = formationOpt.get();
                    
                    // Vérifier si c'est le formateur de la formation ou un admin
                    if ("admin".equals(user.getRole()) || formation.getFormateur().getId().equals(user.getId())) {
                        model.addAttribute("formation", formation);
                        return "formations/edit";
                    }
                }
            }
        }
        
        return "redirect:/formations";
    }

    @PostMapping("/edit/{id}")
    public String updateFormation(@PathVariable Long id, 
                                 @Valid @ModelAttribute("formation") Formation formation, 
                                 BindingResult result, 
                                 RedirectAttributes redirectAttributes) {
        // Validation personnalisée
        if (formation.getDateFin() != null && formation.getDateDebut() != null && 
            formation.getDateFin().isBefore(formation.getDateDebut())) {
            result.rejectValue("dateFin", "error.formation", "La date de fin doit être postérieure à la date de début");
        }
        
        if (result.hasErrors()) {
            return "formations/edit";
        }
        
        Optional<Formation> existingFormationOpt = formationService.getFormationById(id);
        
        if (existingFormationOpt.isPresent()) {
            Formation existingFormation = existingFormationOpt.get();
            
            // Vérifier si l'utilisateur est le formateur de la formation ou un admin
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            Optional<User> userOpt = userService.getUserByEmail(email);
            
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                if ("admin".equals(user.getRole()) || existingFormation.getFormateur().getId().equals(user.getId())) {
                    // Mettre à jour les champs
                    existingFormation.setTitre(formation.getTitre());
                    existingFormation.setDescription(formation.getDescription());
                    existingFormation.setLieu(formation.getLieu());
                    existingFormation.setDateDebut(formation.getDateDebut());
                    existingFormation.setDateFin(formation.getDateFin());
                    existingFormation.setPrix(formation.getPrix());
                    
                    formationService.saveFormation(existingFormation);
                    redirectAttributes.addFlashAttribute("success", "Formation mise à jour avec succès !");
                }
            }
        }
        
        return "redirect:/formations";
    }

    @GetMapping("/delete/{id}")
    public String deleteFormation(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Optional<Formation> formationOpt = formationService.getFormationById(id);
        
        if (formationOpt.isPresent()) {
            Formation formation = formationOpt.get();
            
            // Vérifier si l'utilisateur est le formateur de la formation ou un admin
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            Optional<User> userOpt = userService.getUserByEmail(email);
            
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                if ("admin".equals(user.getRole()) || formation.getFormateur().getId().equals(user.getId())) {
                    formationService.deleteFormation(id);
                    redirectAttributes.addFlashAttribute("success", "Formation supprimée avec succès !");
                }
            }
        }
        
        return "redirect:/formations";
    }
}
