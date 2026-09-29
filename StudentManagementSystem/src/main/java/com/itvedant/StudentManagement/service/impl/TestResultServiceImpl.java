package com.itvedant.StudentManagement.service.impl;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itvedant.StudentManagement.dto.QuestionResultStats;
import com.itvedant.StudentManagement.model.Question;
import com.itvedant.StudentManagement.model.TestAttempt;
import com.itvedant.StudentManagement.reposatory.TestAttemptRepository;
import com.itvedant.StudentManagement.services.TestResultService;

@Service
@Transactional(readOnly = true)
public class TestResultServiceImpl implements TestResultService {

    private final TestAttemptRepository testAttemptRepository;

    public TestResultServiceImpl(
            TestAttemptRepository testAttemptRepository) {

        this.testAttemptRepository = testAttemptRepository;
    }

    @Override
    public Map<Long, QuestionResultStats> getQuestionResultStats(
            Long testId,
            List<Question> questions) {

        // 1. submitted attempts (with answers) - keep only the LATEST attempt per student
        //    so a student who retook the test is counted once
        Map<String, TestAttempt> latestPerStudent = new HashMap<>();

        for (TestAttempt attempt :
                testAttemptRepository.findSubmittedAttemptsWithAnswers(testId)) {

            String key = attempt.getStudentUsername() != null
                    ? attempt.getStudentUsername()
                    : "attempt-" + attempt.getId();

            TestAttempt existing = latestPerStudent.get(key);

            if (existing == null || attempt.getId() > existing.getId()) {
                latestPerStudent.put(key, attempt);
            }
        }

        long totalStudents = latestPerStudent.size();

        // 2. per-question counters
        Map<Long, long[]> counters = new LinkedHashMap<>();   // [correct, wrong]

        if (questions != null) {

            for (Question q : questions) {

                if (q == null || q.getId() == null) {
                    continue;
                }

                counters.put(q.getId(), new long[]{0, 0});
            }
        }

        // 3. count correct / wrong from each student's saved answers
        for (TestAttempt attempt : latestPerStudent.values()) {

            if (attempt.getAnswers() == null) {
                continue;
            }

            for (var answer : attempt.getAnswers()) {

                if (answer == null || answer.getQuestion() == null) {
                    continue;
                }

                Question q = answer.getQuestion();
                long[] c = counters.get(q.getId());

                if (c == null) {
                    continue;
                }

                String selected = answer.getSelectedAnswer();

                if (selected == null || selected.trim().isEmpty()) {
                    continue;                       // counted as "never opened" below
                }

                if (isCorrect(q, selected)) {
                    c[0]++;
                } else {
                    c[1]++;
                }
            }
        }

        // 4. never opened / unanswered = students who submitted - (correct + wrong)
        Map<Long, QuestionResultStats> result = new LinkedHashMap<>();

        for (Map.Entry<Long, long[]> entry : counters.entrySet()) {

            Long questionId = entry.getKey();
            long[] c = entry.getValue();
            long unanswered = Math.max(0, totalStudents - c[0] - c[1]);

            result.put(questionId,
                    new QuestionResultStats(c[0], c[1], unanswered));
        }

        return result;
    }

    /** Works when answers are stored as "A".."D" or as the option text. */
    private boolean isCorrect(Question q, String selected) {

        String correct = q.getCorrectAnswer();

        if (correct == null) {
            return false;
        }

        String sel = selected.trim();
        String cor = correct.trim();

        if (sel.equalsIgnoreCase(cor)) {
            return true;
        }

        return optionText(q, sel).equalsIgnoreCase(optionText(q, cor));
    }

    private String optionText(Question q, String value) {

        String text;

        switch (value.toUpperCase()) {
            case "A": text = q.getOptionA(); break;
            case "B": text = q.getOptionB(); break;
            case "C": text = q.getOptionC(); break;
            case "D": text = q.getOptionD(); break;
            default:  text = value;
        }

        return text == null ? "" : text.trim();
    }
}