package com.itvedant.StudentManagement.controller;

import com.itvedant.StudentManagement.model.Test;
import com.itvedant.StudentManagement.model.TestAttempt;
import com.itvedant.StudentManagement.reposatory.TestAttemptRepository;
import com.itvedant.StudentManagement.reposatory.TestRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;


import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/tests")
public class TestAnalysisController {

    @Autowired
    private TestRepository testRepository;

    @Autowired
    private TestAttemptRepository testAttemptRepository;


    // =========================================================
    // VIEW ANALYSIS PAGE
    // URL:
    // /admin/tests/view-analysis
    // =========================================================

    @GetMapping("/view-analysis")
    public String viewAnalysis(Model model) {

        List<Test> tests = testRepository.findAll();

        /*
         * Store submitted attempt count for every test.
         *
         * Map:
         * testId -> submitted attempts
         */
        Map<Long, Integer> attemptCounts = new HashMap<>();

        /*
         * Store average percentage for every test.
         *
         * Map:
         * testId -> average percentage
         */
        Map<Long, Double> averagePercentages = new HashMap<>();

        /*
         * Store highest marks for every test.
         */
        Map<Long, Integer> highestMarksMap = new HashMap<>();


        for (Test test : tests) {

            List<TestAttempt> allAttempts =
                    testAttemptRepository.findByTestId(test.getId());


            // -------------------------------------------------
            // Only submitted attempts
            // -------------------------------------------------

            List<TestAttempt> submittedAttempts =
                    allAttempts.stream()
                            .filter(this::isSubmitted)
                            .toList();


            // -------------------------------------------------
            // Attempt count
            // -------------------------------------------------

            attemptCounts.put(
                    test.getId(),
                    submittedAttempts.size()
            );


            // -------------------------------------------------
            // Average percentage
            // -------------------------------------------------

            double averagePercentage = 0.0;

            if (!submittedAttempts.isEmpty()
                    && test.getTotalMarks() != null
                    && test.getTotalMarks() > 0) {

                double totalPercentage = 0.0;

                for (TestAttempt attempt : submittedAttempts) {

                    int obtained =
                            attempt.getObtainedMarks() != null
                                    ? attempt.getObtainedMarks()
                                    : 0;

                    totalPercentage +=
                            (obtained * 100.0)
                                    / test.getTotalMarks();
                }

                averagePercentage =
                        totalPercentage / submittedAttempts.size();
            }

            averagePercentages.put(
                    test.getId(),
                    averagePercentage
            );


            // -------------------------------------------------
            // Highest marks
            // -------------------------------------------------

            int highestMarks = 0;

            for (TestAttempt attempt : submittedAttempts) {

                int obtained =
                        attempt.getObtainedMarks() != null
                                ? attempt.getObtainedMarks()
                                : 0;

                if (obtained > highestMarks) {
                    highestMarks = obtained;
                }
            }

            highestMarksMap.put(
                    test.getId(),
                    highestMarks
            );
        }


        model.addAttribute("tests", tests);
        model.addAttribute("attemptCounts", attemptCounts);
        model.addAttribute("averagePercentages", averagePercentages);
        model.addAttribute("highestMarksMap", highestMarksMap);

        return "tests/view-analysis";
    }


    // =========================================================
    // DETAILED TEST ANALYSIS
    //
    // URL:
    // /admin/tests/{testId}/analysis
    // =========================================================

    @GetMapping("/{testId}/analysis")
    public String testAnalysis(
            @PathVariable Long testId,
            Model model) {

        // -----------------------------------------------------
        // Find test
        // -----------------------------------------------------

        Test test = testRepository.findById(testId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Test not found with ID: " + testId
                        )
                );


        // -----------------------------------------------------
        // Get all attempts
        // -----------------------------------------------------

        List<TestAttempt> allAttempts =
                testAttemptRepository.findByTestId(testId);


        // -----------------------------------------------------
        // Only submitted attempts
        // -----------------------------------------------------

        List<TestAttempt> attempts =
                allAttempts.stream()
                        .filter(this::isSubmitted)
                        .sorted(
                                Comparator.comparing(
                                        this::getObtainedMarks
                                ).reversed()
                        )
                        .toList();


        // -----------------------------------------------------
        // BASIC STATISTICS
        // -----------------------------------------------------

        int totalAttempts = attempts.size();

        int passedAttempts = 0;
        int failedAttempts = 0;

        int totalCorrectAnswers = 0;
        int totalWrongAnswers = 0;
        int totalUnanswered = 0;

        int highestMarks = 0;
        int lowestMarks = 0;

        double totalObtainedMarks = 0.0;


        // -----------------------------------------------------
        // Calculate statistics
        // -----------------------------------------------------

        for (TestAttempt attempt : attempts) {

            int obtainedMarks =
                    getObtainedMarks(attempt);

            int correct =
                    attempt.getCorrectAnswers() != null
                            ? attempt.getCorrectAnswers()
                            : 0;

            int wrong =
                    attempt.getWrongAnswers() != null
                            ? attempt.getWrongAnswers()
                            : 0;

            int unanswered =
                    attempt.getUnansweredQuestions() != null
                            ? attempt.getUnansweredQuestions()
                            : 0;


            totalObtainedMarks += obtainedMarks;

            totalCorrectAnswers += correct;
            totalWrongAnswers += wrong;
            totalUnanswered += unanswered;


            // -------------------------------------------------
            // Highest
            // -------------------------------------------------

            if (obtainedMarks > highestMarks) {
                highestMarks = obtainedMarks;
            }


            // -------------------------------------------------
            // Lowest
            // -------------------------------------------------

            if (lowestMarks == 0 || obtainedMarks < lowestMarks) {
                lowestMarks = obtainedMarks;
            }


            // -------------------------------------------------
            // Pass / Fail
            // -------------------------------------------------

            int passingMarks =
                    test.getPassingMarks() != null
                            ? test.getPassingMarks()
                            : 0;

            if (obtainedMarks >= passingMarks) {
                passedAttempts++;
            } else {
                failedAttempts++;
            }
        }


        // -----------------------------------------------------
        // Average marks
        // -----------------------------------------------------

        double averageMarks = 0.0;

        if (totalAttempts > 0) {

            averageMarks =
                    totalObtainedMarks / totalAttempts;
        }


        // -----------------------------------------------------
        // Average percentage
        // -----------------------------------------------------

        double averagePercentage = 0.0;

        if (test.getTotalMarks() != null
                && test.getTotalMarks() > 0
                && totalAttempts > 0) {

            averagePercentage =
                    (averageMarks * 100.0)
                            / test.getTotalMarks();
        }


        // -----------------------------------------------------
        // Pass percentage
        // -----------------------------------------------------

        double passPercentage = 0.0;

        if (totalAttempts > 0) {

            passPercentage =
                    (passedAttempts * 100.0)
                            / totalAttempts;
        }


        // -----------------------------------------------------
        // Fail percentage
        // -----------------------------------------------------

        double failPercentage = 0.0;

        if (totalAttempts > 0) {

            failPercentage =
                    (failedAttempts * 100.0)
                            / totalAttempts;
        }


        // -----------------------------------------------------
        // Topper
        // -----------------------------------------------------

        String topperName = null;

        if (!attempts.isEmpty()) {

            topperName =
                    attempts.get(0).getStudentUsername();
        }


        // -----------------------------------------------------
        // Model attributes
        // -----------------------------------------------------

        model.addAttribute("test", test);

        model.addAttribute("attempts", attempts);

        model.addAttribute(
                "totalAttempts",
                totalAttempts
        );

        model.addAttribute(
                "passedAttempts",
                passedAttempts
        );

        model.addAttribute(
                "failedAttempts",
                failedAttempts
        );

        model.addAttribute(
                "totalCorrectAnswers",
                totalCorrectAnswers
        );

        model.addAttribute(
                "totalWrongAnswers",
                totalWrongAnswers
        );

        model.addAttribute(
                "totalUnanswered",
                totalUnanswered
        );

        model.addAttribute(
                "highestMarks",
                highestMarks
        );

        model.addAttribute(
                "lowestMarks",
                lowestMarks
        );

        model.addAttribute(
                "averageMarks",
                averageMarks
        );

        model.addAttribute(
                "averagePercentage",
                averagePercentage
        );

        model.addAttribute(
                "passPercentage",
                passPercentage
        );

        model.addAttribute(
                "failPercentage",
                failPercentage
        );

        model.addAttribute(
                "topperName",
                topperName
        );


        return "tests/test-analysis";
    }


    // =========================================================
    // CHECK SUBMITTED
    // =========================================================

    private boolean isSubmitted(TestAttempt attempt) {

        return attempt.getStatus() != null
                && "SUBMITTED".equalsIgnoreCase(
                        attempt.getStatus()
                );
    }


    // =========================================================
    // GET OBTAINED MARKS SAFELY
    // =========================================================

    private int getObtainedMarks(TestAttempt attempt) {

        return attempt.getObtainedMarks() != null
                ? attempt.getObtainedMarks()
                : 0;
    }
}