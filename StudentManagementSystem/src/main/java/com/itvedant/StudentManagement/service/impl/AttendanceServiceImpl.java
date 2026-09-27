package com.itvedant.StudentManagement.service.impl;

import com.itvedant.StudentManagement.dto.AttendanceDTO;
import com.itvedant.StudentManagement.model.Attendance;
import com.itvedant.StudentManagement.model.Students;
import com.itvedant.StudentManagement.model.Courses;
import com.itvedant.StudentManagement.reposatory.AttendanceRepository;
import com.itvedant.StudentManagement.services.AttendanceService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class AttendanceServiceImpl
        implements AttendanceService {

    private final AttendanceRepository attendanceRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public AttendanceServiceImpl(
            AttendanceRepository attendanceRepository
    ) {
        this.attendanceRepository = attendanceRepository;
    }

    // =====================================================
    // SAVE ATTENDANCE
    // =====================================================

    @Override
    public Attendance saveAttendance(
            AttendanceDTO dto
    ) {

        if (dto.getStudentId() == null) {
            throw new RuntimeException(
                    "Student ID is required"
            );
        }

        if (dto.getCourseId() == null) {
            throw new RuntimeException(
                    "Course ID is required"
            );
        }

        if (dto.getAttendanceDate() == null) {
            throw new RuntimeException(
                    "Attendance date is required"
            );
        }

        if (dto.getStatus() == null ||
                dto.getStatus().trim().isEmpty()) {

            throw new RuntimeException(
                    "Attendance status is required"
            );
        }

        Students student =
                entityManager.find(
                        Students.class,
                        dto.getStudentId()
                );

        if (student == null) {
            throw new RuntimeException(
                    "Student not found"
            );
        }

        Courses course =
                entityManager.find(
                        Courses.class,
                        dto.getCourseId()
                );

        if (course == null) {
            throw new RuntimeException(
                    "Course not found"
            );
        }

        Attendance attendance =
                attendanceRepository
                        .findByStudentIdAndCourseIdAndAttendanceDate(
                                dto.getStudentId(),
                                dto.getCourseId(),
                                dto.getAttendanceDate()
                        )
                        .orElse(new Attendance());

        attendance.setStudent(student);

        attendance.setCourse(course);

        attendance.setAttendanceDate(
                dto.getAttendanceDate()
        );

        attendance.setStatus(
                dto.getStatus().toUpperCase()
        );

        attendance.setRemarks(
                dto.getRemarks()
        );

        return attendanceRepository.save(
                attendance
        );
    }

    // =====================================================
    // GET BY DATE
    // =====================================================

    @Override
    @Transactional(readOnly = true)
    public List<Attendance> getAttendanceByDate(
            LocalDate date
    ) {

        return attendanceRepository
                .findByAttendanceDate(date);
    }

    // =====================================================
    // GET COURSE + DATE
    // =====================================================

    @Override
    @Transactional(readOnly = true)
    public List<Attendance> getAttendanceByCourseAndDate(
            Long courseId,
            LocalDate date
    ) {

        return attendanceRepository
                .findByCourseIdAndAttendanceDate(
                        courseId,
                        date
                );
    }

    // =====================================================
    // STUDENT ATTENDANCE
    // =====================================================

    @Override
    @Transactional(readOnly = true)
    public List<Attendance> getStudentAttendance(
            Long studentId
    ) {

        return attendanceRepository
                .findByStudentIdOrderByAttendanceDateDesc(
                        studentId
                );
    }

    // =====================================================
    // STUDENT ATTENDANCE %
    // =====================================================

    @Override
    @Transactional(readOnly = true)
    public double getStudentAttendancePercentage(
            Long studentId
    ) {

        long total =
                attendanceRepository
                        .countByStudentId(studentId);

        if (total == 0) {
            return 0.0;
        }

        long present =
                attendanceRepository
                        .countByStudentIdAndStatus(
                                studentId,
                                "PRESENT"
                        );

        return Math.round(
                ((double) present / total) * 10000
        ) / 100.0;
    }

    // =====================================================
    // PRESENT
    // =====================================================

    @Override
    @Transactional(readOnly = true)
    public long getPresentCount(
            LocalDate date
    ) {

        return attendanceRepository
                .countByAttendanceDateAndStatus(
                        date,
                        "PRESENT"
                );
    }

    // =====================================================
    // ABSENT
    // =====================================================

    @Override
    @Transactional(readOnly = true)
    public long getAbsentCount(
            LocalDate date
    ) {

        return attendanceRepository
                .countByAttendanceDateAndStatus(
                        date,
                        "ABSENT"
                );
    }

    // =====================================================
    // LATE
    // =====================================================

    @Override
    @Transactional(readOnly = true)
    public long getLateCount(
            LocalDate date
    ) {

        return attendanceRepository
                .countByAttendanceDateAndStatus(
                        date,
                        "LATE"
                );
    }

    // =====================================================
    // UPDATE
    // =====================================================

    @Override
    public Attendance updateAttendance(
            Long id,
            AttendanceDTO dto
    ) {

        Attendance attendance =
                attendanceRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Attendance record not found"
                                )
                        );

        if (dto.getStudentId() != null) {

            Students student =
                    entityManager.find(
                            Students.class,
                            dto.getStudentId()
                    );

            if (student == null) {
                throw new RuntimeException(
                        "Student not found"
                );
            }

            attendance.setStudent(student);
        }

        if (dto.getCourseId() != null) {

            Courses course =
                    entityManager.find(
                            Courses.class,
                            dto.getCourseId()
                    );

            if (course == null) {
                throw new RuntimeException(
                        "Course not found"
                );
            }

            attendance.setCourse(course);
        }

        if (dto.getAttendanceDate() != null) {

            attendance.setAttendanceDate(
                    dto.getAttendanceDate()
            );
        }

        if (dto.getStatus() != null &&
                !dto.getStatus().trim().isEmpty()) {

            attendance.setStatus(
                    dto.getStatus().toUpperCase()
            );
        }

        if (dto.getRemarks() != null) {

            attendance.setRemarks(
                    dto.getRemarks()
            );
        }

        return attendanceRepository.save(
                attendance
        );
    }

    // =====================================================
    // DELETE
    // =====================================================

    @Override
    public void deleteAttendance(
            Long id
    ) {

        if (!attendanceRepository.existsById(id)) {

            throw new RuntimeException(
                    "Attendance record not found"
            );
        }

        attendanceRepository.deleteById(id);
    }

    // =====================================================
    // NEW: EARLIEST ATTENDANCE DATE
    // Used by the cumulative gauge to know where the range starts.
    // Returns null if no attendance records exist yet.
    // =====================================================

    @Override
    @Transactional(readOnly = true)
    public LocalDate getEarliestAttendanceDate() {

        try {

            return entityManager
                    .createQuery(
                            "SELECT MIN(a.attendanceDate) " +
                            "FROM Attendance a",
                            LocalDate.class
                    )
                    .getSingleResult();

        } catch (Exception ex) {

            return null;
        }
    }
}