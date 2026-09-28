package com.itvedant.StudentManagement.reposatory;

import com.itvedant.StudentManagement.model.TestAttempt;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface TestAttemptRepository extends JpaRepository<TestAttempt, Long> {

    // ---------------------------------------------------------
    // Existing methods
    // ---------------------------------------------------------

    List<TestAttempt> findByTestIdAndStudentUsername(Long testId, String studentUsername);

    List<TestAttempt> findByStudentUsername(String studentUsername);

    // ---------------------------------------------------------
    // Needed by LeadershipController
    // ---------------------------------------------------------

    List<TestAttempt> findByTestId(Long testId);

    // ---------------------------------------------------------
    // Highest obtained marks for a specific test
    // ---------------------------------------------------------

    @Query("SELECT MAX(t.obtainedMarks) FROM TestAttempt t WHERE t.test.id = :testId AND UPPER(t.status) = 'SUBMITTED'")
    Integer findMaxObtainedMarksByTestId(@Param("testId") Long testId);

    // ---------------------------------------------------------
    // Top-scoring attempt for a test
    // ---------------------------------------------------------

    List<TestAttempt> findFirstByTestIdAndStatusIgnoreCaseOrderByObtainedMarksDesc(Long testId, String status);

    // ---------------------------------------------------------
    // Topper username(s) for a test
    // ---------------------------------------------------------

    @Query("SELECT t.studentUsername FROM TestAttempt t WHERE t.test.id = :testId AND UPPER(t.status) = 'SUBMITTED' ORDER BY t.obtainedMarks DESC")
    List<String> findTopperNameByTestId(@Param("testId") Long testId, Pageable pageable);

    // ---------------------------------------------------------
    // Attempt counts per test for a single student
    // Used by StudentPortalController.tests() to avoid N+1
    // Returns rows of [testId (Long), count (Long)]
    // ---------------------------------------------------------

    @Query("""
        SELECT t.test.id, COUNT(t)
        FROM TestAttempt t
        WHERE t.studentUsername = :username
        GROUP BY t.test.id
        """)
    List<Object[]> countAttemptsByStudentGroupedByTest(
            @Param("username") String username);
}