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

    List<TestAttempt> findByTestIdAndStudentUsername(
            Long testId,
            String studentUsername);

    List<TestAttempt> findByStudentUsername(
            String studentUsername);

    List<TestAttempt> findByTestId(
            Long testId);

    @Query("""
        SELECT MAX(t.obtainedMarks)
        FROM TestAttempt t
        WHERE t.test.id = :testId
        AND UPPER(t.status) = 'SUBMITTED'
        """)
    Integer findMaxObtainedMarksByTestId(
            @Param("testId") Long testId);

    List<TestAttempt> findFirstByTestIdAndStatusIgnoreCaseOrderByObtainedMarksDesc(
            Long testId,
            String status);

    @Query("""
        SELECT t.studentUsername
        FROM TestAttempt t
        WHERE t.test.id = :testId
        AND UPPER(t.status) = 'SUBMITTED'
        ORDER BY t.obtainedMarks DESC
        """)
    List<String> findTopperNameByTestId(
            @Param("testId") Long testId,
            Pageable pageable);

    @Query("""
        SELECT t.test.id, COUNT(t)
        FROM TestAttempt t
        WHERE t.studentUsername = :username
        GROUP BY t.test.id
        """)
    List<Object[]> countAttemptsByStudentGroupedByTest(
            @Param("username") String username);

    @Query("""
        SELECT DISTINCT t
        FROM TestAttempt t
        LEFT JOIN FETCH t.answers a
        LEFT JOIN FETCH a.question q
        WHERE t.test.id = :testId
        AND UPPER(t.status) = 'SUBMITTED'
        """)
    List<TestAttempt> findSubmittedAttemptsWithAnswers(
            @Param("testId") Long testId);
}