package com.itvedant.StudentManagement.reposatory;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.itvedant.StudentManagement.model.Enrollment;

public interface EnrollmentRepository
        extends JpaRepository<Enrollment, Long> {

    boolean existsByStudentIdAndCourseId(
            Long StudentId,
            Long CourseIds);

    @Query("""
            select count(distinct e.student.id)
            from Enrollment e
            where e.enrolledDate between :startDate and :endDate
            """)
    long countDistinctStudentByEnrolledDateBetween(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}