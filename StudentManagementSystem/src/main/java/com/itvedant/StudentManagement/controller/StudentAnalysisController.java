package com.itvedant.StudentManagement.controller;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.itvedant.StudentManagement.model.TestAttempt;
import com.itvedant.StudentManagement.reposatory.TestAttemptRepository;

@Controller
@RequestMapping("/student")
public class StudentAnalysisController {

    private final TestAttemptRepository attemptRepository;

    public StudentAnalysisController(TestAttemptRepository attemptRepository) {
        this.attemptRepository = attemptRepository;
    }

    // ============================================================
    // STUDENT ANALYSIS
    // ============================================================

    @GetMapping("/analysis")
    public String analysis(Authentication authentication, Model model) {

        String username = authentication.getName();

        // ========================================================
        // GET ALL ATTEMPTS
        // ========================================================

        List<TestAttempt> allAttempts = attemptRepository.findByStudentUsername(username);

        if (allAttempts == null) {
            allAttempts = new ArrayList<>();
        }

        // ========================================================
        // SORT BY STARTED DATE
        // ========================================================

        allAttempts.sort(
                Comparator.comparing(
                        TestAttempt::getStartedAt,
                        Comparator.nullsLast(Comparator.naturalOrder())
                )
        );

        // ========================================================
        // BASIC COUNTS
        // ========================================================

        int totalAttempts = allAttempts.size();
        int submittedCount = 0;
        int inProgressCount = 0;
        int totalMarksObtained = 0;
        int totalMarksPossible = 0;
        int totalCorrect = 0;
        int totalWrong = 0;
        int totalUnanswered = 0;

        // ========================================================
        // TEST-WISE ROWS
        // ========================================================

        List<AnalysisRow> rows = new ArrayList<>();

        for (TestAttempt attempt : allAttempts) {

            if (attempt == null || attempt.getTest() == null) {
                continue;
            }

            // ----------------------------------------------------
            // STATUS
            // ----------------------------------------------------

            String status = attempt.getStatus();
            boolean submitted = status != null && status.equalsIgnoreCase("SUBMITTED");

            if (submitted) {
                submittedCount++;
            } else {
                inProgressCount++;
            }

            // ----------------------------------------------------
            // MARKS
            // ----------------------------------------------------

            int obtainedMarks = attempt.getObtainedMarks() == null ? 0 : attempt.getObtainedMarks();
            int totalMarks = attempt.getTest().getTotalMarks() == null ? 0 : attempt.getTest().getTotalMarks();

            // ----------------------------------------------------
            // OTHER DATA
            // ----------------------------------------------------

            int correct = attempt.getCorrectAnswers() == null ? 0 : attempt.getCorrectAnswers();
            int wrong = attempt.getWrongAnswers() == null ? 0 : attempt.getWrongAnswers();
            int unanswered = attempt.getUnansweredQuestions() == null ? 0 : attempt.getUnansweredQuestions();

            // ----------------------------------------------------
            // PERCENTAGE
            // ----------------------------------------------------

            double percentage = 0;
            if (totalMarks > 0) {
                percentage = (obtainedMarks * 100.0) / totalMarks;
            }

            // ----------------------------------------------------
            // OVERALL ONLY SUBMITTED TESTS
            // ----------------------------------------------------

            if (submitted) {
                totalMarksObtained += obtainedMarks;
                totalMarksPossible += totalMarks;
                totalCorrect += correct;
                totalWrong += wrong;
                totalUnanswered += unanswered;
            }

            // ----------------------------------------------------
            // TEST METADATA
            // ----------------------------------------------------

            String testName = attempt.getTest().getTestName();
            String course = attempt.getTest().getCourse();
            String subject = attempt.getTest().getSubject();
            int attemptNumber = attempt.getAttemptNumber() == null ? 1 : attempt.getAttemptNumber();

            // ----------------------------------------------------
            // TOPPER MARKS & NAME (Calculated across all students for test)
            // ----------------------------------------------------
            Long testId = attempt.getTest().getId();
            Integer maxScore = attemptRepository.findMaxObtainedMarksByTestId(testId);
            Integer topperMarks = maxScore != null ? maxScore : obtainedMarks;
            
            // Fetch Topper Name (fallback to current student's username if none found)
            List<String> topperList = attemptRepository.findTopperNameByTestId(testId, PageRequest.of(0, 1));
            String topperName = (topperList != null && !topperList.isEmpty()) ? topperList.get(0) : username;

            // ----------------------------------------------------
            // ADD ROW
            // ----------------------------------------------------

            rows.add(
                    new AnalysisRow(
                            testName,
                            course,
                            subject,
                            attemptNumber,
                            obtainedMarks,
                            totalMarks,
                            percentage,
                            correct,
                            wrong,
                            unanswered,
                            status,
                            attempt.getSubmittedAt(),
                            topperMarks,
                            topperName
                    )
            );
        }

        // ========================================================
        // OVERALL PERCENTAGE
        // ========================================================

        double overallPercentage = 0;
        if (totalMarksPossible > 0) {
            overallPercentage = (totalMarksObtained * 100.0) / totalMarksPossible;
        }

        // ========================================================
        // FIRST TWO SUBMITTED TESTS
        // ========================================================

        List<TestAttempt> submittedAttempts = allAttempts.stream()
                .filter(attempt -> attempt.getStatus() != null && attempt.getStatus().equalsIgnoreCase("SUBMITTED"))
                .filter(attempt -> attempt.getSubmittedAt() != null)
                .sorted(Comparator.comparing(TestAttempt::getSubmittedAt))
                .toList();

        // ========================================================
        // ADD BASIC DATA TO PAGE
        // ========================================================

        model.addAttribute("totalAttempts", totalAttempts);
        model.addAttribute("submittedCount", submittedCount);
        model.addAttribute("inProgressCount", inProgressCount);
        model.addAttribute("overallPercentage", overallPercentage);
        model.addAttribute("totalMarksObtained", totalMarksObtained);
        model.addAttribute("totalMarksPossible", totalMarksPossible);
        model.addAttribute("totalCorrect", totalCorrect);
        model.addAttribute("totalWrong", totalWrong);
        model.addAttribute("totalUnanswered", totalUnanswered);
        model.addAttribute("rows", rows);
        model.addAttribute("hasComparison", false);

        // ========================================================
        // COMPARE FIRST TEST AND SECOND TEST
        // ========================================================

        if (submittedAttempts.size() >= 2) {

            TestAttempt firstTest = submittedAttempts.get(0);
            TestAttempt secondTest = submittedAttempts.get(1);

            int firstMarks = firstTest.getObtainedMarks() == null ? 0 : firstTest.getObtainedMarks();
            int firstTotal = firstTest.getTest().getTotalMarks() == null ? 0 : firstTest.getTest().getTotalMarks();
            double firstPercentage = calculatePercentage(firstMarks, firstTotal);

            int secondMarks = secondTest.getObtainedMarks() == null ? 0 : secondTest.getObtainedMarks();
            int secondTotal = secondTest.getTest().getTotalMarks() == null ? 0 : secondTest.getTest().getTotalMarks();
            double secondPercentage = calculatePercentage(secondMarks, secondTotal);

            int marksDifference = secondMarks - firstMarks;
            double percentageDifference = secondPercentage - firstPercentage;

            int firstCorrect = firstTest.getCorrectAnswers() == null ? 0 : firstTest.getCorrectAnswers();
            int secondCorrect = secondTest.getCorrectAnswers() == null ? 0 : secondTest.getCorrectAnswers();
            int correctDifference = secondCorrect - firstCorrect;

            int firstWrong = firstTest.getWrongAnswers() == null ? 0 : firstTest.getWrongAnswers();
            int secondWrong = secondTest.getWrongAnswers() == null ? 0 : secondTest.getWrongAnswers();
            int wrongDifference = secondWrong - firstWrong;

            int firstUnanswered = firstTest.getUnansweredQuestions() == null ? 0 : firstTest.getUnansweredQuestions();
            int secondUnanswered = secondTest.getUnansweredQuestions() == null ? 0 : secondTest.getUnansweredQuestions();
            int unansweredDifference = secondUnanswered - firstUnanswered;

            String performanceStatus;
            if (percentageDifference > 0) {
                performanceStatus = "IMPROVED";
            } else if (percentageDifference < 0) {
                performanceStatus = "DECREASED";
            } else {
                performanceStatus = "NO_CHANGE";
            }

            model.addAttribute("firstTest", firstTest);
            model.addAttribute("secondTest", secondTest);
            model.addAttribute("firstMarks", firstMarks);
            model.addAttribute("secondMarks", secondMarks);
            model.addAttribute("firstTotal", firstTotal);
            model.addAttribute("secondTotal", secondTotal);
            model.addAttribute("firstPercentage", firstPercentage);
            model.addAttribute("secondPercentage", secondPercentage);
            model.addAttribute("marksDifference", marksDifference);
            model.addAttribute("percentageDifference", percentageDifference);
            model.addAttribute("firstCorrect", firstCorrect);
            model.addAttribute("secondCorrect", secondCorrect);
            model.addAttribute("correctDifference", correctDifference);
            model.addAttribute("firstWrong", firstWrong);
            model.addAttribute("secondWrong", secondWrong);
            model.addAttribute("wrongDifference", wrongDifference);
            model.addAttribute("firstUnanswered", firstUnanswered);
            model.addAttribute("secondUnanswered", secondUnanswered);
            model.addAttribute("unansweredDifference", unansweredDifference);
            model.addAttribute("performanceStatus", performanceStatus);
            model.addAttribute("hasComparison", true);
        }

        return "student/analysis";
    }

    private double calculatePercentage(int obtained, int total) {
        if (total <= 0) {
            return 0;
        }
        return (obtained * 100.0) / total;
    }

    // ============================================================
    // ANALYSIS ROW DTO
    // ============================================================

    public static class AnalysisRow {

        private final String testName;
        private final String course;
        private final String subject;
        private final int attemptNumber;
        private final int obtainedMarks;
        private final int totalMarks;
        private final double percentage;
        private final int correct;
        private final int wrong;
        private final int unanswered;
        private final String status;
        private final java.time.LocalDateTime submittedAt;
        private final Integer topperMarks;
        private final String topperName;

        public AnalysisRow(
                String testName,
                String course,
                String subject,
                int attemptNumber,
                int obtainedMarks,
                int totalMarks,
                double percentage,
                int correct,
                int wrong,
                int unanswered,
                String status,
                java.time.LocalDateTime submittedAt,
                Integer topperMarks,
                String topperName) {

            this.testName = testName;
            this.course = course;
            this.subject = subject;
            this.attemptNumber = attemptNumber;
            this.obtainedMarks = obtainedMarks;
            this.totalMarks = totalMarks;
            this.percentage = percentage;
            this.correct = correct;
            this.wrong = wrong;
            this.unanswered = unanswered;
            this.status = status;
            this.submittedAt = submittedAt;
            this.topperMarks = topperMarks;
            this.topperName = topperName;
        }

        public String getTestName() {
            return testName;
        }

        public String getCourse() {
            return course;
        }

        public String getSubject() {
            return subject;
        }

        public int getAttemptNumber() {
            return attemptNumber;
        }

        public int getObtainedMarks() {
            return obtainedMarks;
        }

        public int getTotalMarks() {
            return totalMarks;
        }

        public double getPercentage() {
            return percentage;
        }

        public int getCorrect() {
            return correct;
        }

        public int getWrong() {
            return wrong;
        }

        public int getUnanswered() {
            return unanswered;
        }

        public String getStatus() {
            return status;
        }

        public java.time.LocalDateTime getSubmittedAt() {
            return submittedAt;
        }

        public Integer getTopperMarks() {
            return topperMarks;
        }

        public String getTopperName() {
            return topperName;
        }
    }
}