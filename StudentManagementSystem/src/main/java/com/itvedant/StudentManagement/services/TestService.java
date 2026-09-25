package com.itvedant.StudentManagement.services;

import java.util.List;

import com.itvedant.StudentManagement.model.Test;

public interface TestService {

    Test save(Test test);

    Test getById(Long id);

    List<Test> getAll();

    Test update(Long id, Test test);

    void delete(Long id);

    long getQuestionCount(Long testId);

    long getTotalQuestionCount();
}