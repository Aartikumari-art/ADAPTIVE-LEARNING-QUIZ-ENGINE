package com.quiz.AdaptiveQuiz.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quiz.AdaptiveQuiz.entity.AIQuestion;
import com.quiz.AdaptiveQuiz.entity.Difficulty;
import com.quiz.AdaptiveQuiz.entity.Subject;

@Service
public class OpenAIService {

    @Value("${openai.api.key}")
    private String openAiKey;

    @Value("${openai.model}")
    private String openAiModel;

    @Value("${gemini.api.key:}")
    private String geminiKey;

    @Value("${gemini.url:}")
    private String geminiUrl;

    private final RestTemplate restTemplate;
    private final com.quiz.AdaptiveQuiz.repository.QuestionRepository questionRepo;
    private final ObjectMapper mapper = new ObjectMapper();

    public OpenAIService(RestTemplate restTemplate, com.quiz.AdaptiveQuiz.repository.QuestionRepository questionRepo) {
        this.restTemplate = restTemplate;
        this.questionRepo = questionRepo;
    }

    public AIQuestion generateQuestion(Subject subject, Difficulty difficulty) {
        // PREFER GEMINI IF KEY IS PRESENT
        if (geminiKey != null && !geminiKey.isBlank()) {
            return generateWithGemini(subject, difficulty);
        }
        return generateWithOpenAI(subject, difficulty);
    }

    private AIQuestion generateWithGemini(Subject subject, Difficulty difficulty) {
        String prompt = createPrompt(subject, difficulty);

        // Gemini JSON Structure
        // { "contents": [{ "parts": [{"text": "prompt..."}] }] }
        Map<String, Object> part = new HashMap<>();
        part.put("text", prompt);

        Map<String, Object> content = new HashMap<>();
        content.put("parts", List.of(part));

        Map<String, Object> body = new HashMap<>();
        body.put("contents", List.of(content));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String finalUrl = geminiUrl + geminiKey;

        for (int i = 0; i < 3; i++) {
            try {
                System.out.println("Attempting generation with Gemini (Attempt " + (i + 1) + ")");
                ResponseEntity<Map> response = restTemplate.postForEntity(finalUrl, new HttpEntity<>(body, headers),
                        Map.class);

                // Parse Gemini Response
                // candidates[0].content.parts[0].text
                Map responseBody = response.getBody();
                if (responseBody == null)
                    throw new RuntimeException("Empty response from Gemini");

                List candidates = (List) responseBody.get("candidates");
                if (candidates == null || candidates.isEmpty())
                    throw new RuntimeException("No candidates from Gemini");

                Map candidate = (Map) candidates.get(0);
                Map contentMap = (Map) candidate.get("content");
                List parts = (List) contentMap.get("parts");
                Map partMap = (Map) parts.get(0);
                String text = partMap.get("text").toString();

                return parseAndSave(text, subject, difficulty);

            } catch (org.springframework.web.client.HttpClientErrorException e) {
                System.err.println("Gemini 4xx Error: " + e.getResponseBodyAsString());
                sleep(2000); // Wait longer on client error (e.g. 429)
            } catch (Exception e) {
                System.err.println("Gemini Error: " + e.getMessage());
                sleep(1000);
            }
        }
        throw new RuntimeException("Failed to generate question with Gemini");
    }

    private AIQuestion generateWithOpenAI(Subject subject, Difficulty difficulty) {
        String prompt = createPrompt(subject, difficulty);

        Map<String, Object> body = new HashMap<>();
        body.put("model", openAiModel);
        body.put("messages", List.of(Map.of("role", "user", "content", prompt)));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(openAiKey);

        for (int i = 0; i < 3; i++) {
            try {
                System.out.println("Attempting generation with OpenAI (Attempt " + (i + 1) + ")");
                ResponseEntity<Map> response = restTemplate.postForEntity(
                        "https://api.openai.com/v1/chat/completions",
                        new HttpEntity<>(body, headers), Map.class);

                Map message = (Map) ((Map) ((List) response.getBody().get("choices")).get(0)).get("message");
                String content = message.get("content").toString();

                return parseAndSave(content, subject, difficulty);

            } catch (Exception e) {
                System.err.println("OpenAI Error: " + e.getMessage());
                sleep(1000);
            }
        }
        throw new RuntimeException("Failed to generate question with OpenAI");
    }

    private String createPrompt(Subject subject, Difficulty difficulty) {
        return """
                Generate one multiple-choice question (MCQ) with 4 options.
                Target Audience: CDAC / PG-DAC Students (Graduate Level CS).
                Subject: %s
                Difficulty: %s

                Requirements:
                1. Question should be conceptual, challenging, and unique.
                2. Avoid common/generic examples (e.g., "What is a class?").
                3. Focus on practical scenarios, edge cases, or deeper internals.
                4. Provide 4 distinct options.
                5. Clearly specify the correct answer.

                Return ONLY valid JSON in this format:
                { "question": "Question text", "options": ["A", "B", "C", "D"], "correctAnswer": "Correct Option Text" }
                Do not wrap in markdown code blocks.
                """
                .formatted(subject.getName(), difficulty);
    }

    private AIQuestion parseAndSave(String jsonContent, Subject subject, Difficulty difficulty) throws Exception {
        // Clean markdown if present
        jsonContent = jsonContent.replace("```json", "").replace("```", "").trim();

        AIQuestion q = mapper.readValue(jsonContent, AIQuestion.class);
        q.setSubject(subject);
        q.setDifficulty(difficulty);

        try {
            com.quiz.AdaptiveQuiz.entity.Question dbQ = new com.quiz.AdaptiveQuiz.entity.Question(
                    q.getQuestion(), q.getOptions(), q.getCorrectAnswer(), subject, difficulty);
            questionRepo.save(dbQ);
            System.out.println("✅ Question Generated & Saved");
        } catch (Exception e) {
            System.err.println("Failed to save question to DB: " + e.getMessage());
        }
        return q;
    }

    private void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ignored) {
        }
    }
}
