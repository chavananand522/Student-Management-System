package com.itvedant.StudentManagement.reposatory;

import com.itvedant.StudentManagement.model.Attendance;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository
        extends JpaRepository<Attendance, Long> {

    Optional<Attendance>
    findByStudentIdAndCourseIdAndAttendanceDate(
            Long studentId,
            Long courseId,
            LocalDate attendanceDate
    );

    List<Attendance> findByAttendanceDate(
            LocalDate attendanceDate
    );

    List<Attendance> findByCourseIdAndAttendanceDate(
            Long courseId,
            LocalDate attendanceDate
    );

    List<Attendance>
    findByStudentIdOrderByAttendanceDateDesc(
            Long studentId
    );

    long countByAttendanceDateAndStatus(
            LocalDate attendanceDate,
            String status
    );

    long countByStudentIdAndStatus(
            Long studentId,
            String status
    );

    long countByStudentId(
            Long studentId
    );
}