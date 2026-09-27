package com.itvedant.StudentManagement.reposatory;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.itvedant.StudentManagement.model.Enrollment;

public interface EnrollmentRepository
        extends JpaRepository<Enrollment, Long> {

    // =========================================================
    // EXISTING
    // =========================================================

    boolean existsByStudentIdAndCourseId(
            Long studentId,
            Long courseId);

    @Query("""
            select count(distinct e.student.id)
            from Enrollment e
            where e.enrolledDate between :startDate and :endDate
            """)
    long countDistinctStudentByEnrolledDateBetween(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    // =========================================================
    // NEW — used by StudentAiController and StudentPortalController
    // =========================================================

    /**
     * Fetch all enrollments for a given student id.
     */
    List<Enrollment> findByStudentId(Long studentId);

    /**
     * Fallback lookup by email through the Students relation.
     * Only works if Enrollment has a @ManyToOne Students student field.
     */
    @Query("""
           SELECT e FROM Enrollment e
           WHERE LOWER(e.student.email) = LOWER(:email)
           """)
    List<Enrollment> findByStudentEmailIgnoreCase(@Param("email") String email);
}