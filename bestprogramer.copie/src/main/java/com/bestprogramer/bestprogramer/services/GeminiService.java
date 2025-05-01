package com.bestprogramer.bestprogramer.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Génère un cours pour un module donné.
     * @param module Le nom du module.
     * @return Le contenu du cours généré.
     */
    public String generateCourse(String module) {
        String prompt = "Génère un cours détaillé pour le module suivant : " + module;
        return generateContent(prompt);
    }

    /**
     * Génère un examen pour un module donné.
     * @param module Le nom du module.
     * @return Une liste de questions d'examen.
     */
    public List<Map<String, Object>> generateExam(String module) {
        String prompt = "Génère un examen avec des questions à choix multiples pour le module suivant : " + module;
        String response = generateContent(prompt);

        // Exemple de parsing pour transformer la réponse en liste de questions
        // Vous pouvez adapter cela en fonction de la structure de la réponse de l'API Gemini
        List<Map<String, Object>> questions = new ArrayList<>();
        String[] questionParts = response.split("\n\n"); // Supposons que les questions soient séparées par des sauts de ligne

        for (String questionPart : questionParts) {
            Map<String, Object> question = new HashMap<>();
            String[] lines = questionPart.split("\n");
            question.put("question", lines[0]); // La première ligne est la question
            List<String> options = Arrays.asList(Arrays.copyOfRange(lines, 1, lines.length)); // Les lignes suivantes sont les options
            question.put("options", options);
            question.put("correctAnswer", options.get(0)); // Supposons que la première option est correcte (à adapter)
            questions.add(question);
        }

        return questions;
    }

    /**
     * Corrige un examen et attribue une note.
     * @param answers Les réponses fournies par l'utilisateur.
     * @param questions Les questions de l'examen.
     * @return Un rapport contenant la note et les explications pour les mauvaises réponses.
     */
    public Map<String, Object> correctExam(Map<Integer, String> answers, List<Map<String, Object>> questions) {
        int score = 0;
        int total = questions.size();
        List<Map<String, String>> feedback = new ArrayList<>();

        for (int i = 0; i < questions.size(); i++) {
            Map<String, Object> question = questions.get(i);
            String correctAnswer = (String) question.get("correctAnswer");
            String userAnswer = answers.get(i);

            Map<String, String> feedbackEntry = new HashMap<>();
            feedbackEntry.put("question", (String) question.get("question"));
            feedbackEntry.put("userAnswer", userAnswer);
            feedbackEntry.put("correctAnswer", correctAnswer);

            if (correctAnswer.equals(userAnswer)) {
                score++;
                feedbackEntry.put("result", "Correct");
            } else {
                feedbackEntry.put("result", "Incorrect");
                feedbackEntry.put("explanation", "La bonne réponse est : " + correctAnswer);
            }

            feedback.add(feedbackEntry);
        }

        Map<String, Object> report = new HashMap<>();
        report.put("score", score);
        report.put("total", total);
        report.put("feedback", feedback);

        return report;
    }

    
    public String generateContent(String prompt) {
        // ====== 1. Construction du corps de la requête ======
        Map<String, Object> requestBody = new HashMap<>();
        
        // Structure des contenus attendue par l'API
        List<Map<String, Object>> contents = new ArrayList<>();
        Map<String, Object> part = new HashMap<>();
        part.put("text", prompt); // Le prompt utilisateur
        
        Map<String, Object> content = new HashMap<>();
        content.put("parts", List.of(part)); // Les parties du contenu
        contents.add(content); // Ajout au tableau de contenus
        
        requestBody.put("contents", contents); // Finalisation du corps

        // ====== 2. Configuration des en-têtes HTTP ======
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON); // Type de contenu JSON

        // ====== 3. Construction de la requête complète ======
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        // ====== 4. Appel à l'API Gemini ======
        ResponseEntity<Map> response = restTemplate.postForEntity(
                apiUrl + "?key=" + apiKey, // URL avec paramètre d'API key
                request, // Corps + headers
                Map.class // Type de réponse attendue (JSON désérialisé en Map)
        );

        // ====== 5. Traitement de la réponse ======
        if (response.getStatusCode() == HttpStatus.OK) {
            Map<String, Object> body = response.getBody();
            
            // Vérification de la structure de réponse
            if (body != null && body.containsKey("candidates")) {
                List<Map<String, Object>> candidates = (List<Map<String, Object>>) body.get("candidates");
                
                if (!candidates.isEmpty()) {
                    // Extraction du premier candidat
                    Map<String, Object> candidate = candidates.get(0);
                    Map<String, Object> contentMap = (Map<String, Object>) candidate.get("content");
                    
                    // Extraction des parties textuelles
                    List<Map<String, Object>> parts = (List<Map<String, Object>>) contentMap.get("parts");
                    return (String) parts.get(0).get("text"); // Retourne le texte généré
                }
            }
        }
        
        // Retour par défaut en cas d'erreur
        return "Erreur : Aucune réponse valide de l'API Gemini";
    }
}