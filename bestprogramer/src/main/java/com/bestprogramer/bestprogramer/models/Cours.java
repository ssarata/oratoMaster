package com.bestprogramer.bestprogramer.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "cours")
public class Cours {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String titre;
    private double moyenne;
    
    private String pdfPath; 
    
    public String getFilePath() {
        return pdfPath;
    }

    public void setFilePath(String filePath) {
        this.pdfPath = filePath;
    }// Stocke le chemin du fichier PDF
}
