package com.itvedant.StudentManagement.reposatory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.itvedant.StudentManagement.model.Question;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    long countByTestId(Long testId);
}