package com.itvedant.StudentManagement.services;

import com.itvedant.StudentManagement.dto.AttendanceDTO;
import com.itvedant.StudentManagement.model.Attendance;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceService {

    Attendance saveAttendance(
            AttendanceDTO dto
    );

    List<Attendance> getAttendanceByDate(
            LocalDate date
    );

    List<Attendance> getAttendanceByCourseAndDate(
            Long courseId,
            LocalDate date
    );

    List<Attendance> getStudentAttendance(
            Long studentId
    );

    double getStudentAttendancePercentage(
            Long studentId
    );

    long getPresentCount(
            LocalDate date
    );

    long getAbsentCount(
            LocalDate date
    );

    long getLateCount(
            LocalDate date
    );

    Attendance updateAttendance(
            Long id,
            AttendanceDTO dto
    );

    void deleteAttendance(
            Long id
    );

    // =====================================================
    // NEW: earliest attendance date (for cumulative gauge)
    // =====================================================

    LocalDate getEarliestAttendanceDate();
}