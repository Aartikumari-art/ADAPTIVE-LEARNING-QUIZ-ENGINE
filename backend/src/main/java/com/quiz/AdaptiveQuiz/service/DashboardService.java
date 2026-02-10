package com.quiz.AdaptiveQuiz.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.quiz.AdaptiveQuiz.dto.UserDashboardDTO;
import com.quiz.AdaptiveQuiz.entity.QuizAttempt;
import com.quiz.AdaptiveQuiz.entity.SkillSnapshot;
import com.quiz.AdaptiveQuiz.entity.UserResponse;
import com.quiz.AdaptiveQuiz.repository.QuizAttemptRepository;
import com.quiz.AdaptiveQuiz.repository.SkillSnapshotRepository;
import com.quiz.AdaptiveQuiz.repository.UserResponseRepository;

@Service
public class DashboardService {

        private final QuizAttemptRepository quizAttemptRepository;
        private final UserResponseRepository userResponseRepository;
        private final SkillSnapshotRepository skillSnapshotRepository;

        private final com.quiz.AdaptiveQuiz.repository.UserRepository userRepository;

        public DashboardService(
                        QuizAttemptRepository quizAttemptRepository,
                        UserResponseRepository userResponseRepository,
                        SkillSnapshotRepository skillSnapshotRepository,
                        com.quiz.AdaptiveQuiz.repository.UserRepository userRepository) {
                this.quizAttemptRepository = quizAttemptRepository;
                this.userResponseRepository = userResponseRepository;
                this.skillSnapshotRepository = skillSnapshotRepository;
                this.userRepository = userRepository;
        }

        public UserDashboardDTO getDashboard(String email) {
                if (email == null)
                        throw new RuntimeException("Email is required");

                // Total quizzes
                List<QuizAttempt> attempts = quizAttemptRepository.findAll();
                int totalQuizzes = (int) attempts.stream()
                                .filter(a -> a.getUser() != null && email.equals(a.getUser().getEmail()))
                                .count();

                // Correct & Wrong answers
                List<UserResponse> responses = userResponseRepository.findAll();

                int totalCorrect = (int) responses.stream()
                                .filter(r -> r.isCorrect()
                                                && r.getAttempt() != null
                                                && r.getAttempt().getUser() != null
                                                && email.equals(r.getAttempt().getUser().getEmail()))
                                .count();

                int totalWrong = (int) responses.stream()
                                .filter(r -> !r.isCorrect()
                                                && r.getAttempt() != null
                                                && r.getAttempt().getUser() != null
                                                && email.equals(r.getAttempt().getUser().getEmail()))
                                .count();

                double accuracy = (totalCorrect + totalWrong) == 0
                                ? 0
                                : (totalCorrect * 100.0) / (totalCorrect + totalWrong);

                List<SkillSnapshot> snapshots = skillSnapshotRepository.findByUser_Email(email);

                int skillScore = snapshots.isEmpty()
                                ? 50
                                : (int) snapshots.get(snapshots.size() - 1).getSkillScore();

                // Per-Subject Analytics
                java.util.Map<com.quiz.AdaptiveQuiz.entity.Subject, java.util.List<QuizAttempt>> attemptsBySubject = attempts
                                .stream()
                                .filter(a -> a.getUser() != null && email.equals(a.getUser().getEmail()))
                                .collect(java.util.stream.Collectors.groupingBy(QuizAttempt::getSubject));

                java.util.List<com.quiz.AdaptiveQuiz.dto.SubjectAnalyticsDTO> subjectStats = new java.util.ArrayList<>();

                for (com.quiz.AdaptiveQuiz.entity.Subject sub : attemptsBySubject.keySet()) {
                        java.util.List<QuizAttempt> subAttempts = attemptsBySubject.get(sub);
                        int subTotalAttempts = subAttempts.size();

                        double subAvgAccuracy = subAttempts.stream()
                                        .mapToDouble(QuizAttempt::getAccuracy)
                                        .average().orElse(0.0);

                        // Get latest skill score for this subject
                        double subSkillScore = snapshots.stream()
                                        .filter(s -> s.getSubject() == sub)
                                        .sorted((a, b) -> b.getTimestamp().compareTo(a.getTimestamp())) // Sort desc
                                        .mapToDouble(SkillSnapshot::getSkillScore)
                                        .findFirst().orElse(0.0);

                        subjectStats.add(new com.quiz.AdaptiveQuiz.dto.SubjectAnalyticsDTO(
                                        sub,
                                        subTotalAttempts,
                                        subAvgAccuracy,
                                        subSkillScore));
                }

                int totalSubjectsAttempted = subjectStats.size();

                String userName = userRepository.findByEmail(email)
                                .map(u -> u.getName())
                                .orElse("Student");

                return new UserDashboardDTO(
                                totalQuizzes,
                                totalCorrect,
                                totalWrong,
                                accuracy,
                                skillScore,
                                userName,
                                totalSubjectsAttempted,
                                subjectStats);
        }

        public List<com.quiz.AdaptiveQuiz.dto.LeaderboardDTO> getLeaderboard() {
                List<SkillSnapshot> allSnapshots = skillSnapshotRepository.findAll();

                // Group by User -> Subject -> Latest Snapshot
                java.util.Map<String, java.util.Map<String, SkillSnapshot>> userSubjectSnapshots = new java.util.HashMap<>();

                for (SkillSnapshot s : allSnapshots) {
                        if (s.getUser() == null || s.getSubject() == null)
                                continue;

                        String email = s.getUser().getEmail();
                        String subject = s.getSubject().getName();

                        userSubjectSnapshots.putIfAbsent(email, new java.util.HashMap<>());
                        java.util.Map<String, SkillSnapshot> subjectMap = userSubjectSnapshots.get(email);

                        if (!subjectMap.containsKey(subject)
                                        || s.getTimestamp().isAfter(subjectMap.get(subject).getTimestamp())) {
                                subjectMap.put(subject, s);
                        }
                }

                List<com.quiz.AdaptiveQuiz.dto.LeaderboardDTO> leaderboard = new java.util.ArrayList<>();
                int rank = 1;

                for (String email : userSubjectSnapshots.keySet()) {
                        java.util.Map<String, SkillSnapshot> subjectMap = userSubjectSnapshots.get(email);

                        // Calculate Average Skill
                        double totalSkill = 0;
                        for (SkillSnapshot s : subjectMap.values()) {
                                totalSkill += s.getSkillScore();
                        }
                        int avgSkill = subjectMap.isEmpty() ? 0 : (int) (totalSkill / subjectMap.size());

                        // Get User Name (from any snapshot)
                        String name = subjectMap.values().iterator().next().getUser().getName();

                        // Quizzes Taken (Expensive but necessary for now)
                        long quizzesTaken = quizAttemptRepository.findAll().stream()
                                        .filter(a -> a.getUser() != null && email.equals(a.getUser().getEmail()))
                                        .count();

                        leaderboard.add(new com.quiz.AdaptiveQuiz.dto.LeaderboardDTO(
                                        0, // Rank set later
                                        name,
                                        avgSkill,
                                        (int) quizzesTaken,
                                        0.0));
                }

                // Sort by Skill Score Descending
                leaderboard.sort((a, b) -> Integer.compare(b.getSkillScore(), a.getSkillScore()));

                // Assign Ranks
                for (int i = 0; i < leaderboard.size(); i++) {
                        leaderboard.get(i).setRank(i + 1);
                }

                return leaderboard;
        }
}
