package com.itvedant.StudentManagement.services;

import com.itvedant.StudentManagement.model.Test;
import java.util.List;

public interface TestService {

    Test saveTest(Test test);

    Test getTestById(Long id);

    List<Test> getAllTests();
}