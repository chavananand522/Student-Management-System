package com.itvedant.StudentManagement.services;

import java.util.List;

import com.itvedant.StudentManagement.model.Study;

public interface StudyService {

	List<Study> getAllSubjects();

	Study getSubjectByName(String subject);

	Study saveSubject(Study study);

	void deleteSubject(Long id);
}