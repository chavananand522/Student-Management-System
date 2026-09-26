package com.itvedant.StudentManagement.reposatory;

import com.itvedant.StudentManagement.model.TestAttempt;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestAttemptRepository extends JpaRepository<TestAttempt, Long> {

	List<TestAttempt> findByTestIdAndStudentUsername(Long testId, String studentUsername);

	List<TestAttempt> findByStudentUsername(String studentUsername);

	// Query to retrieve highest obtained marks for a specific test among submitted attempts
	@Query("SELECT MAX(t.obtainedMarks) FROM TestAttempt t WHERE t.test.id = :testId AND UPPER(t.status) = 'SUBMITTED'")
	Integer findMaxObtainedMarksByTestId(@Param("testId") Long testId);

	// Option 1: Native Spring Data JPA Query Method (Recommended)
	List<TestAttempt> findFirstByTestIdAndStatusIgnoreCaseOrderByObtainedMarksDesc(Long testId, String status);

	// Option 2: Using Pageable with JPQL Query
	@Query("SELECT t.studentUsername FROM TestAttempt t WHERE t.test.id = :testId AND UPPER(t.status) = 'SUBMITTED' ORDER BY t.obtainedMarks DESC")
	List<String> findTopperNameByTestId(@Param("testId") Long testId, Pageable pageable);
}