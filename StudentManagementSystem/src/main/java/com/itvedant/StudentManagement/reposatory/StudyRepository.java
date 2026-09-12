package com.itvedant.StudentManagement.reposatory;

import com.itvedant.StudentManagement.entities.Study;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudyRepository extends JpaRepository<Study, Long> {

    Optional<Study> findBySubjectIgnoreCase(String subject);
}