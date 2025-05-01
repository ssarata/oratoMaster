package com.bestprogramer.bestprogramer.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bestprogramer.bestprogramer.models.User;
import com.bestprogramer.bestprogramer.services.StorageService;
import com.bestprogramer.bestprogramer.services.UserService;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private StorageService storageService;


    @GetMapping("/listeFormateurs")
    public String listTrainers(Model model) {
        List<User> formateurs = userService.getUsersByRole("formateur");
        model.addAttribute("formateurs", formateurs);
        return "users/formateurs";
    }
    @GetMapping
    public String listUsers(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "users/list";
    }


    @GetMapping("/listeApprenants")
    public String listStudents(Model model) {
        List<User> apprenants = userService.getUsersByRole("apprenant");
        model.addAttribute("apprenants", apprenants);
        return "users/apprenants";
    }

    @GetMapping("/profile")
    public String viewProfile(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        
        Optional<User> userOpt = userService.getUserByEmail(email);
        if (userOpt.isPresent()) {
            model.addAttribute("user", userOpt.get());
            return "users/profile";
        } else {
            return "redirect:/login";
        }
    }

    @GetMapping("/edit")
    public String showEditForm(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        
        Optional<User> userOpt = userService.getUserByEmail(email);
        if (userOpt.isPresent()) {
            model.addAttribute("user", userOpt.get());
            return "users/edit";
        } else {
            return "redirect:/login";
        }
    }

    @PostMapping("/edit")
    public String updateUser(@Valid @ModelAttribute("user") User user, 
                            BindingResult result,
                            @RequestParam(value = "photoFile", required = false) MultipartFile photoFile,
                            @RequestParam(value = "cvFile", required = false) MultipartFile cvFile,
                            RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            return "users/edit";
        }
        
        // Récupérer l'utilisateur actuel
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Optional<User> currentUserOpt = userService.getUserByEmail(email);
        
        if (currentUserOpt.isPresent()) {
            User currentUser = currentUserOpt.get();
            
            // Mettre à jour les champs modifiables
            currentUser.setNom(user.getNom());
            currentUser.setPrenom(user.getPrenom());
            currentUser.setSexe(user.getSexe());
            currentUser.setContact(user.getContact());
            
            // Traitement de la photo
            if (photoFile != null && !photoFile.isEmpty()) {
                String photoFilename = storageService.store(photoFile);
                currentUser.setPhoto(photoFilename);
            }
            
            // Traitement du CV (uniquement pour les formateurs)
            if ("formateur".equals(currentUser.getRole()) && cvFile != null && !cvFile.isEmpty()) {
                String cvFilename = storageService.store(cvFile);
                currentUser.setCv(cvFilename);
            }
            
            userService.saveUser(currentUser);
            
            redirectAttributes.addFlashAttribute("success", "Profil mis à jour avec succès !");
            return "redirect:/users/profile";
        } else {
            return "redirect:/login";
        }
    }

    @GetMapping("/{id}")
    public String viewUser(@PathVariable Long id, Model model) {
        Optional<User> userOpt = userService.getUserById(id);
        if (userOpt.isPresent()) {
            model.addAttribute("user", userOpt.get());
            return "users/view";
        } else {
            return "redirect:/users";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        userService.deleteUser(id);
        redirectAttributes.addFlashAttribute("success", "Utilisateur supprimé avec succès !");
        return "redirect:/users";
    }
}
