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
        // EASY - Seed 3 Questions
        if (questionRepo.countBySubjectAndDifficulty(subject, Difficulty.EASY) < 3) {
            createQuestion(questionRepo, subject, Difficulty.EASY,
                    "Basic " + subject.getName() + " concept? ", "A", List.of("A", "B", "C", "D"));
            createQuestion(questionRepo, subject, Difficulty.EASY,
                    "Intro to " + subject.getName() + " syntax? ", "B", List.of("A", "B", "C", "D"));
            createQuestion(questionRepo, subject, Difficulty.EASY,
                    "Simple " + subject.getName() + " definition? ", "C", List.of("A", "B", "C", "D"));
        }

        // MEDIUM - Seed 3 Questions
        if (questionRepo.countBySubjectAndDifficulty(subject, Difficulty.MEDIUM) < 3) {
            createQuestion(questionRepo, subject, Difficulty.MEDIUM,
                    "Intermediate " + subject.getName() + " logic? ", "B", List.of("X", "Y", "Z", "W"));
            createQuestion(questionRepo, subject, Difficulty.MEDIUM,
                    "Common " + subject.getName() + " pattern? ", "C", List.of("P", "Q", "R", "S"));
            createQuestion(questionRepo, subject, Difficulty.MEDIUM,
                    "Explain " + subject.getName() + " lifecycle? ", "A", List.of("1", "2", "3", "4"));
        }

        // HARD - Seed 3 Questions
        if (questionRepo.countBySubjectAndDifficulty(subject, Difficulty.HARD) < 3) {
            createQuestion(questionRepo, subject, Difficulty.HARD,
                    "Advanced " + subject.getName() + " optimization? ", "D", List.of("Ops1", "Ops2", "Ops3", "Ops4"));
            createQuestion(questionRepo, subject, Difficulty.HARD,
                    "Deep dive " + subject.getName() + " internals? ", "A", List.of("Core", "Shell", "Kernel", "None"));
            createQuestion(questionRepo, subject, Difficulty.HARD,
                    "Complex " + subject.getName() + " architecture? ", "B",
                    List.of("Mono", "Micro", "Serverless", "Hybrid"));
        }
    }

    private void createQuestion(QuestionRepository repo, Subject sub, Difficulty diff, String content, String ans,
            List<String> opts) {
        Question q = new Question();
        q.setSubject(sub);
        q.setDifficulty(diff);
        q.setContent(content);
        q.setCorrectAnswer(ans);
        q.setOptions(opts);
        repo.save(q);
    }
}
