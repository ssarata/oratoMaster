package com.bestprogramer.bestprogramer.controllers;

import com.bestprogramer.bestprogramer.models.Livre;
import com.bestprogramer.bestprogramer.repositories.LivreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;


@Controller
@RequestMapping("/livres")
public class LivreController {

    @Autowired
    private LivreRepository livreRepository;

    private final String uploadDir = "/home/sarata/Images/bestPro/oratoMaster/bestprogramer/uploads/pdf/";

    @GetMapping
    public String index(Model model, 
                       @RequestParam(name = "search", required = false) String search,
                       @RequestParam(name = "categorie", required = false) String categorie,
                       @RequestParam(name = "origine", required = false) String origine,
                       @RequestParam(name = "page", defaultValue = "0") int page,
                       @RequestParam(name = "size", defaultValue = "12") int size) {
        
        // Création de la pagination
        Pageable pageable = PageRequest.of(page, size, Sort.by("titre").ascending());
        
        // Recherche des livres selon les critères
        List<Livre> livres;
        
        if (search != null && !search.isEmpty()) {
            // Recherche par titre (contenant la chaîne de recherche)
            livres = livreRepository.findByTitreContainingIgnoreCase(search);
            
            // Filtrage supplémentaire si d'autres critères sont spécifiés
            if (categorie != null && !categorie.isEmpty()) {
                livres = livres.stream()
                    .filter(livre -> livre.getCategorie() != null && livre.getCategorie().equals(categorie))
                    .toList();
            }
            
            if (origine != null && !origine.isEmpty()) {
                livres = livres.stream()
                    .filter(livre -> livre.getOrigine() != null && livre.getOrigine().equals(origine))
                    .toList();
            }
        } else if (categorie != null && !categorie.isEmpty()) {
            // Recherche par catégorie
            livres = livreRepository.findByCategorieIgnoreCase(categorie);
            
            // Filtrage par origine si spécifié
            if (origine != null && !origine.isEmpty()) {
                livres = livres.stream()
                    .filter(livre -> livre.getOrigine() != null && livre.getOrigine().equals(origine))
                    .toList();
            }
        } else if (origine != null && !origine.isEmpty()) {
            // Recherche par origine
            livres = livreRepository.findByOrigineIgnoreCase(origine);
        } else {
            // Aucun critère, récupérer tous les livres
            livres = livreRepository.findAll();
        }
        
        // Pagination manuelle (si nécessaire)
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), livres.size());
        
        // Calcul du nombre total de pages
        int totalPages = (int) Math.ceil((double) livres.size() / size);
        
        // Sous-liste pour la page actuelle
        List<Livre> pageContent = livres.subList(start, end);
        
        model.addAttribute("livres", pageContent);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("searchTerm", search);
        
        return "livres/index";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("livre", new Livre());
        return "livres/create";
    }

   @PostMapping("/create")
    public String createLivre(@ModelAttribute Livre livre, @RequestParam("file") MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            System.out.println("Aucun fichier PDF fourni !");
            return "redirect:/livres?error=fichier";
        }

        String sanitizedFileName = file.getOriginalFilename().replaceAll("[^a-zA-Z0-9\\.\\-]", "_");
        String filePath = uploadDir + sanitizedFileName;
        File dest = new File(filePath);
        dest.getParentFile().mkdirs(); // Crée les répertoires si nécessaire
        file.transferTo(dest);

        livre.setFichierPdf(filePath);

        Livre saved = livreRepository.save(livre);
        System.out.println("Livre sauvegardé : " + saved.getId());

        return "redirect:/livres";
    }


    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Livre livre = livreRepository.findById(id).orElse(null); // Récupère le livre ou retourne null
        if (livre == null) {
            model.addAttribute("errorMessage", "Livre introuvable.");
            return "redirect:/livres"; // Redirige si le livre n'existe pas
        }
        model.addAttribute("livre", livre); // Passe le livre au modèle
        return "livres/edit"; // Correspond au fichier HTML
    }

    @PostMapping("/update/{id}")
    public String updateLivre(@PathVariable Long id, @ModelAttribute Livre livre, @RequestParam("file") MultipartFile file) throws IOException {
        Livre existingLivre = livreRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Livre introuvable"));
        existingLivre.setTitre(livre.getTitre());
        existingLivre.setAuteur(livre.getAuteur());
        existingLivre.setDescription(livre.getDescription());
        existingLivre.setCategorie(livre.getCategorie());
        existingLivre.setOrigine(livre.getOrigine());

        if (!file.isEmpty()) {
            String sanitizedFileName = file.getOriginalFilename().replaceAll("[^a-zA-Z0-9\\.\\-]", "_");
            String filePath = uploadDir + sanitizedFileName;
            File dest = new File(filePath);
            dest.getParentFile().mkdirs();
            file.transferTo(dest);
            existingLivre.setFichierPdf(filePath); // Met à jour le chemin du fichier
        }

        livreRepository.save(existingLivre);
        return "redirect:/livres";
    }

    @GetMapping("/delete/{id}")
    public String deleteLivre(@PathVariable Long id) {
        livreRepository.deleteById(id);
        return "redirect:/livres";
    }

  
    @GetMapping("/download/{id}")
public ResponseEntity<byte[]> downloadPdf(@PathVariable Long id) throws IOException {
    Livre livre = livreRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Livre introuvable"));

    File file = new File(livre.getFichierPdf());
    byte[] content = java.nio.file.Files.readAllBytes(file.toPath());

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_PDF);
    headers.setContentDisposition(ContentDisposition
        .builder("attachment")
        .filename(file.getName())
        .build());

    return new ResponseEntity<>(content, headers, HttpStatus.OK);
}

    
    @GetMapping("/view/{id}")
    public String viewPdf(@PathVariable Long id, Model model) {
        Livre livre = livreRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Livre introuvable"));
        model.addAttribute("livre", livre);
        return "livres/view";
    }
}
