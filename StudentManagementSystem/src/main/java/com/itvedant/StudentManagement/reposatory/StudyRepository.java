package com.itvedant.StudentManagement.reposatory;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.itvedant.StudentManagement.model.Study;

public interface StudyRepository extends JpaRepository<Study, Long> {

    Optional<Study> findBySubjectIgnoreCase(String subject);
}