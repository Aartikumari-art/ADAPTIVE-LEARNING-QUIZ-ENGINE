package com.quiz.AdaptiveQuiz.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

import com.quiz.AdaptiveQuiz.dto.SubmitAnswerRequest;
import com.quiz.AdaptiveQuiz.dto.SubmissionResponse;
import com.quiz.AdaptiveQuiz.dto.QuizResultResponse;
import com.quiz.AdaptiveQuiz.entity.*;
import com.quiz.AdaptiveQuiz.service.QuizService;

@RestController
@RequestMapping("/api/quiz")
@CrossOrigin
public class QuizController {

    private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    // ================= START QUIZ =================
    @PostMapping("/start")
    public ResponseEntity<?> startQuiz(
            @RequestParam Long userId,
            @RequestParam String subject) {

        QuizAttempt attempt = quizService.startQuizByUserId(userId, subject);
        AIQuestion q = quizService.getFirstQuestion(subject);

        return ResponseEntity.ok(
                Map.of(
                        "attemptId", attempt.getAttemptId(),
                        "question", q));
    }

    // ================= SUBMIT ANSWER =================
    @PostMapping("/submit")
    public ResponseEntity<SubmissionResponse> submitAnswer(
            @RequestBody SubmitAnswerRequest request) {

        SubmissionResponse response = quizService.submitAnswerByAttempt(
                request.getAttemptId(),
                request.getQuestionText(),
                request.getSelectedAnswer(),
                request.getCorrectAnswer());

        return ResponseEntity.ok(response);
    }

    // ================= NEXT QUESTION =================
    @GetMapping("/next-question/{attemptId}")
    public ResponseEntity<AIQuestion> getNextQuestion(@PathVariable Long attemptId) {
        AIQuestion next = quizService.generateNextQuestion(attemptId);
        if (next == null) {
            // Quiz likely complete or error, client should check result
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(next);
    }

    // ================= RESULT =================
    @GetMapping("/result/{attemptId}")
    public ResponseEntity<QuizResultResponse> getResult(
            @PathVariable Long attemptId) {

        return ResponseEntity.ok(
                quizService.getResult(attemptId));
    }

    @GetMapping("/review/{attemptId}")
    public ResponseEntity<List<UserResponse>> getReview(
            @PathVariable Long attemptId) {

        return ResponseEntity.ok(
                quizService.getReview(attemptId));
    }

    // ================= STATUS (TIMER SYNC) =================
    @GetMapping("/status/{attemptId}")
    public ResponseEntity<?> getQuizStatus(@PathVariable Long attemptId) {
        return ResponseEntity.ok(quizService.getQuizStatus(attemptId));
    }

    // ================= DISCONNECT/TIMEOUT =================
    @PostMapping("/finish/{attemptId}")
    public ResponseEntity<?> finishQuiz(@PathVariable Long attemptId) {
        quizService.finishQuiz(attemptId);
        return ResponseEntity.ok().build();
    }
}
