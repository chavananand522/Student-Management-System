package com.itvedant.StudentManagement.services;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;

import com.itvedant.StudentManagement.dto.EnrollmentDTO;
import com.itvedant.StudentManagement.dto.EnrollmentSummeryDTO;
import com.itvedant.StudentManagement.model.Enrollment;

public interface EnrollmentService {

    void enrollStudentToCourses(EnrollmentDTO enrollmentDTO);

    Page<EnrollmentSummeryDTO> getEnrolledStudents(int page, int size);

    EnrollmentSummeryDTO findEnrolledStudentCourseDetails(Long studentId);

    List<EnrollmentSummeryDTO> getRecentlyEnrolledStudents(int page, int size);

    Enrollment findEnrollmentById(Long id);

    void updateEnrollmentFee(Long id, BigDecimal newFee);

    void deleteEnrollment(Long id);

    void deleteAllEnrollmentsForStudent(Long studentId);

    List<Long> findEnrolledCourseIdsByStudent(Long studentId);

    void updateStudentCourses(Long studentId, List<Long> courseIds);
}