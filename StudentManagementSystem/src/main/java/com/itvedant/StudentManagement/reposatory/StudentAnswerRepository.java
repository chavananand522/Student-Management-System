package com.itvedant.StudentManagement.reposatory;

import com.itvedant.StudentManagement.model.StudentAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentAnswerRepository
        extends JpaRepository<StudentAnswer, Long> {

}