package com.itvedant.StudentManagement.service.impl;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.itvedant.StudentManagement.dto.CourseStudentCountDTO;
import com.itvedant.StudentManagement.dto.DashboardStatsDTO;
import com.itvedant.StudentManagement.dto.MonthlyEnrollmentDTO;
import com.itvedant.StudentManagement.model.Enrollment;
import com.itvedant.StudentManagement.reposatory.CourseRepository;
import com.itvedant.StudentManagement.reposatory.EnrollmentRepository;
import com.itvedant.StudentManagement.reposatory.StudentRepositiry;
import com.itvedant.StudentManagement.services.DashboardService;

@Service
public class DashboardServiceImpl
        implements DashboardService {

    private static final Logger log =
            LoggerFactory.getLogger(
                    DashboardServiceImpl.class);

    private final EnrollmentRepository enrollmentRepository;

    private final StudentRepositiry studentRepository;

    private final CourseRepository courseRepository;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public DashboardServiceImpl(
            EnrollmentRepository enrollmentRepository,
            StudentRepositiry studentRepository,
            CourseRepository courseRepository) {

        this.enrollmentRepository =
                enrollmentRepository;

        this.studentRepository =
                studentRepository;

        this.courseRepository =
                courseRepository;
    }

    // =========================================================
    // DASHBOARD STATISTICS
    // =========================================================

    @Override
    public DashboardStatsDTO getDashboardStats() {

        log.info("Loading dashboard statistics");

        // -----------------------------------------------------
        // TOTAL STUDENTS
        // -----------------------------------------------------

        long totalStudents =
                studentRepository.count();

        // -----------------------------------------------------
        // TOTAL COURSES
        // -----------------------------------------------------

        long totalCourses =
                courseRepository.count();

        // -----------------------------------------------------
        // TOP PERFORMING COURSE
        // -----------------------------------------------------

        String topPerformingCourse =
                getTopPerformingCourse();

        // -----------------------------------------------------
        // CURRENT MONTH
        // -----------------------------------------------------

        YearMonth currentMonth =
                YearMonth.now();

        LocalDateTime startDate =
                currentMonth
                        .atDay(1)
                        .atStartOfDay();

        LocalDateTime endDate =
                currentMonth
                        .atEndOfMonth()
                        .atTime(LocalTime.MAX);

        // -----------------------------------------------------
        // STUDENTS ENROLLED THIS MONTH
        // -----------------------------------------------------

        long studentEnrolledThisMonth =
                enrollmentRepository
                        .countDistinctStudentByEnrolledDateBetween(
                                startDate,
                                endDate);

        // -----------------------------------------------------
        // STUDENTS BY COURSE
        // -----------------------------------------------------

        List<CourseStudentCountDTO>
                studentsByCourse =
                getStudentsByCourse();

        // -----------------------------------------------------
        // MONTHLY ENROLLMENT
        // -----------------------------------------------------

        List<MonthlyEnrollmentDTO>
                monthlyEnrollment =
                getMonthlyEnrollment();

        // -----------------------------------------------------
        // CREATE DTO
        // -----------------------------------------------------

        DashboardStatsDTO dashboardStatsDTO =
                new DashboardStatsDTO();

        dashboardStatsDTO.setTotalStudents(
                totalStudents);

        dashboardStatsDTO.setTotalCourses(
                totalCourses);

        dashboardStatsDTO.setTopPerformingCourse(
                topPerformingCourse);

        dashboardStatsDTO.setStudentEntolledThisMonth(
                studentEnrolledThisMonth);

        dashboardStatsDTO.setStudentsByCourse(
                studentsByCourse);

        dashboardStatsDTO.setMonthlyEnrollment(
                monthlyEnrollment);

        log.info(
                "Dashboard loaded: students={}, courses={}, topCourse={}, monthlyStudents={}",
                totalStudents,
                totalCourses,
                topPerformingCourse,
                studentEnrolledThisMonth);

        return dashboardStatsDTO;
    }

    // =========================================================
    // TOP PERFORMING COURSE
    // =========================================================

    private String getTopPerformingCourse() {

        List<Enrollment> enrollments =
                enrollmentRepository.findAll();

        if (enrollments == null ||
                enrollments.isEmpty()) {

            return "N/A";
        }

        return enrollments
                .stream()

                .filter(e ->
                        e.getCourse() != null &&
                        e.getCourse().getCourseName() != null)

                .collect(Collectors.groupingBy(
                        e -> e.getCourse().getCourseName(),
                        Collectors.counting()))

                .entrySet()

                .stream()

                .max(Map.Entry.comparingByValue())

                .map(Map.Entry::getKey)

                .orElse("N/A");
    }

    // =========================================================
    // STUDENTS BY COURSE
    // =========================================================

    private List<CourseStudentCountDTO>
    getStudentsByCourse() {

        List<Object[]> results =
                enrollmentRepository
                        .getStudentsByCourse();

        List<CourseStudentCountDTO> courseData =
                new ArrayList<>();

        if (results == null ||
                results.isEmpty()) {

            return courseData;
        }

        for (Object[] result : results) {

            if (result == null ||
                    result.length < 2) {

                continue;
            }

            String courseName =
                    result[0] != null
                            ? result[0].toString()
                            : "Unknown";

            long studentCount = 0;

            if (result[1] instanceof Number) {

                studentCount =
                        ((Number) result[1])
                                .longValue();

            } else if (result[1] != null) {

                try {

                    studentCount =
                            Long.parseLong(
                                    result[1].toString());

                } catch (NumberFormatException ex) {

                    log.warn(
                            "Unable to convert student count for course {}",
                            courseName);
                }
            }

            courseData.add(
                    new CourseStudentCountDTO(
                            courseName,
                            studentCount));
        }

        return courseData;
    }

    // =========================================================
    // MONTHLY ENROLLMENT
    // =========================================================

    private List<MonthlyEnrollmentDTO>
    getMonthlyEnrollment() {

        int currentYear =
                YearMonth.now().getYear();

        List<Object[]> results =
                enrollmentRepository
                        .getMonthlyStudentEnrollment(
                                currentYear);

        Map<Integer, Long> monthlyData =
                new HashMap<>();

        if (results != null) {

            for (Object[] result : results) {

                if (result == null ||
                        result.length < 2) {

                    continue;
                }

                if (!(result[0] instanceof Number) ||
                        !(result[1] instanceof Number)) {

                    continue;
                }

                int month =
                        ((Number) result[0])
                                .intValue();

                long count =
                        ((Number) result[1])
                                .longValue();

                monthlyData.put(
                        month,
                        count);
            }
        }

        // -----------------------------------------------------
        // ALWAYS CREATE JANUARY TO DECEMBER
        // -----------------------------------------------------

        List<MonthlyEnrollmentDTO>
                monthlyEnrollment =
                new ArrayList<>();

        for (int month = 1;
             month <= 12;
             month++) {

            String monthName =
                    Month.of(month)
                            .getDisplayName(
                                    TextStyle.SHORT,
                                    Locale.ENGLISH);

            long count =
                    monthlyData.getOrDefault(
                            month,
                            0L);

            monthlyEnrollment.add(
                    new MonthlyEnrollmentDTO(
                            monthName,
                            count));
        }

        return monthlyEnrollment;
    }
}