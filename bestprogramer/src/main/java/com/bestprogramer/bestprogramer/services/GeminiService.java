package com.bestprogramer.bestprogramer.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;

@Service
public class GeminiService {

    @Value("${gemini.api.key}") // Stocke ta clé API dans application.properties
    private String apiKey;

    private final RestTemplate restTemplate;

    public GeminiService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public String genererCours(String sujet) {
        // Mettez ici l'URL correcte de l'API Gemini
        String apiUrl = "https://api.gemini.com/v1/generate-course";  // Remplacez cette URL par celle de Gemini

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);

        String prompt = "Génère un cours détaillé en art oratoire sur : " + sujet;

        // Format de la requête pour Gemini, ajustez en fonction de la documentation de Gemini
        String requestBody = "{ \"model\": \"gemini-pro\", \"messages\": [{ \"role\": \"user\", \"content\": \"" + prompt + "\" }] }";

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, request, String.class);
            return response.getBody(); // Retourne la réponse de Gemini
        } catch (Exception e) {
            // Gérez l'erreur si la requête échoue
            e.printStackTrace();
            return e.getMessage();
        }
    }
}
