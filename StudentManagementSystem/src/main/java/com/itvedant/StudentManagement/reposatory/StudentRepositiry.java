package com.itvedant.StudentManagement.reposatory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.itvedant.StudentManagement.model.Students;

public interface StudentRepositiry extends JpaRepository<Students, Long> {

	boolean existsByEmailIgnoreCase(String email);
	
	boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);
	
	Page<Students>findByActiveTrue(Pageable pageable);
	
	
}
