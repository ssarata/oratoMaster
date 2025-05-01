package com.bestprogramer.bestprogramer.services;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.bestprogramer.bestprogramer.config.StorageProperties;
import com.bestprogramer.bestprogramer.exceptions.StorageException;
import com.bestprogramer.bestprogramer.exceptions.StorageFileNotFoundException;

@Service
public class StorageService {

    private final Path rootLocation;

    @Autowired
    public StorageService(StorageProperties properties) {
        this.rootLocation = Paths.get(properties.getLocation());
        init();
    }

    public void init() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new StorageException("Impossible de créer le répertoire de stockage", e);
        }
    }

    public String store(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new StorageException("Impossible de stocker un fichier vide");
            }
            
            String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
            String extension = "";
            
            int lastIndex = originalFilename.lastIndexOf('.');
            if (lastIndex > 0) {
                extension = originalFilename.substring(lastIndex);
            }
            
            String filename = UUID.randomUUID().toString() + extension;
            
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, this.rootLocation.resolve(filename),
                    StandardCopyOption.REPLACE_EXISTING);
                return filename;
            }
        } catch (IOException e) {
            throw new StorageException("Échec du stockage du fichier", e);
        }
    }

    public Path load(String filename) {
        return rootLocation.resolve(filename);
    }

    public Resource loadAsResource(String filename) {
        try {
            Path file = load(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new StorageFileNotFoundException("Impossible de lire le fichier: " + filename);
            }
        } catch (MalformedURLException e) {
            throw new StorageFileNotFoundException("Impossible de lire le fichier: " + filename, e);
        }
    }
}
