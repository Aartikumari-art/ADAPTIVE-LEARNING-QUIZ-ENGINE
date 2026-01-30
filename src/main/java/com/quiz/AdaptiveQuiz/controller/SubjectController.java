package com.quiz.AdaptiveQuiz.controller;

import com.quiz.AdaptiveQuiz.entity.Subject;
import com.quiz.AdaptiveQuiz.repository.SubjectRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subjects")
@CrossOrigin
public class SubjectController {

    private final SubjectRepository subjectRepo;

    private final com.quiz.AdaptiveQuiz.repository.QuestionRepository questionRepo;
    private final com.quiz.AdaptiveQuiz.repository.QuizAttemptRepository quizAttemptRepo;

    public SubjectController(SubjectRepository subjectRepo,
            com.quiz.AdaptiveQuiz.repository.QuestionRepository questionRepo,
            com.quiz.AdaptiveQuiz.repository.QuizAttemptRepository quizAttemptRepo) {
        this.subjectRepo = subjectRepo;
        this.questionRepo = questionRepo;
        this.quizAttemptRepo = quizAttemptRepo;
    }

    @GetMapping
    public List<Subject> getAllSubjects() {
        return subjectRepo.findAll();
    }

    @PostMapping
    public ResponseEntity<?> addSubject(@RequestBody Subject subject) {
        if (subjectRepo.existsByName(subject.getName())) {
            return ResponseEntity.badRequest().body("Subject already exists");
        }
        return ResponseEntity.ok(subjectRepo.save(subject));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSubject(@PathVariable Long id) {
        try {
            Subject subject = subjectRepo.findById(id).orElse(null);
            if (subject == null) {
                return ResponseEntity.notFound().build();
            }

            // Explicitly delete children to avoid 500 Constraint Violation
            // (JPA Cascade should handle this, but explicit delete is safer for existing
            // bad state)
            List<com.quiz.AdaptiveQuiz.entity.QuizAttempt> attempts = quizAttemptRepo.findBySubject(subject);
            quizAttemptRepo.deleteAll(attempts);

            List<com.quiz.AdaptiveQuiz.entity.Question> questions = questionRepo.findBySubject(subject);
            questionRepo.deleteAll(questions);

            subjectRepo.delete(subject);
            return ResponseEntity.ok("Subject deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error deleting subject: " + e.getMessage());
        }
    }
}
