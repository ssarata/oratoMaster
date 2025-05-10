// package com.bestprogramer.bestprogramer.controllers;


// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.security.core.Authentication;
// import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.stereotype.Controller;
// import org.springframework.ui.Model;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.RequestMapping;

// import com.bestprogramer.bestprogramer.models.User;
// import com.bestprogramer.bestprogramer.models.CoursModel;
// import com.bestprogramer.bestprogramer.models.Module;
// import com.bestprogramer.bestprogramer.models.Progression;
// import com.bestprogramer.bestprogramer.models.Soumission;
// import com.bestprogramer.bestprogramer.repositories.CoursRepository;
// import com.bestprogramer.bestprogramer.repositories.ModuleRepository;
// import com.bestprogramer.bestprogramer.repositories.ProgressionRepository;
// import com.bestprogramer.bestprogramer.repositories.SoumissionRepository;
// import com.bestprogramer.bestprogramer.repositories.MessageRepository;
// import com.bestprogramer.bestprogramer.services.UserService;

// import java.util.List;
// import java.util.Optional;

// @Controller
// @RequestMapping("/dashboard")
// public class ApprenantController {

//     @Autowired
//     private UserService userService;
    
//     @Autowired
//     private CoursRepository coursRepository;
    
//     @Autowired
//     private ModuleRepository moduleRepository;
    
//     @Autowired
//     private ProgressionRepository progressionRepository;
    
//     @Autowired
//     private SoumissionRepository soumissionRepository;
    
//     @Autowired
//     private MessageRepository messageRepository;

//     @GetMapping("/apprenant")
//     public String dashboard(Model model) {
//         Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//         String email = auth.getName();
        
//         Optional<User> userOpt = userService.getUserByEmail(email);
//         if (userOpt.isPresent()) {
//             User apprenant = userOpt.get();
//             model.addAttribute("user", apprenant);
            
//             // Vérifier si l'utilisateur est bien un apprenant
//             if (!"apprenant".equals(apprenant.getRole())) {
//                 return "redirect:/dashboard";
//             }
            
//             // Récupérer les statistiques pour le dashboard
//             // List<Cours> tousLesCours = coursRepository.findAll();
//             // List<Progression> progressions = progressionRepository.findByUser(apprenant);
            
//             // int coursTermines = 0;
//             // for (Progression progression : progressions) {
//             //     if (progression.isTermine()) {
//             //         coursTermines++;
//             //     }
//             // }
            
//             // double pourcentageProgression = tousLesCours.isEmpty() ? 0 : (double) coursTermines / tousLesCours.size() * 100;
//             // model.addAttribute("pourcentageProgression", Math.round(pourcentageProgression));
//             // model.addAttribute("coursTermines", coursTermines);
//             // model.addAttribute("totalCours", tousLesCours.size());
            
//             // // Récupérer les modules
//             // List<Module> modules = moduleRepository.findAllByOrderByOrdreAsc();
//             // model.addAttribute("modules", modules);
            
//             // // Récupérer les soumissions récentes
//             // List<Soumission> soumissionsRecentes = soumissionRepository.findByUserOrderByDateCreationDesc(apprenant);
//             // model.addAttribute("soumissionsRecentes", soumissionsRecentes);
            
//             // // Récupérer les messages non lus
//             // List<User> messagesNonLus = messageRepository.findByDestinataireAndLuOrderByDateEnvoiDesc(apprenant, false);
//             // model.addAttribute("nombreMessagesNonLus", messagesNonLus.size());
            
//             return "dashboard/apprenant";
//         } else {
//             return "redirect:/login";
//         }
//     }
// }
