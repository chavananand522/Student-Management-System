package com.itvedant.StudentManagement.reposatory;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.itvedant.StudentManagement.model.Enrollment;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

	// =========================================================
	// EXISTING
	// =========================================================

	boolean existsByStudentIdAndCourseId(Long studentId, Long courseId);

	// =========================================================
	// STUDENTS ENROLLED THIS MONTH
	// =========================================================

	@Query("""
			SELECT COUNT(DISTINCT e.student.id)
			FROM Enrollment e
			WHERE e.enrolledDate BETWEEN :startDate AND :endDate
			""")
	long countDistinctStudentByEnrolledDateBetween(@Param("startDate") LocalDateTime startDate,
			@Param("endDate") LocalDateTime endDate);

	// =========================================================
	// RECENT STUDENT ENROLLMENTS
	// =========================================================

	/**
	 * Fetch all enrollments for a given student id.
	 */
	List<Enrollment> findByStudentId(Long studentId);

	/**
	 * Fallback lookup by email through the Students relation.
	 */
	@Query("""
			SELECT e
			FROM Enrollment e
			WHERE LOWER(e.student.email) = LOWER(:email)
			""")
	List<Enrollment> findByStudentEmailIgnoreCase(@Param("email") String email);

	// =========================================================
	// STUDENTS BY COURSE
	// =========================================================

	@Query("""
			SELECT
			    e.course.courseName,
			    COUNT(DISTINCT e.student.id)
			FROM Enrollment e
			GROUP BY e.course.courseName
			ORDER BY COUNT(DISTINCT e.student.id) DESC
			""")
	List<Object[]> getStudentsByCourse();

	// =========================================================
	// MONTHLY STUDENT ENROLLMENT
	// =========================================================

	@Query("""
			SELECT
			    MONTH(e.enrolledDate),
			    COUNT(DISTINCT e.student.id)
			FROM Enrollment e
			WHERE YEAR(e.enrolledDate) = :year
			GROUP BY MONTH(e.enrolledDate)
			ORDER BY MONTH(e.enrolledDate)
			""")
	List<Object[]> getMonthlyStudentEnrollment(@Param("year") int year);
}