package com.bestprogramer.bestprogramer.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.bestprogramer.bestprogramer.models.User;
import com.bestprogramer.bestprogramer.services.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import java.util.Optional;

@Controller
public class HomeController {

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String home() {
        return "home/index";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        
        Optional<User> userOpt = userService.getUserByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            model.addAttribute("user", user);
            
            if ("formateur".equals(user.getRole())) {
                return "redirect:/dashboard/formateur";
            } else if ("apprenant".equals(user.getRole())) {
                return "redirect:/dashboard/apprenant";
            } else {
                return "dashboard/index";
            }
        } else {
            return "redirect:/login";
        }
    }
}
