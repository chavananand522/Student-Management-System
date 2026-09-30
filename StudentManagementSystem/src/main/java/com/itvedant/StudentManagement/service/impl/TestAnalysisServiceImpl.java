package com.itvedant.StudentManagement.service.impl;

import com.itvedant.StudentManagement.dto.QuestionAnalysisDTO;
import com.itvedant.StudentManagement.dto.StudentPerformanceDTO;
import com.itvedant.StudentManagement.dto.TestAnalysisDTO;
import com.itvedant.StudentManagement.model.Question;
import com.itvedant.StudentManagement.model.StudentAnswer;
import com.itvedant.StudentManagement.model.Test;
import com.itvedant.StudentManagement.model.TestAttempt;
import com.itvedant.StudentManagement.reposatory.TestAttemptRepository;
import com.itvedant.StudentManagement.reposatory.TestRepository;
import com.itvedant.StudentManagement.services.TestAnalysisService;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Comparator;
import java.util.List;


@Service
public class TestAnalysisServiceImpl
        implements TestAnalysisService {

    private final TestRepository testRepository;

    private final TestAttemptRepository testAttemptRepository;

    public TestAnalysisServiceImpl(
            TestRepository testRepository,
            TestAttemptRepository testAttemptRepository
    ) {

        this.testRepository = testRepository;

        this.testAttemptRepository =
                testAttemptRepository;
    }

    // =========================================================
    // MAIN ANALYSIS
    // =========================================================

    @Override
    @Transactional
    public TestAnalysisDTO getTestAnalysis(
            Long testId
    ) {

        // =====================================================
        // FIND TEST
        // =====================================================

        Test test =
                testRepository.findById(testId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Test not found with ID: "
                                                + testId
                                )
                        );

        // =====================================================
        // FIND SUBMITTED ATTEMPTS
        // =====================================================

        List<TestAttempt> attempts =
                testAttemptRepository
                        .findSubmittedAttemptsWithAnswers(
                                testId
                        );

        // =====================================================
        // CREATE DTO
        // =====================================================

        TestAnalysisDTO dto =
                new TestAnalysisDTO();

        // =====================================================
        // TEST INFORMATION
        // =====================================================

        dto.setTestId(test.getId());

        dto.setTestName(test.getTestName());

        dto.setCourse(test.getCourse());

        dto.setSubject(
                test.getSubject() != null
                        ? test.getSubject()
                        : "General"
        );

        dto.setChapter(
                test.getChapter() != null
                        ? test.getChapter()
                        : "-"
        );

        dto.setTotalMarks(
                test.getTotalMarks() != null
                        ? test.getTotalMarks()
                        : 0
        );

        dto.setPassingMarks(
                test.getPassingMarks() != null
                        ? test.getPassingMarks()
                        : 0
        );

        dto.setDuration(
                test.getDuration() != null
                        ? test.getDuration()
                        : 0
        );

        // =====================================================
        // QUESTIONS
        // =====================================================

        List<Question> questions =
                test.getQuestions();

        int totalQuestions =
                questions != null
                        ? questions.size()
                        : 0;

        dto.setTotalQuestions(
                totalQuestions
        );

        // =====================================================
        // STUDENTS
        // =====================================================

        int totalStudents =
                attempts.size();

        dto.setTotalStudents(
                totalStudents
        );

        dto.setAttemptedStudents(
                totalStudents
        );

        dto.setAttemptedPercentage(
                totalStudents > 0
                        ? 100.0
                        : 0.0
        );

        // =====================================================
        // STUDENT ANALYSIS
        // =====================================================

        double totalScore = 0;

        int passedStudents = 0;

        for (TestAttempt attempt : attempts) {

            StudentPerformanceDTO student =
                    createStudentDTO(
                            attempt,
                            test
                    );

            dto.getStudents()
                    .add(student);

            totalScore +=
                    student.getScore() != null
                            ? student.getScore()
                            : 0;

            if ("PASS".equals(
                    student.getStatus()
            )) {

                passedStudents++;
            }

            addToScoreDistribution(
                    dto,
                    student.getPercentage()
            );
        }

        // =====================================================
        // AVERAGE
        // =====================================================

        if (totalStudents > 0) {

            double averageScore =
                    totalScore / totalStudents;

            dto.setAverageScore(
                    round(averageScore)
            );

            double averagePercentage = 0;

            if (dto.getTotalMarks() > 0) {

                averagePercentage =
                        (
                                averageScore /
                                        dto.getTotalMarks()
                        ) * 100;
            }

            dto.setAveragePercentage(
                    round(averagePercentage)
            );

        } else {

            dto.setAverageScore(0);

            dto.setAveragePercentage(0);
        }

        // =====================================================
        // PASS / FAIL
        // =====================================================

        dto.setPassedStudents(
                passedStudents
        );

        dto.setFailedStudents(
                totalStudents -
                        passedStudents
        );

        if (totalStudents > 0) {

            dto.setPassRate(
                    round(
                            (
                                    (double) passedStudents /
                                            totalStudents
                            ) * 100
                    )
            );

        } else {

            dto.setPassRate(0);
        }

        // =====================================================
        // QUESTION ANALYSIS
        // =====================================================

        buildQuestionAnalysis(
                dto,
                questions,
                attempts
        );

        // =====================================================
        // SORT STUDENTS
        // =====================================================

        dto.getStudents()
                .sort(
                        Comparator.comparingDouble(
                                StudentPerformanceDTO::getPercentage
                        ).reversed()
                );

        return dto;
    }

    // =========================================================
    // STUDENT DTO
    // =========================================================

    private StudentPerformanceDTO createStudentDTO(
            TestAttempt attempt,
            Test test
    ) {

        StudentPerformanceDTO dto =
                new StudentPerformanceDTO();

        dto.setAttemptId(
                attempt.getId()
        );

        dto.setUsername(
                attempt.getStudentUsername()
        );

        /*
         * Your TestAttempt currently stores
         * studentUsername rather than a Student entity.
         *
         * Therefore username is displayed as the
         * student name as well.
         */

        dto.setStudentName(
                attempt.getStudentUsername()
        );

        dto.setAttemptNumber(
                attempt.getAttemptNumber()
        );

        int score =
                attempt.getObtainedMarks() != null
                        ? attempt.getObtainedMarks()
                        : 0;

        dto.setScore(score);

        // =====================================================
        // PERCENTAGE
        // =====================================================

        double percentage = 0;

        if (test.getTotalMarks() != null
                && test.getTotalMarks() > 0) {

            percentage =
                    (
                            (double) score /
                                    test.getTotalMarks()
                    ) * 100;
        }

        dto.setPercentage(
                round(percentage)
        );

        // =====================================================
        // ANSWERS
        // =====================================================

        dto.setCorrectAnswers(
                attempt.getCorrectAnswers() != null
                        ? attempt.getCorrectAnswers()
                        : 0
        );

        dto.setWrongAnswers(
                attempt.getWrongAnswers() != null
                        ? attempt.getWrongAnswers()
                        : 0
        );

        dto.setUnansweredQuestions(
                attempt.getUnansweredQuestions() != null
                        ? attempt.getUnansweredQuestions()
                        : 0
        );

        // =====================================================
        // TIME
        // =====================================================

        long timeTaken = 0;

        if (attempt.getStartedAt() != null
                && attempt.getSubmittedAt() != null) {

            timeTaken =
                    Duration.between(
                            attempt.getStartedAt(),
                            attempt.getSubmittedAt()
                    ).toMinutes();
        }

        dto.setTimeTakenMinutes(
                timeTaken
        );

        // =====================================================
        // STATUS
        // =====================================================

        int passingMarks =
                test.getPassingMarks() != null
                        ? test.getPassingMarks()
                        : 0;

        if (score >= passingMarks) {

            dto.setStatus("PASS");

        } else {

            dto.setStatus("FAIL");
        }

        return dto;
    }

    // =========================================================
    // SCORE DISTRIBUTION
    // =========================================================

    private void addToScoreDistribution(
            TestAnalysisDTO dto,
            double percentage
    ) {

        if (percentage <= 20) {

            dto.setScore0To20(
                    dto.getScore0To20() + 1
            );

        } else if (percentage <= 40) {

            dto.setScore21To40(
                    dto.getScore21To40() + 1
            );

        } else if (percentage <= 60) {

            dto.setScore41To60(
                    dto.getScore41To60() + 1
            );

        } else if (percentage <= 80) {

            dto.setScore61To80(
                    dto.getScore61To80() + 1
            );

        } else {

            dto.setScore81To100(
                    dto.getScore81To100() + 1
            );
        }
    }

    // =========================================================
    // QUESTION ANALYSIS
    // =========================================================

    private void buildQuestionAnalysis(
            TestAnalysisDTO dto,
            List<Question> questions,
            List<TestAttempt> attempts
    ) {

        if (questions == null) {
            return;
        }

        int totalCorrect = 0;

        int totalIncorrect = 0;

        int totalSkipped = 0;

        int questionNumber = 1;

        // =====================================================
        // EACH QUESTION
        // =====================================================

        for (Question question : questions) {

            QuestionAnalysisDTO questionDTO =
                    new QuestionAnalysisDTO();

            questionDTO.setQuestionId(
                    question.getId()
            );

            questionDTO.setQuestionNumber(
                    questionNumber++
            );

            questionDTO.setQuestionText(
                    question.getQuestionText()
            );

            questionDTO.setMarks(
                    question.getMarks() != null
                            ? question.getMarks()
                            : 0
            );

            int correct = 0;

            int incorrect = 0;

            int skipped = 0;

            // =================================================
            // EACH STUDENT ATTEMPT
            // =================================================

            for (TestAttempt attempt : attempts) {

                StudentAnswer answer =
                        findAnswerForQuestion(
                                attempt,
                                question.getId()
                        );

                // =============================================
                // SKIPPED
                // =============================================

                if (answer == null) {

                    skipped++;

                    continue;
                }

                // =============================================
                // CORRECT
                // =============================================

                if (Boolean.TRUE.equals(
                        answer.getCorrect()
                )) {

                    correct++;

                } else {

                    incorrect++;
                }
            }

            // =================================================
            // TOTAL
            // =================================================

            int total =
                    correct +
                            incorrect +
                            skipped;

            questionDTO.setTotalStudents(
                    total
            );

            questionDTO.setCorrectCount(
                    correct
            );

            questionDTO.setIncorrectCount(
                    incorrect
            );

            questionDTO.setSkippedCount(
                    skipped
            );

            // =================================================
            // PERCENTAGES
            // =================================================

            double correctPercentage = 0;

            double incorrectPercentage = 0;

            double skippedPercentage = 0;

            if (total > 0) {

                correctPercentage =
                        (
                                (double) correct /
                                        total
                        ) * 100;

                incorrectPercentage =
                        (
                                (double) incorrect /
                                        total
                        ) * 100;

                skippedPercentage =
                        (
                                (double) skipped /
                                        total
                        ) * 100;
            }

            questionDTO.setCorrectPercentage(
                    round(correctPercentage)
            );

            questionDTO.setIncorrectPercentage(
                    round(incorrectPercentage)
            );

            questionDTO.setSkippedPercentage(
                    round(skippedPercentage)
            );

            // =================================================
            // DIFFICULTY
            // =================================================

            if (correctPercentage >= 75) {

                questionDTO.setDifficulty(
                        "Easy"
                );

            } else if (correctPercentage >= 45) {

                questionDTO.setDifficulty(
                        "Medium"
                );

            } else {

                questionDTO.setDifficulty(
                        "Hard"
                );
            }

            dto.getQuestions()
                    .add(questionDTO);

            // =================================================
            // OVERALL TOTALS
            // =================================================

            totalCorrect += correct;

            totalIncorrect += incorrect;

            totalSkipped += skipped;
        }

        // =====================================================
        // OVERALL QUESTION TOTALS
        // =====================================================

        dto.setTotalCorrect(
                totalCorrect
        );

        dto.setTotalIncorrect(
                totalIncorrect
        );

        dto.setTotalUnattempted(
                totalSkipped
        );

        int totalAnswers =
                totalCorrect +
                        totalIncorrect +
                        totalSkipped;

        // =====================================================
        // OVERALL QUESTION PERCENTAGES
        // =====================================================

        if (totalAnswers > 0) {

            dto.setCorrectPercentage(
                    round(
                            (
                                    (double) totalCorrect /
                                            totalAnswers
                            ) * 100
                    )
            );

            dto.setIncorrectPercentage(
                    round(
                            (
                                    (double) totalIncorrect /
                                            totalAnswers
                            ) * 100
                    )
            );

            dto.setUnattemptedPercentage(
                    round(
                            (
                                    (double) totalSkipped /
                                            totalAnswers
                            ) * 100
                    )
            );

        } else {

            dto.setCorrectPercentage(0);

            dto.setIncorrectPercentage(0);

            dto.setUnattemptedPercentage(0);
        }
    }

    // =========================================================
    // FIND ANSWER
    // =========================================================

    private StudentAnswer findAnswerForQuestion(
            TestAttempt attempt,
            Long questionId
    ) {

        if (attempt.getAnswers() == null) {
            return null;
        }

        for (StudentAnswer answer :
                attempt.getAnswers()) {

            if (answer.getQuestion() != null
                    && answer.getQuestion()
                    .getId()
                    .equals(questionId)) {

                return answer;
            }
        }

        return null;
    }

    // =========================================================
    // ROUND
    // =========================================================

    private double round(double value) {

        return Math.round(
                value * 100.0
        ) / 100.0;
    }
}