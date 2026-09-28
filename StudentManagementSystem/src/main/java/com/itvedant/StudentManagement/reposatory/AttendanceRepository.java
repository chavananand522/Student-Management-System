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

	// ---------------------------------------------------------
	// Single-row lookups
	// ---------------------------------------------------------

	Optional<Attendance> findByStudentIdAndCourseIdAndAttendanceDate(Long studentId, Long courseId,
			LocalDate attendanceDate);

	List<Attendance> findByAttendanceDate(LocalDate attendanceDate);

	List<Attendance> findByCourseIdAndAttendanceDate(Long courseId, LocalDate attendanceDate);

	List<Attendance> findByStudentIdOrderByAttendanceDateDesc(Long studentId);

	// ---------------------------------------------------------
	// Counts
	// ---------------------------------------------------------

	long countByAttendanceDateAndStatus(LocalDate attendanceDate, String status);

	long countByStudentIdAndStatus(Long studentId, String status);

	long countByStudentId(Long studentId);

	long countByStudentIdAndCourseId(Long studentId, Long courseId);

	long countByStudentIdAndCourseIdAndStatus(Long studentId, Long courseId, String status);

	// ---------------------------------------------------------
	// Pagination
	// ---------------------------------------------------------

	Page<Attendance> findByStudentIdOrderByAttendanceDateDesc(Long studentId, Pageable pageable);

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

	// ---------------------------------------------------------
	// Per-subject summary
	// ---------------------------------------------------------

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

	@Query("""
			SELECT
			    COUNT(a),
			    COALESCE(SUM(CASE WHEN a.status = 'PRESENT' THEN 1 ELSE 0 END), 0),
			    COALESCE(SUM(CASE WHEN a.status = 'ABSENT'  THEN 1 ELSE 0 END), 0),
			    COALESCE(SUM(CASE WHEN a.status = 'LATE'    THEN 1 ELSE 0 END), 0)
			FROM Attendance a
			WHERE a.student.id = :studentId
			""")
	Object[] findAttendanceSummaryForStudent(@Param("studentId") Long studentId);
}