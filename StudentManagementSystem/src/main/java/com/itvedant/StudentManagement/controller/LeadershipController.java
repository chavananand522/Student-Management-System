package com.itvedant.StudentManagement.controller;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.itvedant.StudentManagement.model.Students;
import com.itvedant.StudentManagement.model.Test;
import com.itvedant.StudentManagement.model.TestAttempt;
import com.itvedant.StudentManagement.model.Users;
import com.itvedant.StudentManagement.reposatory.StudentRepositiry;
import com.itvedant.StudentManagement.reposatory.TestAttemptRepository;
import com.itvedant.StudentManagement.reposatory.TestRepository;
import com.itvedant.StudentManagement.reposatory.UserRepository;

@Controller
public class LeadershipController {

    private final TestAttemptRepository attemptRepository;
    private final TestRepository testRepository;
    private final StudentRepositiry studentRepository;
    private final UserRepository userRepository;

    public LeadershipController(
            TestAttemptRepository attemptRepository,
            TestRepository testRepository,
            StudentRepositiry studentRepository,
            UserRepository userRepository) {

        this.attemptRepository = attemptRepository;
        this.testRepository = testRepository;
        this.studentRepository = studentRepository;
        this.userRepository = userRepository;
    }

    // =========================================================
    // LEADERSHIP BOARD — BASED ON THE LATEST PUBLISHED TEST
    // =========================================================

    @GetMapping({"/leadership", "/admin/leadership"})
    @Transactional(readOnly = true)
    public String leadership(
            Authentication authentication,
            Model model) {

        // -----------------------------------------------------
        // 1. Latest PUBLISHED test (highest id)
        // -----------------------------------------------------
        List<Test> publishedTests = testRepository
                .findByStatusIgnoreCase("PUBLISHED");

        Test latestTest = publishedTests
                .stream()
                .max(Comparator.comparing(
                        Test::getId,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .orElse(null);

        // -----------------------------------------------------
        // 2. Empty board
        // -----------------------------------------------------
        if (latestTest == null) {

            model.addAttribute("latestTest", null);
            model.addAttribute("rankings",   new ArrayList<>());
            model.addAttribute("firstPlace",  null);
            model.addAttribute("secondPlace", null);
            model.addAttribute("thirdPlace",  null);
            model.addAttribute("currentStudent", null);
            model.addAttribute("currentRank",    0);
            model.addAttribute("totalRanked",    0);
            model.addAttribute("rankingMessage",
                    "No published test available yet.");

            return "leadership";
        }

        // -----------------------------------------------------
        // 3. Every SUBMITTED attempt for this test
        // -----------------------------------------------------
        List<TestAttempt> attempts = attemptRepository
                .findByTestId(latestTest.getId())
                .stream()
                .filter(a -> "SUBMITTED".equalsIgnoreCase(a.getStatus()))
                .collect(Collectors.toList());

        // -----------------------------------------------------
        // 4. One row per attempt
        // -----------------------------------------------------
        List<RankedStudent> ranked = new ArrayList<>();

        for (TestAttempt attempt : attempts) {

            String username = attempt.getStudentUsername();

            if (username == null || username.isBlank()) continue;

            int marks    = attempt.getObtainedMarks() == null
                    ? 0 : attempt.getObtainedMarks();

            int correct  = attempt.getCorrectAnswers() == null
                    ? 0 : attempt.getCorrectAnswers();

            int wrong    = attempt.getWrongAnswers() == null
                    ? 0 : attempt.getWrongAnswers();

            int unans    = attempt.getUnansweredQuestions() == null
                    ? 0 : attempt.getUnansweredQuestions();

            int totalQ   = correct + wrong + unans;

            double percent = totalQ == 0
                    ? 0.0
                    : (marks * 100.0) / totalQ;

            String displayName = resolveStudentName(username);

            ranked.add(new RankedStudent(
                    username,
                    displayName,
                    marks,
                    correct,
                    wrong,
                    unans,
                    percent
            ));
        }

        // -----------------------------------------------------
        // 5. Sort by marks DESC, percent DESC
        // -----------------------------------------------------
        ranked.sort(
                Comparator.comparingInt(RankedStudent::marks)
                          .reversed()
                          .thenComparing(
                              Comparator.comparingDouble(
                                  RankedStudent::percent)
                              .reversed())
        );

        for (int i = 0; i < ranked.size(); i++) {
            ranked.get(i).setRank(i + 1);
        }

        // -----------------------------------------------------
        // 6. Top 3
        // -----------------------------------------------------
        RankedStudent first  = ranked.size() > 0 ? ranked.get(0) : null;
        RankedStudent second = ranked.size() > 1 ? ranked.get(1) : null;
        RankedStudent third  = ranked.size() > 2 ? ranked.get(2) : null;

        // -----------------------------------------------------
        // 7. Current student's rank
        // -----------------------------------------------------
        RankedStudent current = null;
        int currentRank = 0;
        int totalRanked = ranked.size();

        if (authentication != null && authentication.isAuthenticated()) {

            String username = authentication.getName();

            for (RankedStudent r : ranked) {
                if (username.equals(r.username())) {
                    current = r;
                    currentRank = r.rank();
                    break;
                }
            }
        }

        // -----------------------------------------------------
        // 8. Message
        // -----------------------------------------------------
        String rankingMessage =
                buildRankingMessage(currentRank, totalRanked);

        // -----------------------------------------------------
        // 9. Push to model
        // -----------------------------------------------------
        model.addAttribute("latestTest", latestTest);

        model.addAttribute("firstPlace",  first);
        model.addAttribute("secondPlace", second);
        model.addAttribute("thirdPlace",  third);

        model.addAttribute("currentStudent", current);
        model.addAttribute("currentRank",    currentRank);
        model.addAttribute("totalRanked",    totalRanked);
        model.addAttribute("rankingMessage", rankingMessage);

        model.addAttribute("rankings", ranked);

        return "leadership";
    }

    // =========================================================
    // HELPERS
    // =========================================================

    private String resolveStudentName(String username) {

        if (username == null) return "Student";

        try {
            Users user = userRepository
                    .findByUserName(username)
                    .orElse(null);

            if (user != null) {

                if (user.getFullName() != null &&
                    !user.getFullName().isBlank()) {
                    return user.getFullName();
                }

                if (user.getEmail() != null &&
                    !user.getEmail().isBlank()) {
                    return user.getEmail();
                }
            }

            Students student = studentRepository
                    .findByEmailIgnoreCase(username)
                    .orElse(null);

            if (student != null) {

                String first = student.getFirstName() == null
                        ? "" : student.getFirstName();

                String last = student.getLastName() == null
                        ? "" : student.getLastName();

                String full = (first + " " + last).trim();

                if (!full.isEmpty()) return full;
            }

        } catch (Exception ignored) {
        }

        return username;
    }

    private String buildRankingMessage(int rank, int total) {

        if (total == 0) {
            return "No attempts submitted for this test yet. Be the first!";
        }

        if (rank == 0) {
            return "You haven't attempted this test yet. Join the race!";
        }

        if (rank == 1) {
            return "Outstanding! You topped the latest test! 🏆";
        }

        if (rank == 2) {
            return "Excellent! Just one step away from the top spot! 🥈";
        }

        if (rank == 3) {
            return "Great job! You're on the podium! 🥉";
        }

        if (rank <= 10) {
            return "You're in the top 10 — keep pushing to climb higher!";
        }

        return "Just a little more effort to shine on the Leadership Board!";
    }

    // =========================================================
    // INNER CLASS
    // =========================================================

    public static class RankedStudent {

        private final String username;
        private final String displayName;
        private final int marks;
        private final int correct;
        private final int wrong;
        private final int unanswered;
        private final double percent;

        private int rank;

        public RankedStudent(
                String username,
                String displayName,
                int marks,
                int correct,
                int wrong,
                int unanswered,
                double percent) {

            this.username = username;
            this.displayName = displayName;
            this.marks = marks;
            this.correct = correct;
            this.wrong = wrong;
            this.unanswered = unanswered;
            this.percent = percent;
        }

        public String username()    { return username; }
        public String displayName() { return displayName; }
        public int marks()          { return marks; }
        public int correct()        { return correct; }
        public int wrong()          { return wrong; }
        public int unanswered()     { return unanswered; }
        public double percent()     { return percent; }

        public int rank()             { return rank; }
        public void setRank(int rank) { this.rank = rank; }

        public String getUsername()    { return username; }
        public String getDisplayName() { return displayName; }
        public int getMarks()          { return marks; }
        public int getCorrect()        { return correct; }
        public int getWrong()          { return wrong; }
        public int getUnanswered()     { return unanswered; }
        public double getPercent()     { return percent; }
        public int getRank()           { return rank; }

        public String getPercentFormatted() {
            return String.format("%.2f%%", percent);
        }
    }
}