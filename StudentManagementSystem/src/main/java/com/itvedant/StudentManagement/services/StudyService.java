package com.itvedant.StudentManagement.services;

import com.itvedant.StudentManagement.entities.Study;

import java.util.List;

public interface StudyService {

    List<Study> getAllSubjects();

    Study getSubjectByName(String subject);

    Study saveSubject(Study study);

    void deleteSubject(Long id);
}