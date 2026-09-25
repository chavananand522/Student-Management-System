package com.itvedant.StudentManagement.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itvedant.StudentManagement.model.Question;
import com.itvedant.StudentManagement.model.Test;
import com.itvedant.StudentManagement.reposatory.QuestionRepository;
import com.itvedant.StudentManagement.reposatory.TestRepository;
import com.itvedant.StudentManagement.services.TestService;

@Service
public class TestServiceImpl implements TestService {

    private final TestRepository testRepository;
    private final QuestionRepository questionRepository;

    public TestServiceImpl(
            TestRepository testRepository,
            QuestionRepository questionRepository) {

        this.testRepository = testRepository;
        this.questionRepository = questionRepository;
    }

    @Override
    @Transactional
    public Test save(Test test) {

        if (test.getQuestions() != null) {

            for (Question question : test.getQuestions()) {

                question.setTest(test);

                if (question.getMarks() == null || question.getMarks() <= 0) {
                    question.setMarks(1);
                }
            }
        }

        return testRepository.save(test);
    }

    @Override
    @Transactional(readOnly = true)
    public Test getById(Long id) {

        Test test = testRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Test not found with id: " + id
                        )
                );

        // Force loading of questions
        if (test.getQuestions() != null) {
            test.getQuestions().size();
        }

        return test;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Test> getAll() {

        List<Test> tests = testRepository.findAll();

        // Force loading of questions
        tests.forEach(test -> {
            if (test.getQuestions() != null) {
                test.getQuestions().size();
            }
        });

        return tests;
    }

    @Override
    @Transactional
    public Test update(Long id, Test updatedTest) {

        Test existingTest = testRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Test not found with id: " + id
                        )
                );

        existingTest.setTestName(updatedTest.getTestName());
        existingTest.setCourse(updatedTest.getCourse());
        existingTest.setSubject(updatedTest.getSubject());
        existingTest.setChapter(updatedTest.getChapter());
        existingTest.setDuration(updatedTest.getDuration());
        existingTest.setTotalMarks(updatedTest.getTotalMarks());
        existingTest.setPassingMarks(updatedTest.getPassingMarks());

        existingTest.setShuffleQuestions(
                Boolean.TRUE.equals(
                        updatedTest.getShuffleQuestions()
                )
        );

        existingTest.setShuffleOptions(
                Boolean.TRUE.equals(
                        updatedTest.getShuffleOptions()
                )
        );

        existingTest.setShowResultImmediately(
                Boolean.TRUE.equals(
                        updatedTest.getShowResultImmediately()
                )
        );

        existingTest.setAllowTestRetake(
                Boolean.TRUE.equals(
                        updatedTest.getAllowTestRetake()
                )
        );

        existingTest.setNumberOfAttempts(
                updatedTest.getNumberOfAttempts() == null
                        ? 1
                        : updatedTest.getNumberOfAttempts()
        );

        existingTest.setStatus(updatedTest.getStatus());

        return testRepository.save(existingTest);
    }

    @Override
    @Transactional
    public void delete(Long id) {

        if (!testRepository.existsById(id)) {
            throw new RuntimeException(
                    "Test not found with id: " + id
            );
        }

        testRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public long getQuestionCount(Long testId) {

        return questionRepository.countByTestId(testId);
    }

    @Override
    @Transactional(readOnly = true)
    public long getTotalQuestionCount() {

        return testRepository.findAll()
                .stream()
                .mapToLong(test ->
                        questionRepository.countByTestId(test.getId())
                )
                .sum();
    }
}