package com.quiz.AdaptiveQuiz.config;

import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.quiz.AdaptiveQuiz.entity.User;
import com.quiz.AdaptiveQuiz.entity.Subject;
import com.quiz.AdaptiveQuiz.entity.Question;
import com.quiz.AdaptiveQuiz.entity.Difficulty;
import com.quiz.AdaptiveQuiz.repository.UserRepository;
import com.quiz.AdaptiveQuiz.repository.SubjectRepository;
import com.quiz.AdaptiveQuiz.repository.QuestionRepository;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initData(UserRepository userRepo,
            PasswordEncoder encoder,
            SubjectRepository subjectRepo,
            QuestionRepository questionRepo) { // Injected QuestionRepository

        return args -> {

            // 1. Initialize Admin
            if (!userRepo.existsByEmail("admin@quiz.com")) {
                User admin = new User();
                admin.setName("MASTER ADMIN");
                admin.setEmail("admin@quiz.com");
                admin.setPassword(encoder.encode("admin123"));
                admin.setRole("ADMIN");
                admin.setVerified(true);
                userRepo.save(admin);
                System.out.println("✅ ADMIN CREATED");
            }

            // 2. Initialize Subjects
            String[] defaultSubjects = {
                    "JAVA", "CPP", "DATABASE_TECHNOLOGIES", "WEB_PROGRAMMING",
                    "CSHARP_ASPNET", "ADVANCED_JAVA", "DSA", "OPERATING_SYSTEM"
            };

            for (String subName : defaultSubjects) {
                Subject s;
                if (!subjectRepo.existsByName(subName)) {
                    s = new Subject(subName);
                    s = subjectRepo.save(s);
                    System.out.println("✅ Subject seeded: " + subName);
                } else {
                    s = subjectRepo.findByName(subName).orElseThrow();
                }

                // Seed dummy questions (Check is inside the method)
                seedQuestions(s, questionRepo);
            }
        };
    }

    private void seedQuestions(Subject subject, QuestionRepository questionRepo) {
        // EASY
        if (!questionRepo.existsBySubjectAndDifficulty(subject, Difficulty.EASY)) {
            Question q = new Question();
            q.setContent("Basic " + subject.getName() + " question (EASY). What is it?");
            q.setSubject(subject);
            q.setDifficulty(Difficulty.EASY);
            q.setOptions(List.of("Option A", "Option B", "Option C", "Option D"));
            q.setCorrectAnswer("Option A");
            questionRepo.save(q);
        }

        // MEDIUM
        if (!questionRepo.existsBySubjectAndDifficulty(subject, Difficulty.MEDIUM)) {
            Question q = new Question();
            q.setContent("Intermediate " + subject.getName() + " question (MEDIUM). Explain?");
            q.setSubject(subject);
            q.setDifficulty(Difficulty.MEDIUM);
            q.setOptions(List.of("Option A", "Option B", "Option C", "Option D"));
            q.setCorrectAnswer("Option B");
            questionRepo.save(q);
        }

        // HARD
        if (!questionRepo.existsBySubjectAndDifficulty(subject, Difficulty.HARD)) {
            Question q = new Question();
            q.setContent("Advanced " + subject.getName() + " question (HARD). Analyze?");
            q.setSubject(subject);
            q.setDifficulty(Difficulty.HARD);
            q.setOptions(List.of("Option A", "Option B", "Option C", "Option D"));
            q.setCorrectAnswer("Option C");
            questionRepo.save(q);
        }
    }
}
