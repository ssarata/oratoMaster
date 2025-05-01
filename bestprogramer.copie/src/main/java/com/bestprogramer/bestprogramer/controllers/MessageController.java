package com.bestprogramer.bestprogramer.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bestprogramer.bestprogramer.models.Message;
import com.bestprogramer.bestprogramer.models.User;
import com.bestprogramer.bestprogramer.repositories.MessageRepository;
import com.bestprogramer.bestprogramer.services.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/messages")
public class MessageController {

    @Autowired
    private MessageRepository messageRepository;
    
    @Autowired
    private UserService userService;

    @GetMapping
    public String inbox(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Optional<User> userOpt = userService.getUserByEmail(email);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            List<Message> messagesRecus = messageRepository.findByDestinataireOrderByDateEnvoiDesc(user);
            List<Message> messagesEnvoyes = messageRepository.findByExpediteurOrderByDateEnvoiDesc(user);
            
            model.addAttribute("messagesRecus", messagesRecus);
            model.addAttribute("messagesEnvoyes", messagesEnvoyes);
            
            return "messages/inbox";
        } else {
            return "redirect:/login";
        }
    }

    @GetMapping("/nouveau")
    public String nouveauMessage(Model model, @RequestParam(required = false) Long destinataireId) {
        model.addAttribute("message", new Message());
        
        if (destinataireId != null) {
            Optional<User> destinataireOpt = userService.getUserById(destinataireId);
            destinataireOpt.ifPresent(destinataire -> model.addAttribute("destinataire", destinataire));
        }
        
        // Récupérer la liste des formateurs ou apprenants selon le rôle de l'utilisateur
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Optional<User> userOpt = userService.getUserByEmail(email);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            List<User> contacts;
            
            if ("formateur".equals(user.getRole())) {
                contacts = userService.getUsersByRole("apprenant");
            } else {
                contacts = userService.getUsersByRole("formateur");
            }
            
            model.addAttribute("contacts", contacts);
            return "messages/nouveau";
        } else {
            return "redirect:/login";
        }
    }

    @PostMapping("/envoyer")
    public String envoyerMessage(@RequestParam("destinataireId") Long destinataireId,
                               @RequestParam("contenu") String contenu,
                               RedirectAttributes redirectAttributes) {
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Optional<User> expediteurOpt = userService.getUserByEmail(email);
        Optional<User> destinataireOpt = userService.getUserById(destinataireId);
        
        if (expediteurOpt.isPresent() && destinataireOpt.isPresent() && !contenu.trim().isEmpty()) {
            Message message = new Message();
            message.setExpediteur(expediteurOpt.get());
            message.setDestinataire(destinataireOpt.get());
            message.setContenu(contenu);
            message.setDateEnvoi(LocalDateTime.now());
            message.setLu(false);
            
            messageRepository.save(message);
            
            redirectAttributes.addFlashAttribute("success", "Message envoyé avec succès !");
        } else {
            redirectAttributes.addFlashAttribute("error", "Impossible d'envoyer le message.");
        }
        
        return "redirect:/messages";
    }

    @GetMapping("/{id}")
    public String lireMessage(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Message> messageOpt = messageRepository.findById(id);
        
        if (messageOpt.isPresent()) {
            Message message = messageOpt.get();
            
            // Vérifier que l'utilisateur est bien le destinataire ou l'expéditeur
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            Optional<User> userOpt = userService.getUserByEmail(email);
            
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                
                if (message.getDestinataire().getId().equals(user.getId()) || 
                    message.getExpediteur().getId().equals(user.getId())) {
                    
                    // Marquer comme lu si l'utilisateur est le destinataire
                    if (message.getDestinataire().getId().equals(user.getId()) && !message.isLu()) {
                        message.setLu(true);
                        messageRepository.save(message);
                    }
                    
                    model.addAttribute("message", message);
                    return "messages/lire";
                }
            }
        }
        
        redirectAttributes.addFlashAttribute("error", "Message non trouvé ou accès non autorisé.");
        return "redirect:/messages";
    }

    @GetMapping("/supprimer/{id}")
    public String supprimerMessage(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Optional<Message> messageOpt = messageRepository.findById(id);
        
        if (messageOpt.isPresent()) {
            Message message = messageOpt.get();
            
            // Vérifier que l'utilisateur est bien le destinataire ou l'expéditeur
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            Optional<User> userOpt = userService.getUserByEmail(email);
            
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                
                if (message.getDestinataire().getId().equals(user.getId()) || 
                    message.getExpediteur().getId().equals(user.getId())) {
                    
                    messageRepository.delete(message);
                    redirectAttributes.addFlashAttribute("success", "Message supprimé avec succès !");
                    return "redirect:/messages";
                }
            }
        }
        
        redirectAttributes.addFlashAttribute("error", "Message non trouvé ou accès non autorisé.");
        return "redirect:/messages";
    }
}
