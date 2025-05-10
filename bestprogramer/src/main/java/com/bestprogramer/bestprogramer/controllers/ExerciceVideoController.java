package com.bestprogramer.bestprogramer.controllers;

import com.bestprogramer.bestprogramer.models.ExerciceVideo;
import com.bestprogramer.bestprogramer.models.User;
import com.bestprogramer.bestprogramer.repositories.ExerciceVideoRepository;
import com.bestprogramer.bestprogramer.services.AIService;
import com.bestprogramer.bestprogramer.services.StorageService;
import com.bestprogramer.bestprogramer.services.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/exercices")
public class ExerciceVideoController {

    @Autowired
    private ExerciceVideoRepository exerciceVideoRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private StorageService storageService;

    @Autowired
    private AIService aiService;

    // Afficher tous les exercices vidéo publics
    @GetMapping
    public String index(Model model, 
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "9") int size,
                        @RequestParam(required = false) String search,
                        @RequestParam(required = false) String categorie,
                        @RequestParam(required = false) String sort) {
        
        Pageable pageable;
        Page<ExerciceVideo> exercicesPage;
        
        // Définir le tri
        if ("vues".equals(sort)) {
            pageable = PageRequest.of(page, size, Sort.by("vues").descending());
        } else if ("notes".equals(sort)) {
            pageable = PageRequest.of(page, size, Sort.by("noteGemini").descending());
        } else if ("recent".equals(sort)) {
            pageable = PageRequest.of(page, size, Sort.by("dateCreation").descending());
        } else {
            pageable = PageRequest.of(page, size, Sort.by("dateCreation").descending());
        }
        
        // Appliquer les filtres
        if (search != null && !search.isEmpty()) {
            exercicesPage = exerciceVideoRepository.findByPubliqueTrueAndTitreContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                    search, search, pageable);
            model.addAttribute("search", search);
        } else if (categorie != null && !categorie.isEmpty()) {
            exercicesPage = exerciceVideoRepository.findByPubliqueTrueAndCategorieIgnoreCase(categorie, pageable);
            model.addAttribute("categorie", categorie);
        } else {
            exercicesPage = exerciceVideoRepository.findByPubliqueTrue(pageable);
        }
        
        model.addAttribute("exercices", exercicesPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", exercicesPage.getTotalPages());
        model.addAttribute("sort", sort);
        
        // Obtenir l'utilisateur connecté
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            Optional<User> userOpt = userService.getUserByEmail(auth.getName());
            userOpt.ifPresent(user -> model.addAttribute("user", user));
        }
        
        return "exercices/index";
    }
    
    // Afficher le formulaire de création d'exercice vidéo
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        // Vérifier si l'utilisateur est connecté
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getName().equals("anonymousUser")) {
            return "redirect:/login";
        }
        
        Optional<User> userOpt = userService.getUserByEmail(auth.getName());
        if (userOpt.isEmpty()) {
            return "redirect:/login";
        }
        
        User user = userOpt.get();
        model.addAttribute("user", user);
        model.addAttribute("exerciceVideo", new ExerciceVideo());
        
        return "exercices/create";
    }
    
    // Traiter la soumission du formulaire de création
    @PostMapping("/create")
    public String createExercice(@ModelAttribute ExerciceVideo exerciceVideo,
                                @RequestParam("videoFile") MultipartFile videoFile,
                                @RequestParam(value = "publique", defaultValue = "false") boolean publique,
                                RedirectAttributes redirectAttributes) {
        
        // Vérifier si l'utilisateur est connecté
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getName().equals("anonymousUser")) {
            return "redirect:/login";
        }
        
        Optional<User> userOpt = userService.getUserByEmail(auth.getName());
        if (userOpt.isEmpty()) {
            return "redirect:/login";
        }
        
        User user = userOpt.get();
        
        // Vérifier si un fichier vidéo a été fourni
        if (videoFile.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Veuillez sélectionner une vidéo à télécharger");
            return "redirect:/exercices/create";
        }
        
        try {
            // Stocker la vidéo
            String filename = storageService.store(videoFile);
            exerciceVideo.setVideoUrl(filename);
            exerciceVideo.setUser(user);
            exerciceVideo.setPublique(publique);
            
            // Analyser la vidéo avec Gemini
            String analyse = aiService.analyserVideo(filename, exerciceVideo.getConsigne());
            Double note = aiService.evaluerVideo(filename, exerciceVideo.getConsigne());
            
            exerciceVideo.setAnalyseGemini(analyse);
            exerciceVideo.setNoteGemini(note);
            
            // Sauvegarder l'exercice
            exerciceVideoRepository.save(exerciceVideo);
            
            redirectAttributes.addFlashAttribute("success", "Votre exercice vidéo a été téléchargé et analysé avec succès");
            return "redirect:/exercices/view/" + exerciceVideo.getId();
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Une erreur s'est produite lors du téléchargement de la vidéo: " + e.getMessage());
            return "redirect:/exercices/create";
        }
    }
    
    // Afficher un exercice vidéo spécifique
    @GetMapping("/view/{id}")
    public String viewExercice(@PathVariable Long id, Model model) {
        Optional<ExerciceVideo> exerciceOpt = exerciceVideoRepository.findById(id);
        
        if (exerciceOpt.isEmpty()) {
            return "redirect:/exercices";
        }
        
        ExerciceVideo exercice = exerciceOpt.get();
        
        // Vérifier si l'exercice est public ou si l'utilisateur est le propriétaire
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isOwner = false;
        
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            Optional<User> userOpt = userService.getUserByEmail(auth.getName());
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                model.addAttribute("user", user);
                isOwner = exercice.getUser().getId().equals(user.getId());
            }
        }
        
        if (!exercice.isPublique() && !isOwner) {
            return "redirect:/exercices";
        }
        
        // Incrémenter le compteur de vues
        exercice.incrementVues();
        exerciceVideoRepository.save(exercice);
        
        model.addAttribute("exercice", exercice);
        model.addAttribute("isOwner", isOwner);
        
        // Récupérer d'autres exercices similaires
        Pageable pageable = PageRequest.of(0, 4, Sort.by("dateCreation").descending());
        Page<ExerciceVideo> similarExercices;
        
        if (exercice.getCategorie() != null && !exercice.getCategorie().isEmpty()) {
            similarExercices = exerciceVideoRepository.findByPubliqueTrueAndCategorieIgnoreCase(
                    exercice.getCategorie(), pageable);
        } else {
            similarExercices = exerciceVideoRepository.findByPubliqueTrue(pageable);
        }
        
        model.addAttribute("similarExercices", similarExercices.getContent());
        
        return "exercices/view";
    }
    
    // Afficher les exercices de l'utilisateur connecté
    @GetMapping("/mes-exercices")
    public String mesExercices(Model model) {
        // Vérifier si l'utilisateur est connecté
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getName().equals("anonymousUser")) {
            return "redirect:/login";
        }
        
        Optional<User> userOpt = userService.getUserByEmail(auth.getName());
        if (userOpt.isEmpty()) {
            return "redirect:/login";
        }
        
        User user = userOpt.get();
        List<ExerciceVideo> exercices = exerciceVideoRepository.findByUser(user);
        
        model.addAttribute("user", user);
        model.addAttribute("exercices", exercices);
        
        return "exercices/mes-exercices";
    }
    
    // Supprimer un exercice
    @PostMapping("/delete/{id}")
    public String deleteExercice(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Optional<ExerciceVideo> exerciceOpt = exerciceVideoRepository.findById(id);
        
        if (exerciceOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Exercice non trouvé");
            return "redirect:/exercices/mes-exercices";
        }
        
        ExerciceVideo exercice = exerciceOpt.get();
        
        // Vérifier si l'utilisateur est le propriétaire
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getName().equals("anonymousUser")) {
            return "redirect:/login";
        }
        
        Optional<User> userOpt = userService.getUserByEmail(auth.getName());
        if (userOpt.isEmpty()) {
            return "redirect:/login";
        }
        
        User user = userOpt.get();
        
        if (!exercice.getUser().getId().equals(user.getId())) {
            redirectAttributes.addFlashAttribute("error", "Vous n'êtes pas autorisé à supprimer cet exercice");
            return "redirect:/exercices/mes-exercices";
        }
        
        // Supprimer l'exercice
        exerciceVideoRepository.delete(exercice);
        redirectAttributes.addFlashAttribute("success", "Exercice supprimé avec succès");
        
        return "redirect:/exercices/mes-exercices";
    }
    
    // Aimer un exercice
    @PostMapping("/like/{id}")
    @ResponseBody
    public String likeExercice(@PathVariable Long id) {
        Optional<ExerciceVideo> exerciceOpt = exerciceVideoRepository.findById(id);
        
        if (exerciceOpt.isEmpty()) {
            return "error";
        }
        
        ExerciceVideo exercice = exerciceOpt.get();
        exercice.incrementLikes();
        exerciceVideoRepository.save(exercice);
        
        return exercice.getLikes().toString();
    }
}
