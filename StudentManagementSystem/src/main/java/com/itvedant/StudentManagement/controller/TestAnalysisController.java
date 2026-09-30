package com.itvedant.StudentManagement.controller;

import com.itvedant.StudentManagement.model.Test;
import com.itvedant.StudentManagement.model.TestAttempt;
import com.itvedant.StudentManagement.reposatory.TestAttemptRepository;
import com.itvedant.StudentManagement.reposatory.TestRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping("/admin/tests")
public class TestAnalysisController {

    @Autowired
    private TestRepository testRepository;

    @Autowired
    private TestAttemptRepository testAttemptRepository;


    @GetMapping("/{testId}/analysis")
    public String testAnalysis(
            @PathVariable Long testId,
            Model model) {

        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Test not found with ID: " + testId
                ));

        List<TestAttempt> allAttempts =
                testAttemptRepository.findByTestId(testId);

        List<TestAttempt> attempts =
                allAttempts.stream()
                        .filter(this::isSubmitted)
                        .sorted(
                                Comparator.comparing(
                                        this::getObtainedMarks
                                ).reversed()
                        )
                        .toList();

        int totalAttempts = attempts.size();
        int passedAttempts = 0;
        int failedAttempts = 0;
        int totalCorrectAnswers = 0;
        int totalWrongAnswers = 0;
        int totalUnanswered = 0;
        int highestMarks = 0;
        int lowestMarks = Integer.MAX_VALUE;
        double totalObtainedMarks = 0.0;

        for (TestAttempt attempt : attempts) {

            int obtainedMarks = getObtainedMarks(attempt);

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

            if (obtainedMarks > highestMarks) {
                highestMarks = obtainedMarks;
            }

            if (obtainedMarks < lowestMarks) {
                lowestMarks = obtainedMarks;
            }

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

        if (attempts.isEmpty()) {
            lowestMarks = 0;
        }

        double averageMarks = 0.0;
        if (totalAttempts > 0) {
            averageMarks = totalObtainedMarks / totalAttempts;
        }

        double averagePercentage = 0.0;
        if (test.getTotalMarks() != null
                && test.getTotalMarks() > 0
                && totalAttempts > 0) {
            averagePercentage =
                    (averageMarks * 100.0) / test.getTotalMarks();
        }

        double passPercentage = 0.0;
        if (totalAttempts > 0) {
            passPercentage =
                    (passedAttempts * 100.0) / totalAttempts;
        }

        double failPercentage = 0.0;
        if (totalAttempts > 0) {
            failPercentage =
                    (failedAttempts * 100.0) / totalAttempts;
        }

        String topperName = null;
        if (!attempts.isEmpty()) {
            topperName = attempts.get(0).getStudentUsername();
            if (topperName == null || topperName.isBlank()) {
                topperName = "N/A";
            }
        }

        model.addAttribute("test", test);
        model.addAttribute("attempts", attempts);
        model.addAttribute("totalAttempts", totalAttempts);
        model.addAttribute("passedAttempts", passedAttempts);
        model.addAttribute("failedAttempts", failedAttempts);
        model.addAttribute("totalCorrectAnswers", totalCorrectAnswers);
        model.addAttribute("totalWrongAnswers", totalWrongAnswers);
        model.addAttribute("totalUnanswered", totalUnanswered);
        model.addAttribute("highestMarks", highestMarks);
        model.addAttribute("lowestMarks", lowestMarks);
        model.addAttribute("averageMarks", averageMarks);
        model.addAttribute("averagePercentage", averagePercentage);
        model.addAttribute("passPercentage", passPercentage);
        model.addAttribute("failPercentage", failPercentage);
        model.addAttribute("topperName", topperName);

        return "tests/test-analysis";
    }


    private boolean isSubmitted(TestAttempt attempt) {
        return attempt.getStatus() != null
                && "SUBMITTED".equalsIgnoreCase(attempt.getStatus());
    }

    private int getObtainedMarks(TestAttempt attempt) {
        return attempt.getObtainedMarks() != null
                ? attempt.getObtainedMarks()
                : 0;
    }
}