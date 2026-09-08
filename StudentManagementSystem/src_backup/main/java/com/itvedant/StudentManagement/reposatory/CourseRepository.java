package com.itvedant.StudentManagement.reposatory;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import com.itvedant.StudentManagement.model.Courses;

public interface CourseRepository extends JpaRepository<Courses,Long>{
	
	boolean existsByCourseCodeIgnoreCase(String courseCode);
	
	boolean existsByCourseCodeIgnoreCaseAndIdNot(String courseCode, Long id);
	
	Page<Courses>findByActiveTrue(Pageable pageable);
	
	List<Courses>findByActiveTrue(Sort sort);

}
