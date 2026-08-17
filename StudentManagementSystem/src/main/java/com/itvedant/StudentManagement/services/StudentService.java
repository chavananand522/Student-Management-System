package com.itvedant.StudentManagement.services;

import org.springframework.data.domain.Page;

import com.itvedant.StudentManagement.dto.StudentDTO;

public interface StudentService {
	
	boolean existsByEmailIgnoreCase(String email);
	
	StudentDTO createStudent(StudentDTO studentDTO);
	
	 Page<StudentDTO>getStudents(int page, int size);
}
