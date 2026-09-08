package com.itvedant.StudentManagement.reposatory;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.itvedant.StudentManagement.dto.EnrollmentSummeryDTO;
import com.itvedant.StudentManagement.model.Students;

public interface StudentRepositiry extends JpaRepository<Students, Long> {

	boolean existsByEmailIgnoreCase(String email);

	boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);

	Page<Students> findByActiveTrue(Pageable pageable);

	List<Students> findByActiveTrue();

	@Query(value = """
			select new com.itvedant.StudentManagement.dto.EnrollmentSummeryDTO(
				s.id,
				concat(s.firstName, ' ', s.lastName),
				s.email,
				count(e.id),
				sum(c.fee)
			)
			from Students s
			join s.enrollments e
			join e.course c
			group by s.id, s.firstName, s.lastName, s.email
			""", countQuery = """
			select count(distinct s.id)
			from Students s
			join s.enrollments e
			""")
	Page<EnrollmentSummeryDTO> findEnrolledStudentIds(Pageable pageable);

	@Query("""
			select distinct s
			from Students s
			join fetch s.enrollments e
			join fetch e.course c
			where s.id = :id
			""")
	Optional<Students> findEnrolledStudentCourseDetails(@Param("id") Long id);

	// Step 1: get just the IDs of recently enrolled students, correctly
	// paginated and sorted at the SQL level (most recent enrollment first).
	@Query(value = """
			select s.id
			from Students s
			join s.enrollments e
			group by s.id
			order by max(e.enrolledDate) desc
			""", countQuery = """
			select count(distinct s.id)
			from Students s
			join s.enrollments e
			""")
	Page<Long> findRecentlyEnrolledStudentIds(Pageable pageable);

	// Step 2: given a page of IDs, fetch the full student + enrollment +
	// course graph in one query (join fetch is safe here since there's
	// no pagination applied at this level).
	@Query("""
			select distinct s
			from Students s
			join fetch s.enrollments e
			join fetch e.course c
			where s.id in :ids
			""")
	List<Students> findStudentsWithCoursesByIds(@Param("ids") List<Long> ids);
}