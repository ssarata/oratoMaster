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
    public ResponseEntity<byte[]> generateCoursePdf() throws IOException {
        String module = "art oratoire"; // Définir le module pour lequel générer le cours
        String courseContent = geminiService.generateCourse(module); // Utiliser generateCourse
        
        ByteArrayOutputStream pdfStream = pdfService.generateCoursePdf(courseContent);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(
            ContentDisposition.builder("attachment")
                .filename("cours_art_oratoire.pdf")
                .build());
        
        return new ResponseEntity<>(pdfStream.toByteArray(), headers, HttpStatus.OK);
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

