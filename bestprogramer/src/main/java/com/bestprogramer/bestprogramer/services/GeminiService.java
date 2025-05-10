package com.bestprogramer.bestprogramer.services;

import com.bestprogramer.bestprogramer.models.CoursModel;
import com.bestprogramer.bestprogramer.models.Question;
import com.bestprogramer.bestprogramer.models.Quiz;
import com.bestprogramer.bestprogramer.models.Reponse;
import com.bestprogramer.bestprogramer.repositories.CoursRepository;
import com.bestprogramer.bestprogramer.repositories.QuizRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private CoursRepository coursRepository;

    @Autowired
    private QuizRepository quizRepository;

    private final RestTemplate restTemplate = new RestTemplate();

    public List<Map<String, Object>> generateCourse(String module) {
        String prompt = "Génère un cours détaillé de : " + module;
        String response = generateContent(prompt);
        System.out.println("Response: " + response);

        if (response == null || response.trim().isEmpty() || response.startsWith("Erreur")) {
            throw new IllegalStateException("Erreur : Le contenu du cours est invalide.\n" + response);
        }

         List<Map<String, Object>> courseContent = new ArrayList<>();
        String[] sections = response.split("\n\n");
        for (String section : sections) {
            Map<String, Object> content = new HashMap<>();
            content.put("text", section.trim());
            courseContent.add(content);
        }

        CoursModel cours = new CoursModel();
        cours.setTitre(module);
        cours.setDescription(String.join("\n\n", sections));
        cours = coursRepository.save(cours);

        generateAndSaveExam(module, cours);

        return courseContent;
    }

    public void generateAndSaveExam(String module, CoursModel cours) {
        String prompt = "Génère un examen avec des questions à choix multiples pour le module suivant : " + module;
        String response = generateContent(prompt);

        if (response == null || response.trim().isEmpty() || response.startsWith("Erreur")) {
            throw new IllegalStateException("Erreur : Aucune question valide n’a été générée.\n" + response);
        }

        Quiz quiz = new Quiz();
        quiz.setCours(cours);

        List<Question> questions = new ArrayList<>();
        double totalPoints = 0;

        String[] questionBlocks = response.split("\n\n");
        for (String block : questionBlocks) {
            String[] lines = block.split("\n");
            if (lines.length < 2) continue;

            Question question = new Question();
            question.setQuestion(lines[0].trim());
            question.setQuiz(quiz);

            List<Reponse> reponses = new ArrayList<>();
            for (int i = 1; i < lines.length; i++) {
                Reponse reponse = new Reponse();
                reponse.setReponse(lines[i].trim());
                reponse.setPoint(i == 1 ? 1.0 : 0.0); // la 1re réponse est correcte
                reponse.setQuestion(question);

                if (i == 1) totalPoints += 1.0;
                reponses.add(reponse);
            }

            question.setReponses(reponses);
            questions.add(question);
        }

        if (questions.isEmpty()) {
            throw new IllegalStateException("Erreur : Aucune question valide n’a été générée.");
        }

        quiz.setQuestions(questions);
        quiz.setNotes(String.valueOf(totalPoints));
        quiz.setMoyenne((totalPoints / questions.size()) * 20.0);

        quizRepository.save(quiz);
    }

    public Map<String, Object> correctExam(Map<Integer, String> answers, List<Map<String, Object>> questions) {
        int score = 0;
        int total = questions.size();
        List<Map<String, String>> feedback = new ArrayList<>();

        for (int i = 0; i < total; i++) {
            Map<String, Object> question = questions.get(i);
            String correct = (String) question.get("correctAnswer");
            String user = answers.get(i);

            Map<String, String> entry = new HashMap<>();
            entry.put("question", (String) question.get("question"));
            entry.put("userAnswer", user);
            entry.put("correctAnswer", correct);

            if (correct.equals(user)) {
                score++;
                entry.put("result", "Correct");
            } else {
                entry.put("result", "Incorrect");
                entry.put("explanation", "La bonne réponse est : " + correct);
            }

            feedback.add(entry);
        }

        Map<String, Object> report = new HashMap<>();
        report.put("score", score);
        report.put("total", total);
        report.put("feedback", feedback);
        return report;
    }

    public String generateContent(String prompt) {
        try {
            Map<String, Object> part = Map.of("text", prompt);
            Map<String, Object> content = Map.of("parts", List.of(part));
            Map<String, Object> body = Map.of("contents", List.of(content));

            HttpHeaders headers = new HttpHeaders();
            System.out.println("API Key: hhhhhhhh");
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(
                    apiUrl + "?key=" + apiKey, request, Map.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> resBody = response.getBody();
                if (resBody != null && resBody.containsKey("candidates")) {
                    System.out.println("111111111111");

                    List<Map<String, Object>> candidates = (List<Map<String, Object>>) resBody.get("candidates");
                    if (!candidates.isEmpty()) {
                        Map<String, Object> contentMap = (Map<String, Object>) candidates.get(0).get("content");
                        List<Map<String, Object>> parts = (List<Map<String, Object>>) contentMap.get("parts");
                        return (String) parts.get(0).get("text");
                    }
                }
                return "Erreur : Réponse de l'API vide ou mal formée.";
            } else if (response.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                return "Erreur 429 : Trop de requêtes. Merci de réessayer plus tard.";
            } else if (response.getStatusCode() == HttpStatus.BAD_REQUEST) {
                return "Erreur : Clé API invalide ou mal formée.";
            } else {
                return "Erreur API : " + response.getStatusCode();
            }

        } catch (Exception e) {
          //  e.printStackTrace();
            return "Erreur lors de l'appel à l'API Gemini : " + e.getMessage();
        }
    }
}
