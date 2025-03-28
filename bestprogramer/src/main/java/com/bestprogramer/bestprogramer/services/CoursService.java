package com.bestprogramer.bestprogramer.services;

import com.bestprogramer.bestprogramer.models.Cours;
import com.bestprogramer.bestprogramer.repositories.CourRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Service
public class CoursService {
    private final CourRepository coursRepository;
    private final Path rootLocation = Paths.get("uploads");

    public CoursService(CourRepository coursRepository) {
        this.coursRepository = coursRepository;
    }

    public List<Cours> getAllCours() {
        return coursRepository.findAll();
    }

    public Optional<Cours> getCoursById(Long id) {
        return coursRepository.findById(id);
    }

    public Cours saveCours(Cours cours, MultipartFile file) throws IOException {
        if (!file.isEmpty()) {
            Files.copy(file.getInputStream(), this.rootLocation.resolve(file.getOriginalFilename()));
            cours.setFilePath(file.getOriginalFilename());
        }
        return coursRepository.save(cours);
    }

    public void deleteCours(Long id) {
        coursRepository.deleteById(id);
    }

    
}
