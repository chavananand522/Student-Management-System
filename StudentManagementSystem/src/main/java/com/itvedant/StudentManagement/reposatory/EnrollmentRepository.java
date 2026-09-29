package com.itvedant.StudentManagement.reposatory;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.itvedant.StudentManagement.model.Enrollment;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    boolean existsByStudentIdAndCourseId(
            Long studentId,
            Long courseId
    );

    @Query("""
        SELECT COUNT(DISTINCT e.student.id)
        FROM Enrollment e
        WHERE e.enrolledDate BETWEEN :startDate AND :endDate
        """)
    long countDistinctStudentByEnrolledDateBetween(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    List<Enrollment> findByStudentId(Long studentId);

    @Query("""
        SELECT e.course.id
        FROM Enrollment e
        WHERE e.student.id = :studentId
        """)
    List<Long> findCourseIdsByStudentId(
            @Param("studentId") Long studentId
    );

    @Query("""
        SELECT e
        FROM Enrollment e
        WHERE LOWER(e.student.email) = LOWER(:email)
        """)
    List<Enrollment> findByStudentEmailIgnoreCase(
            @Param("email") String email
    );

    @Query("""
        SELECT e.course.courseName, COUNT(DISTINCT e.student.id)
        FROM Enrollment e
        GROUP BY e.course.courseName
        ORDER BY COUNT(DISTINCT e.student.id) DESC
        """)
    List<Object[]> getStudentsByCourse();

    @Query("""
        SELECT MONTH(e.enrolledDate), COUNT(DISTINCT e.student.id)
        FROM Enrollment e
        WHERE YEAR(e.enrolledDate) = :year
        GROUP BY MONTH(e.enrolledDate)
        ORDER BY MONTH(e.enrolledDate)
        """)
    List<Object[]> getMonthlyStudentEnrollment(
            @Param("year") int year
    );

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        DELETE FROM Enrollment e
        WHERE e.student.id = :studentId
        AND e.course.id = :courseId
        """)
    void deleteByStudentIdAndCourseId(
            @Param("studentId") Long studentId,
            @Param("courseId") Long courseId
    );

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        DELETE FROM Enrollment e
        WHERE e.student.id = :studentId
        """)
    void deleteAllByStudentId(
            @Param("studentId") Long studentId
    );

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(
        value = """
            DELETE FROM enrollment
            WHERE course_id = :courseId
            """,
        nativeQuery = true
    )
    void deleteAllByCourseId(
            @Param("courseId") Long courseId
    );
}