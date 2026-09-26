
package com.itvedant.StudentManagement.reposatory;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.itvedant.StudentManagement.model.FeePayment;

public interface FeePaymentRepository extends JpaRepository<FeePayment, Long> {

	List<FeePayment> findByStudentIdOrderByPaymentDateDesc(long studentId);

	List<FeePayment> findByStudentIdAndCourseIdOrderByPaymentDateDesc(long studentId, long courseId);

	@Query("""
			SELECT COALESCE(SUM(f.amount), 0)
			FROM FeePayment f
			WHERE f.student.id = :studentId
			AND f.course.id = :courseId
			""")
	double getPaidByStudentAndCourse(@Param("studentId") long studentId, @Param("courseId") long courseId);

	@Query("""
			SELECT COALESCE(SUM(f.amount), 0)
			FROM FeePayment f
			WHERE f.student.id = :studentId
			""")
	double getTotalPaidByStudent(@Param("studentId") long studentId);
}