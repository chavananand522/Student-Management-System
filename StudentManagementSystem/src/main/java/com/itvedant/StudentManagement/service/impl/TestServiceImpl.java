package com.itvedant.StudentManagement.service.impl;

import com.itvedant.StudentManagement.model.Question;
import com.itvedant.StudentManagement.model.Test;
import com.itvedant.StudentManagement.reposatory.TestRepository;
import com.itvedant.StudentManagement.services.TestService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TestServiceImpl implements TestService {

    private final TestRepository testRepository;

    public TestServiceImpl(TestRepository testRepository) {
        this.testRepository = testRepository;
    }

    @Override
    @Transactional
    public Test saveTest(Test test) {

        if (test.getQuestions() != null) {

            for (Question question : test.getQuestions()) {
                question.setTest(test);
            }
        }

        return testRepository.save(test);
    }

    @Override
    @Transactional(readOnly = true)
    public Test getTestById(Long id) {

        return testRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Test> getAllTests() {

        return testRepository.findAll();
    }

    @Override
    @Transactional
    public void deleteTest(Long id) {

        testRepository.deleteById(id);
    }
}