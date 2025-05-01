package com.bestprogramer.bestprogramer.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bestprogramer.bestprogramer.models.User;
import com.bestprogramer.bestprogramer.services.StorageService;
import com.bestprogramer.bestprogramer.services.UserService;

import jakarta.validation.Valid;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private StorageService storageService;

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") User user, 
                              BindingResult result, 
                              @RequestParam(value = "photoFile", required = false) MultipartFile photoFile,
                              @RequestParam(value = "cvFile", required = false) MultipartFile cvFile,
                              RedirectAttributes redirectAttributes) {
        
        if (userService.emailExists(user.getEmail())) {
            result.rejectValue("email", "error.user", "Cet email est déjà utilisé");
        }
        
        if (result.hasErrors()) {
            return "auth/register";
        }
        
        // Traitement de la photo
        if (photoFile != null && !photoFile.isEmpty()) {
            String photoFilename = storageService.store(photoFile);
            user.setPhoto(photoFilename);
        }
        
        // Traitement du CV (uniquement pour les formateurs)
        if ("formateur".equals(user.getRole()) && cvFile != null && !cvFile.isEmpty()) {
            String cvFilename = storageService.store(cvFile);
            user.setCv(cvFilename);
        }

        
        
        userService.saveUser(user);
        
        redirectAttributes.addFlashAttribute("success", "Inscription réussie ! Vous pouvez maintenant vous connecter.");
        return "redirect:/login";
    }
}
