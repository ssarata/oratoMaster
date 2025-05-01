

package com.bestprogramer.bestprogramer.controllers;

import com.bestprogramer.bestprogramer.services.GeminiService;
import com.bestprogramer.bestprogramer.services.PdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.util.List;
import java.util.Map;

@Controller
public class GeminiController {

    @Autowired
    private GeminiService geminiService;

    @Autowired
    private PdfService pdfService;

    
   
    
    public GeminiController(GeminiService geminiService, PdfService pdfService) {
        this.geminiService = geminiService;
        this.pdfService = pdfService;
    }

    @GetMapping("/listecours")
    public  ResponseEntity<List<Map<String, Object>>> generateCoursePdf() throws IOException {
        String module = "art oratoire"; // Définir le module pour lequel générer le cours
        List<Map<String, Object>>  courseContent = geminiService.generateCourse(module); // Utiliser generateCourse
        
        // ByteArrayOutputStream pdfStream = pdfService.generateCoursePdf(courseContent);
        
        // HttpHeaders headers = new HttpHeaders();
        // headers.setContentType(MediaType.APPLICATION_PDF);
        // headers.setContentDisposition(
        //     ContentDisposition.builder("attachment")
        //         .filename("cours_art_oratoire.pdf")
        //         .build());
        
       // return new ResponseEntity<>(pdfStream.toByteArray(), headers, HttpStatus.OK);
     return new ResponseEntity<>(courseContent, HttpStatus.OK);

    }

    @GetMapping("/examen")
    public ResponseEntity<List<Map<String, Object>>> generateExam() {
        String module = "art oratoire"; // Définir le module pour lequel générer l'examen
        List<Map<String, Object>> examQuestions = geminiService.generateExam(module);
        return new ResponseEntity<>(examQuestions, HttpStatus.OK);
    }

    @PostMapping("/corriger")
    public ResponseEntity<Map<String, Object>> correctExam(@RequestBody Map<Integer, String> answers) {
        String module = "art oratoire"; // Définir le module pour lequel corriger l'examen
        List<Map<String, Object>> examQuestions = geminiService.generateExam(module); // Générer les questions
        Map<String, Object> correctionReport = geminiService.correctExam(answers, examQuestions);
        return new ResponseEntity<>(correctionReport, HttpStatus.OK);
    }
}



// package com.bestprogramer.bestprogramer.controllers;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.security.core.Authentication;
// import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.stereotype.Controller;
// import org.springframework.ui.Model;
// import org.springframework.web.bind.annotation.*;
// import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// import com.bestprogramer.bestprogramer.models.*;
// import com.bestprogramer.bestprogramer.models.Module;
// import com.bestprogramer.bestprogramer.repositories.*;
// import com.bestprogramer.bestprogramer.services.*;

// import java.time.LocalDateTime;
// import java.util.*;

// @Controller
// @RequestMapping("/formation")
// public class GeminiController {

//     @Autowired
//     private ModuleRepository moduleRepository;
    
//     @Autowired
//     private CoursRepository coursRepository;
    
//     @Autowired
//     private ExamenRepository examenRepository;
    
//     @Autowired
//     private UserService userService;
    
//     @Autowired
//     private ProgressionRepository progressionRepository;
    
//     @Autowired
//     private GeminiService geminiService;

//     @GetMapping("/cours-gemini/{id}")
//     public String viewCoursGemini(@PathVariable Long id, Model model) {
//         Optional<Cours> coursOpt = coursRepository.findById(id);
        
//         if (coursOpt.isPresent()) {
//             Cours cours = coursOpt.get();
//             Module module = cours.getModule();
            
//             // Récupérer le contenu du cours généré par Gemini
//             String coursContent = geminiService.generateCourse(module.getTitre() + " - " + cours.getTitre());
            
//             // Générer les questions d'examen
//             List<Map<String, Object>> examQuestions = geminiService.generateExam(module.getTitre() + " - " + cours.getTitre());
            
//             // Récupérer les cours précédent et suivant pour la navigation
//             List<Cours> modulesCours = coursRepository.findByModuleOrderByOrdreAsc(module);
//             Cours previousCours = null;
//             Cours nextCours = null;
            
//             for (int i = 0; i < modulesCours.size(); i++) {
//                 if (modulesCours.get(i).getId().equals(cours.getId())) {
//                     if (i > 0) {
//                         previousCours = modulesCours.get(i - 1);
//                     }
//                     if (i < modulesCours.size() - 1) {
//                         nextCours = modulesCours.get(i + 1);
//                     }
//                     break;
//                 }
//             }
            
//             // Récupérer la progression de l'utilisateur
//             Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//             String email = auth.getName();
//             Optional<User> userOpt = userService.getUserByEmail(email);
            
//             if (userOpt.isPresent()) {
//                 User user = userOpt.get();
//                 Optional<Progression> progressionOpt = progressionRepository.findByUserAndCours(user, cours);
//                 progressionOpt.ifPresent(progression -> model.addAttribute("progression", progression));
//             }
            
//             // Ajouter les attributs au modèle
//             model.addAttribute("cours", cours);
//             model.addAttribute("module", module);
//             model.addAttribute("coursContent", coursContent);
//             model.addAttribute("examQuestions", examQuestions);
//             model.addAttribute("previousCours", previousCours);
//             model.addAttribute("nextCours", nextCours);
            
//             // Exemples de ressources (à remplacer par des données réelles)
//             model.addAttribute("documents", Collections.emptyList());
//             model.addAttribute("videos", Collections.emptyList());
//             model.addAttribute("liens", Collections.emptyList());
            
//             return "formation/cours-gemini";
//         } else {
//             return "redirect:/formation";
//         }
//     }
    
//     @PostMapping("/cours-gemini/{id}/submit-exam")
//     public String submitExam(@PathVariable Long id, 
//                            @RequestParam Map<String, String> formData,
//                            RedirectAttributes redirectAttributes,
//                            Model model) {
        
//         Optional<Cours> coursOpt = coursRepository.findById(id);
        
//         if (coursOpt.isPresent()) {
//             Cours cours = coursOpt.get();
//             Module module = cours.getModule();
            
//             // Récupérer les questions d'examen
//             List<Map<String, Object>> examQuestions = geminiService.generateExam(module.getTitre() + " - " + cours.getTitre());
            
//             // Extraire les réponses de l'utilisateur
//             Map<Integer, String> userAnswers = new HashMap<>();
//             for (String key : formData.keySet()) {
//                 if (key.startsWith("answers[") && key.endsWith("]")) {
//                     int index = Integer.parseInt(key.substring(8, key.length() - 1));
//                     userAnswers.put(index, formData.get(key));
//                 }
//             }
            
//             // Corriger l'examen
//             Map<String, Object> examResult = geminiService.correctExam(userAnswers, examQuestions);
            
//             // Mettre à jour la progression si l'utilisateur a réussi l'examen
//             int score = (int) examResult.get("score");
//             int total = (int) examResult.get("total");
            
//             if (score >= total * 0.7) { // 70% pour réussir
//                 Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//                 String email = auth.getName();
//                 Optional<User> userOpt = userService.getUserByEmail(email);
                
//                 if (userOpt.isPresent()) {
//                     User user = userOpt.get();
//                     Optional<Progression> progressionOpt = progressionRepository.findByUserAndCours(user, cours);
                    
//                     if (progressionOpt.isPresent()) {
//                         Progression progression = progressionOpt.get();
//                         progression.setTermine(true);
//                         progression.setDateCompletion(LocalDateTime.now());
//                         progressionRepository.save(progression);
//                     } else {
//                         Progression progression = new Progression();
//                         progression.setUser(user);
//                         progression.setCours(cours);
//                         progression.setTermine(true);
//                         progression.setDateCompletion(LocalDateTime.now());
//                         progressionRepository.save(progression);
//                     }
//                 }
//             }
            
//             // Préparer le modèle pour l'affichage des résultats
//             String coursContent = geminiService.generateCourse(module.getTitre() + " - " + cours.getTitre());
            
//             // Récupérer les cours précédent et suivant pour la navigation
//             List<Cours> modulesCours = coursRepository.findByModuleOrderByOrdreAsc(module);
//             Cours previousCours = null;
//             Cours nextCours = null;
            
//             for (int i = 0; i < modulesCours.size(); i++) {
//                 if (modulesCours.get(i).getId().equals(cours.getId())) {
//                     if (i > 0) {
//                         previousCours = modulesCours.get(i - 1);
//                     }
//                     if (i < modulesCours.size() - 1) {
//                         nextCours = modulesCours.get(i + 1);
//                     }
//                     break;
//                 }
//             }
            
//             // Récupérer la progression mise à jour
//             Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//             String email = auth.getName();
//             Optional<User> userOpt = userService.getUserByEmail(email);
            
//             if (userOpt.isPresent()) {
//                 User user = userOpt.get();
//                 Optional<Progression> progressionOpt = progressionRepository.findByUserAndCours(user, cours);
//                 progressionOpt.ifPresent(progression -> model.addAttribute("progression", progression));
//             }
            
//             // Ajouter les attributs au modèle
//             model.addAttribute("cours", cours);
//             model.addAttribute("module", module);
//             model.addAttribute("coursContent", coursContent);
//             model.addAttribute("examQuestions", examQuestions);
//             model.addAttribute("examResult", examResult);
//             model.addAttribute("previousCours", previousCours);
//             model.addAttribute("nextCours", nextCours);
            
//             // Exemples de ressources (à remplacer par des données réelles)
//             model.addAttribute("documents", Collections.emptyList());
//             model.addAttribute("videos", Collections.emptyList());
//             model.addAttribute("liens", Collections.emptyList());
            
//             return "formation/cours-gemini";
//         } else {
//             return "redirect:/formation";
//         }
//     }
// }
