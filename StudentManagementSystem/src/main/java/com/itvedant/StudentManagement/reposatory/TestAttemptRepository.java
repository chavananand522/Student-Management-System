package com.itvedant.StudentManagement.reposatory;

import com.itvedant.StudentManagement.model.TestAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TestAttemptRepository extends JpaRepository<TestAttempt, Long> {

    List<TestAttempt> findByTestIdAndStudentUsername(
            Long testId,
            String studentUsername
    );

    List<TestAttempt> findByStudentUsername(
            String studentUsername
    );
}