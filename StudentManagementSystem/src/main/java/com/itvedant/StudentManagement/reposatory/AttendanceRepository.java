package com.itvedant.StudentManagement.reposatory;

import com.itvedant.StudentManagement.dto.StudentAttendanceDTO;
import com.itvedant.StudentManagement.model.Attendance;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

	// ============================================================
	// EXISTING METHODS (used by admin side)
	// ============================================================

	Optional<Attendance> findByStudentIdAndCourseIdAndAttendanceDate(Long studentId, Long courseId,
			LocalDate attendanceDate);

	List<Attendance> findByAttendanceDate(LocalDate attendanceDate);

	List<Attendance> findByCourseIdAndAttendanceDate(Long courseId, LocalDate attendanceDate);

	List<Attendance> findByStudentIdOrderByAttendanceDateDesc(Long studentId);

	long countByAttendanceDateAndStatus(LocalDate attendanceDate, String status);

	long countByStudentIdAndStatus(Long studentId, String status);

	long countByStudentId(Long studentId);

	// ============================================================
	// NEW METHODS FOR STUDENT PORTAL
	// ============================================================

	/**
	 * Paginated attendance for a student, newest first.
	 */
	Page<Attendance> findByStudentIdOrderByAttendanceDateDesc(Long studentId, Pageable pageable);

	/**
	 * Filtered attendance for a student (optional course + date range).
	 */
	@Query("""
			SELECT a FROM Attendance a
			WHERE a.student.id = :studentId
			  AND (:courseId IS NULL OR a.course.id = :courseId)
			  AND (:startDate IS NULL OR a.attendanceDate >= :startDate)
			  AND (:endDate IS NULL OR a.attendanceDate <= :endDate)
			ORDER BY a.attendanceDate DESC
			""")
	Page<Attendance> findStudentAttendanceFiltered(@Param("studentId") Long studentId, @Param("courseId") Long courseId,
			@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, Pageable pageable);

	/**
	 * Count attendance records for a student in a specific course.
	 */
	long countByStudentIdAndCourseId(Long studentId, Long courseId);

	/**
	 * Count by student + course + status (used for subject-wise breakdown).
	 */
	long countByStudentIdAndCourseIdAndStatus(Long studentId, Long courseId, String status);

	/**
	 * Subject-wise summary — one row per course the student has records in. PRESENT
	 * + LATE are counted as attended.
	 */
	@Query("""
			SELECT new com.itvedant.StudentManagement.dto.StudentAttendanceDTO(
			    c.id,
			    c.courseName,
			    c.courseCode,
			    COUNT(a.id),
			    COALESCE(SUM(CASE WHEN a.status = 'PRESENT' THEN 1 ELSE 0 END), 0),
			    COALESCE(SUM(CASE WHEN a.status = 'ABSENT'  THEN 1 ELSE 0 END), 0),
			    COALESCE(SUM(CASE WHEN a.status = 'LATE'    THEN 1 ELSE 0 END), 0)
			)
			FROM Attendance a
			JOIN a.course c
			WHERE a.student.id = :studentId
			GROUP BY c.id, c.courseName, c.courseCode
			ORDER BY c.courseName
			""")
	List<StudentAttendanceDTO> findSubjectWiseSummary(@Param("studentId") Long studentId);
}