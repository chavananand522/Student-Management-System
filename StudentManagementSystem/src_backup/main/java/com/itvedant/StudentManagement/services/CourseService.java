package com.itvedant.StudentManagement.services;

import java.util.List;

import org.springframework.data.domain.Page;

import com.itvedant.StudentManagement.dto.CourseDTO;

public interface CourseService {
	
	
	List<CourseDTO> getAllCourses(); 

	CourseDTO createCourse(CourseDTO courseDTO);
	
	 boolean existsByCourseCode(String courseCode);
	 
	 boolean existsByCourseCodeAndIdNot(String courseCode, Long id);
	 
	 Page<CourseDTO>getCourses(int page, int size);
	 
	 CourseDTO getCourseById(Long id);
	 
	 CourseDTO updateCourse(Long id,CourseDTO courseDTO);
	 
}
