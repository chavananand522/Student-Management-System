package com.itvedant.StudentManagement.services;

import org.springframework.data.domain.Page;

import com.itvedant.StudentManagement.dto.StudentDTO;

public interface StudentService {
	
	boolean existsByEmailIgnoreCase(String email);
	
	boolean existsByEmailIgnoreCaseAndIdNot(String email,Long id);
	
	StudentDTO createStudent(StudentDTO studentDTO);
	
	 Page<StudentDTO>getStudents(int page, int size);
	 
	 StudentDTO getStudentById(Long id);
	 
	 StudentDTO updateStudent(Long id,StudentDTO studentDTO);
	 
}
