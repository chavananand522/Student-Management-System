
package com.itvedant.StudentManagement.services;

import java.util.List;

import org.springframework.data.domain.Page;

import com.itvedant.StudentManagement.dto.EnrollmentDTO;
import com.itvedant.StudentManagement.dto.EnrollmentSummeryDTO;

public interface EnrollmentService {

    void enrollStudentToCourses(EnrollmentDTO enrollmentDTO);

    Page<EnrollmentSummeryDTO> getEnrolledStudents(
            int page,
            int size);

    EnrollmentSummeryDTO findEnrolledStudentCourseDetails(
            Long studentId);

    List<EnrollmentSummeryDTO> getRecentlyEnrolledStudents(
            int page,
            int size);
}

