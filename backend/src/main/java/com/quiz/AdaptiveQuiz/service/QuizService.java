package com.quiz.AdaptiveQuiz.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.quiz.AdaptiveQuiz.dto.QuizResultResponse;
import com.quiz.AdaptiveQuiz.entity.*;
import com.quiz.AdaptiveQuiz.repository.*;
import com.quiz.AdaptiveQuiz.repository.SubjectRepository;

@Service
public class QuizService {

        private final QuizAttemptRepository attemptRepo;
        private final UserResponseRepository responseRepo;
        private final UserRepository userRepo;
        private final OpenAIService openAIService;
        private final com.quiz.AdaptiveQuiz.repository.SkillSnapshotRepository skillRepo;
        private final SubjectRepository subjectRepo;
        private final com.quiz.AdaptiveQuiz.repository.QuestionRepository questionRepo; // Ensure this is available
        private final AdaptiveLogicService adaptiveLogicService;

        public QuizService(
                        QuizAttemptRepository attemptRepo,
                        UserResponseRepository responseRepo,
                        UserRepository userRepo,
                        OpenAIService openAIService,
                        com.quiz.AdaptiveQuiz.repository.SkillSnapshotRepository skillRepo,
                        SubjectRepository subjectRepo,
                        com.quiz.AdaptiveQuiz.repository.QuestionRepository questionRepo,
                        AdaptiveLogicService adaptiveLogicService) {

                this.attemptRepo = attemptRepo;
                this.responseRepo = responseRepo;
                this.userRepo = userRepo;
                this.openAIService = openAIService;
                this.skillRepo = skillRepo;
                this.subjectRepo = subjectRepo;
                this.questionRepo = questionRepo;
                this.adaptiveLogicService = adaptiveLogicService;
        }

        // ================= START QUIZ =================
        public QuizAttempt startQuizByUserId(Long userId, String subjectName) {

                User user = userRepo.findById(userId)
                                .orElseThrow(() -> new RuntimeException("User not found"));

                Subject subject = subjectRepo.findByName(subjectName)
                                .orElseThrow(() -> new RuntimeException("Subject not found: " + subjectName));

                QuizAttempt attempt = new QuizAttempt();
                attempt.setUser(user);
                attempt.setSubject(subject);

                attempt.setTotalQuestions(15);
                attempt.setCorrectAnswers(0);
                attempt.setWrongAnswers(0);
                attempt.setSkippedAnswers(0);
                attempt.setAccuracy(0);
                attempt.setStartTime(java.time.LocalDateTime.now());

                // ✅ THIS IS THE MISSING LINE
                attempt.setCurrentDifficulty(Difficulty.MEDIUM);

                return attemptRepo.save(attempt);
        }

        // ================= FIRST QUESTION =================
        public AIQuestion getFirstQuestion(String subjectName) {
                Subject subject = subjectRepo.findByName(subjectName)
                                .orElseThrow(() -> new RuntimeException("Subject not found: " + subjectName));

                try {
                        return openAIService.generateQuestion(
                                        subject,
                                        Difficulty.MEDIUM);
                } catch (Exception e) {
                        System.err.println("Fallback to Local DB: " + e.getMessage());
                        return getFallbackQuestion(subject, Difficulty.MEDIUM);
                }
        }

        private AIQuestion getFallbackQuestion(Subject subject, Difficulty difficulty) {
                com.quiz.AdaptiveQuiz.entity.Question dbQ = ((com.quiz.AdaptiveQuiz.repository.QuestionRepository) questionRepo)
                                .findRandomBySubjectAndDifficulty(subject, difficulty);

                if (dbQ == null) {
                        // Relax difficulty constraint
                        dbQ = ((com.quiz.AdaptiveQuiz.repository.QuestionRepository) questionRepo)
                                        .findRandomBySubject(subject);
                }

                if (dbQ == null) {
                        throw new RuntimeException("No questions available for subject: " + subject.getName());
                }

                // Map DB Entity back to AIQuestion DTO for frontend consistency
                AIQuestion aiQ = new AIQuestion();
                aiQ.setQuestion(dbQ.getContent());
                aiQ.setOptions(dbQ.getOptions());
                aiQ.setCorrectAnswer(dbQ.getCorrectAnswer());
                aiQ.setSubject(subject);
                aiQ.setDifficulty(dbQ.getDifficulty());
                return aiQ;
        }

        // ================= SUBMIT ANSWER =================
        @Transactional
        public com.quiz.AdaptiveQuiz.dto.SubmissionResponse submitAnswerByAttempt(
                        Long attemptId,
                        String questionText,
                        String selectedAnswer,
                        String correctAnswer) {

                QuizAttempt attempt = attemptRepo.findById(attemptId).orElseThrow();

                // Idempotency Check: Prevent duplicate submissions (Time-based: 2 seconds)
                UserResponse lastResponse = responseRepo.findTopByAttemptOrderByIdDesc(attempt);
                if (lastResponse != null) {
                        java.time.Duration diff = java.time.Duration.between(lastResponse.getCreatedAt(),
                                        java.time.LocalDateTime.now());
                        if (diff.toSeconds() < 2) {
                                System.out.println("Duplicate submission detected (Too fast). Ignoring.");
                                return new com.quiz.AdaptiveQuiz.dto.SubmissionResponse(false, "Duplicate Ignored");
                        }
                        // Also check strict text duplicate if it's the EXACT same question text
                        if (lastResponse.getQuestionText().trim().equalsIgnoreCase(questionText.trim())) {
                                System.out.println("Duplicate Question Submission (Same Text). Ignoring.");
                                return new com.quiz.AdaptiveQuiz.dto.SubmissionResponse(false, "Duplicate Ignored");
                        }
                }

                boolean isSkipped = selectedAnswer == null || selectedAnswer.trim().isEmpty();
                boolean isCorrect = !isSkipped && selectedAnswer.equals(correctAnswer);

                // User ka response save karo
                UserResponse response = new UserResponse();
                response.setAttempt(attempt);

                // Safety defaults
                response.setQuestionText(questionText != null ? questionText : "Unknown Question");
                response.setSelectedAnswer(isSkipped ? "SKIPPED" : selectedAnswer);
                response.setCorrectAnswer(correctAnswer != null ? correctAnswer : "Unknown");
                response.setDifficulty(attempt.getCurrentDifficulty());
                response.setCorrect(isCorrect);

                responseRepo.save(response);

                // Score update
                // Score update & Adaptive Logic
                Difficulty current = attempt.getCurrentDifficulty();
                Difficulty next = current;

                if (isCorrect) {
                        attempt.setCorrectAnswers(attempt.getCorrectAnswers() + 1);
                        next = adaptiveLogicService.nextDifficulty(current, true);
                } else {
                        if (isSkipped) {
                                attempt.setSkippedAnswers(attempt.getSkippedAnswers() + 1);
                                next = current; // No change for skipped
                        } else {
                                attempt.setWrongAnswers(attempt.getWrongAnswers() + 1);
                                next = adaptiveLogicService.nextDifficulty(current, false);
                        }
                }

                attempt.setCurrentDifficulty(next);
                attemptRepo.save(attempt);

                int attempted = attempt.getCorrectAnswers()
                                + attempt.getWrongAnswers()
                                + attempt.getSkippedAnswers();

                // Quiz complete
                if (attempted >= attempt.getTotalQuestions()) {
                        calculateAccuracy(attempt);
                        saveSkillSnapshot(attempt);
                        return new com.quiz.AdaptiveQuiz.dto.SubmissionResponse(true, "Quiz Completed");
                }

                return new com.quiz.AdaptiveQuiz.dto.SubmissionResponse(false, "Answer Submitted");
        }

        // ================= GENERATE NEXT QUESTION =================
        public AIQuestion generateNextQuestion(Long attemptId) {
                QuizAttempt attempt = attemptRepo.findById(attemptId).orElseThrow();

                // If already completed, return null or handle appropriately
                if ((attempt.getCorrectAnswers() + attempt.getWrongAnswers() + attempt.getSkippedAnswers()) >= attempt
                                .getTotalQuestions()) {
                        return null;
                }

                // Fetch FULL history to avoid duplicates across all attempts
                List<String> previousQuestions = responseRepo.findDistinctQuestionTextByUserAndSubject(
                                attempt.getUser().getId(),
                                attempt.getSubject().getId());

                // Normalize for comparison
                previousQuestions = previousQuestions.stream()
                                .map(String::trim)
                                .map(String::toLowerCase)
                                .collect(java.util.stream.Collectors.toList());

                for (int i = 0; i < 3; i++) { // Try 3 times (reduced from 5 to avoid rate limits)
                        try {
                                AIQuestion question = openAIService.generateQuestion(
                                                attempt.getSubject(),
                                                attempt.getCurrentDifficulty());

                                if (question == null)
                                        continue;

                                String newQuestionText = question.getQuestion().trim().toLowerCase();
                                boolean isDuplicate = previousQuestions.stream()
                                                .anyMatch(prev -> prev.equals(newQuestionText));

                                if (!isDuplicate) {
                                        return question;
                                }
                                System.out.println("⚠️ Generated duplicate question, retrying... (Attempt " + (i + 1)
                                                + ")");
                        } catch (Exception e) {
                                System.err.println("Error generating question: " + e.getMessage());
                        }
                }

                System.out.println("⚠️ OpenAI failed or duplicate. Fetching fallback from DB.");
                return getFallbackQuestion(attempt.getSubject(), attempt.getCurrentDifficulty());
        }

        private void saveSkillSnapshot(QuizAttempt attempt) {
                adaptiveLogicService.saveSkillSnapshot(
                                attempt.getUser(),
                                attempt.getSubject(),
                                attempt.getCorrectAnswers(),
                                attempt.getTotalQuestions());
        }

        // ================= ACCURACY =================
        private void calculateAccuracy(QuizAttempt attempt) {

                double accuracy = (attempt.getCorrectAnswers() * 100.0)
                                / attempt.getTotalQuestions();

                attempt.setAccuracy(accuracy);
                attemptRepo.save(attempt);
        }

        // ================= RESULT =================
        public QuizResultResponse getResult(Long attemptId) {

                QuizAttempt attempt = attemptRepo.findById(attemptId).orElseThrow();

                return new QuizResultResponse(
                                attempt.getSubject(),
                                attempt.getTotalQuestions(),
                                attempt.getCorrectAnswers(),
                                attempt.getWrongAnswers(),
                                attempt.getSkippedAnswers(),
                                attempt.getAccuracy());
        }

        // ================= REVIEW =================
        public List<UserResponse> getReview(Long attemptId) {
                QuizAttempt attempt = attemptRepo.findById(attemptId).orElseThrow();
                return responseRepo.findByAttempt(attempt);
        }

        public java.util.Map<String, Object> getQuizStatus(Long attemptId) {
                QuizAttempt attempt = attemptRepo.findById(attemptId)
                                .orElseThrow(() -> new RuntimeException("Attempt not found"));

                long elapsed = java.time.Duration.between(attempt.getStartTime(), java.time.LocalDateTime.now())
                                .toSeconds();
                long remaining = 900 - elapsed; // 15 minutes = 900 seconds

                if (remaining < 0)
                        remaining = 0;

                return java.util.Map.of(
                                "startTime", attempt.getStartTime().toString(),
                                "remainingSeconds", remaining);
        }

        @Transactional
        public void finishQuiz(Long attemptId) {
                QuizAttempt attempt = attemptRepo.findById(attemptId)
                                .orElseThrow(() -> new RuntimeException("Attempt not found"));

                int completed = attempt.getCorrectAnswers() + attempt.getWrongAnswers() + attempt.getSkippedAnswers();
                int remaining = attempt.getTotalQuestions() - completed;

                if (remaining > 0) {
                        attempt.setSkippedAnswers(attempt.getSkippedAnswers() + remaining);
                }

                // Finalize logic
                calculateAccuracy(attempt);
                saveSkillSnapshot(attempt);

                // Mark as finished if we had an endTime field, but currently we rely on total
                // questions being accounted for.
                attemptRepo.save(attempt);
        }
}
