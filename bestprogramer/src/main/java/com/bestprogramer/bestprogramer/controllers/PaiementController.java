package com.bestprogramer.bestprogramer.controllers;

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
import com.bestprogramer.bestprogramer.models.Paiement;
import com.bestprogramer.bestprogramer.models.User;
import com.bestprogramer.bestprogramer.services.InscriptionService;
import com.bestprogramer.bestprogramer.services.PaiementApiService;
import com.bestprogramer.bestprogramer.services.PaiementService;
import com.bestprogramer.bestprogramer.services.UserService;

@Controller
@RequestMapping("/paiements")
public class PaiementController {

    @Autowired
    private PaiementService paiementService;
    
    @Autowired
    private InscriptionService inscriptionService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private PaiementApiService paiementApiService;

    @GetMapping("/initier/{inscriptionId}")
    public String initierPaiement(@PathVariable Long inscriptionId, 
                                 @RequestParam(required = false) String telephone,
                                 Model model) {
        Optional<Inscription> inscriptionOpt = inscriptionService.getInscriptionById(inscriptionId);
        
        if (inscriptionOpt.isPresent()) {
            Inscription inscription = inscriptionOpt.get();
            Formation formation = inscription.getFormation();
            
            // Vérifier si l'utilisateur est l'apprenant de l'inscription
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            Optional<User> userOpt = userService.getUserByEmail(email);
            
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                if (inscription.getApprenant().getId().equals(user.getId())) {
                    model.addAttribute("inscription", inscription);
                    model.addAttribute("formation", formation);
                    model.addAttribute("telephone", telephone);
                    return "paiements/initier";
                }
            }
        }
        
        return "redirect:/formations";
    }

    @PostMapping("/initier/{inscriptionId}")
    public String processPaiement(@PathVariable Long inscriptionId, 
                                 @RequestParam String methode,
                                 @RequestParam String telephone,
                                 RedirectAttributes redirectAttributes) {
        Optional<Inscription> inscriptionOpt = inscriptionService.getInscriptionById(inscriptionId);
        
        if (inscriptionOpt.isPresent()) {
            Inscription inscription = inscriptionOpt.get();
            Formation formation = inscription.getFormation();
            
            // Vérifier si l'utilisateur est l'apprenant de l'inscription
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            Optional<User> userOpt = userService.getUserByEmail(email);
            
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                if (inscription.getApprenant().getId().equals(user.getId())) {
                    // Initier le paiement
                    Paiement paiement = paiementService.initierPaiement(inscription, formation.getPrix().doubleValue(), methode);
                    
                    // Rediriger vers l'API de paiement (simulée)
                    String redirectUrl = paiementApiService.initialiserPaiementApi(paiement, telephone);
                    return "redirect:" + redirectUrl;
                }
            }
        }
        
        return "redirect:/formations";
    }

    @GetMapping("/simuler/{paiementId}")
    public String simulerPaiement(@PathVariable Long paiementId, 
                                 @RequestParam(required = false) String telephone,
                                 Model model) {
        Optional<Paiement> paiementOpt = paiementService.getPaiementById(paiementId);
        
        if (paiementOpt.isPresent()) {
            Paiement paiement = paiementOpt.get();
            Inscription inscription = paiement.getInscription();
            Formation formation = inscription.getFormation();
            
            model.addAttribute("paiement", paiement);
            model.addAttribute("inscription", inscription);
            model.addAttribute("formation", formation);
            model.addAttribute("telephone", telephone);
            
            return "paiements/simuler";
        }
        
        return "redirect:/formations";
    }

    @PostMapping("/confirmer/{paiementId}")
    public String confirmerPaiement(@PathVariable Long paiementId, RedirectAttributes redirectAttributes) {
        Paiement paiement = paiementService.confirmerPaiement(paiementId);
        
        if (paiement != null) {
            redirectAttributes.addFlashAttribute("success", "Paiement confirmé avec succès ! Votre inscription est maintenant validée.");
            return "redirect:/inscriptions";
        }
        
        redirectAttributes.addFlashAttribute("error", "Erreur lors de la confirmation du paiement.");
        return "redirect:/formations";
    }

    @PostMapping("/annuler/{paiementId}")
    public String annulerPaiement(@PathVariable Long paiementId, RedirectAttributes redirectAttributes) {
        Paiement paiement = paiementService.annulerPaiement(paiementId);
        
        if (paiement != null) {
            redirectAttributes.addFlashAttribute("error", "Paiement annulé.");
            return "redirect:/formations";
        }
        
        redirectAttributes.addFlashAttribute("error", "Erreur lors de l'annulation du paiement.");
        return "redirect:/formations";
    }
}
