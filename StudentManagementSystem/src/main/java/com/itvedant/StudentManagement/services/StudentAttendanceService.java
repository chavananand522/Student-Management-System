package com.itvedant.StudentManagement.services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;

import com.itvedant.StudentManagement.dto.StudentAttendanceDTO;
import com.itvedant.StudentManagement.model.Attendance;

public interface StudentAttendanceService {

    /**
     * Paginated + filtered attendance for a student.
     */
    Page<Attendance> getStudentAttendance(
            Long studentId,
            Long courseId,
            LocalDate startDate,
            LocalDate endDate,
            int page,
            int size);

    /**
     * Subject-wise summary cards.
     */
    List<StudentAttendanceDTO> getSubjectWiseSummary(Long studentId);

    /**
     * Overall attendance percentage across all courses.
     * WEIGHTED: PRESENT = 1.0, LATE = 0.5, ABSENT = 0.0
     */
    double getOverallPercentage(Long studentId);

    /**
     * Total / present / absent / late counts across all courses.
     */
    long getTotalCount(Long studentId);
    long getPresentCount(Long studentId);
    long getAbsentCount(Long studentId);
    long getLateCount(Long studentId);
}