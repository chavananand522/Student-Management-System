package com.itvedant.StudentManagement.controller;

import com.itvedant.StudentManagement.dto.AttendanceDTO;
import com.itvedant.StudentManagement.dto.StudentOptionDTO;
import com.itvedant.StudentManagement.model.Attendance;
import com.itvedant.StudentManagement.model.Courses;
import com.itvedant.StudentManagement.model.Enrollment;
import com.itvedant.StudentManagement.services.AttendanceService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PersistenceContext
    private EntityManager entityManager;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    // =====================================================
    // ATTENDANCE PAGE
    // =====================================================

    @GetMapping
    public String attendancePage(

            @RequestParam(value = "date", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date,

            @RequestParam(value = "courseId", required = false)
            Long courseId,

            @RequestParam(value = "search", required = false)
            String search,

            Model model
    ) {

        // --------------------------------------------------
        // Determine effective date + whether user applied a date filter
        // --------------------------------------------------

        boolean dateFilterApplied = (date != null);

        if (date == null) {
            date = LocalDate.now();
        }

        if (search != null) {
            search = search.trim();
            if (search.isEmpty()) search = null;
        }

        // --------------------------------------------------
        // TABLE: records for the selected date (+ course + search)
        // --------------------------------------------------

        StringBuilder jpql = new StringBuilder(
                "SELECT a FROM Attendance a " +
                "JOIN FETCH a.student s " +
                "JOIN FETCH a.course c " +
                "WHERE a.attendanceDate = :date "
        );

        if (courseId != null) {
            jpql.append("AND a.course.id = :courseId ");
        }

        if (search != null) {
            jpql.append(
                    "AND ( LOWER(s.firstName) LIKE :kw " +
                    "   OR LOWER(s.lastName)  LIKE :kw " +
                    "   OR LOWER(CONCAT(s.firstName, ' ', s.lastName)) LIKE :kw ) "
            );
        }

        jpql.append("ORDER BY s.firstName ASC, s.lastName ASC");

        TypedQuery<Attendance> tableQuery =
                entityManager.createQuery(jpql.toString(), Attendance.class)
                        .setParameter("date", date);

        if (courseId != null) tableQuery.setParameter("courseId", courseId);
        if (search != null)   tableQuery.setParameter("kw", "%" + search.toLowerCase() + "%");

        List<Attendance> attendanceList = tableQuery.getResultList();

        // --------------------------------------------------
        // TODAY'S STATS (for the four top cards + the table)
        // --------------------------------------------------

        long presentToday = attendanceList.stream()
                .filter(a -> "PRESENT".equalsIgnoreCase(a.getStatus())).count();
        long absentToday  = attendanceList.stream()
                .filter(a -> "ABSENT".equalsIgnoreCase(a.getStatus())).count();
        long lateToday    = attendanceList.stream()
                .filter(a -> "LATE".equalsIgnoreCase(a.getStatus())).count();
        long totalToday   = presentToday + absentToday + lateToday;

        // --------------------------------------------------
        // CUMULATIVE STATS (for the gauge)
        // If no date filter applied → first record ... today
        // If date filter applied    → first record ... selectedDate
        //
        // NEW WEIGHTAGE:
        //   PRESENT → 100% of a unit
        //   LATE    →  50% of a unit
        //   ABSENT  →   0% of a unit
        // --------------------------------------------------

        LocalDate firstDate = attendanceService.getEarliestAttendanceDate();

        LocalDate rangeStart = (firstDate != null) ? firstDate : date;
        LocalDate rangeEnd   = date;

        long cumulativePresentRaw = 0;
        long cumulativeLateRaw    = 0;
        long cumulativeAbsentRaw  = 0;
        long cumulativeTotal      = 0;

        if (firstDate != null) {

            // PRESENT count
            StringBuilder presentJpql = new StringBuilder(
                    "SELECT COUNT(a) FROM Attendance a " +
                    "WHERE a.attendanceDate BETWEEN :start AND :end " +
                    "AND UPPER(a.status) = 'PRESENT' "
            );
            if (courseId != null) presentJpql.append("AND a.course.id = :courseId ");
            if (search != null) {
                presentJpql.append(
                        "AND ( LOWER(a.student.firstName) LIKE :kw " +
                        "   OR LOWER(a.student.lastName)  LIKE :kw " +
                        "   OR LOWER(CONCAT(a.student.firstName, ' ', a.student.lastName)) LIKE :kw ) "
                );
            }

            TypedQuery<Long> pq = entityManager
                    .createQuery(presentJpql.toString(), Long.class)
                    .setParameter("start", rangeStart)
                    .setParameter("end", rangeEnd);

            if (courseId != null) pq.setParameter("courseId", courseId);
            if (search != null)   pq.setParameter("kw", "%" + search.toLowerCase() + "%");

            cumulativePresentRaw = pq.getSingleResult();

            // LATE count
            StringBuilder lateJpql = new StringBuilder(
                    "SELECT COUNT(a) FROM Attendance a " +
                    "WHERE a.attendanceDate BETWEEN :start AND :end " +
                    "AND UPPER(a.status) = 'LATE' "
            );
            if (courseId != null) lateJpql.append("AND a.course.id = :courseId ");
            if (search != null) {
                lateJpql.append(
                        "AND ( LOWER(a.student.firstName) LIKE :kw " +
                        "   OR LOWER(a.student.lastName)  LIKE :kw " +
                        "   OR LOWER(CONCAT(a.student.firstName, ' ', a.student.lastName)) LIKE :kw ) "
                );
            }

            TypedQuery<Long> lq = entityManager
                    .createQuery(lateJpql.toString(), Long.class)
                    .setParameter("start", rangeStart)
                    .setParameter("end", rangeEnd);

            if (courseId != null) lq.setParameter("courseId", courseId);
            if (search != null)   lq.setParameter("kw", "%" + search.toLowerCase() + "%");

            cumulativeLateRaw = lq.getSingleResult();

            // TOTAL count
            StringBuilder totalJpql = new StringBuilder(
                    "SELECT COUNT(a) FROM Attendance a " +
                    "WHERE a.attendanceDate BETWEEN :start AND :end "
            );
            if (courseId != null) totalJpql.append("AND a.course.id = :courseId ");
            if (search != null) {
                totalJpql.append(
                        "AND ( LOWER(a.student.firstName) LIKE :kw " +
                        "   OR LOWER(a.student.lastName)  LIKE :kw " +
                        "   OR LOWER(CONCAT(a.student.firstName, ' ', a.student.lastName)) LIKE :kw ) "
                );
            }

            TypedQuery<Long> tq = entityManager
                    .createQuery(totalJpql.toString(), Long.class)
                    .setParameter("start", rangeStart)
                    .setParameter("end", rangeEnd);

            if (courseId != null) tq.setParameter("courseId", courseId);
            if (search != null)   tq.setParameter("kw", "%" + search.toLowerCase() + "%");

            cumulativeTotal = tq.getSingleResult();

            cumulativeAbsentRaw = cumulativeTotal - cumulativePresentRaw - cumulativeLateRaw;
            if (cumulativeAbsentRaw < 0) cumulativeAbsentRaw = 0;
        }

        // --------------------------------------------------
        // WEIGHTED PERCENTAGE
        //   PRESENT = 1.0
        //   LATE    = 0.5
        //   ABSENT  = 0.0
        // --------------------------------------------------
        double weightedPresent = cumulativePresentRaw + (cumulativeLateRaw * 0.5);

        double attendancePercentage = 0.0;
        if (cumulativeTotal > 0) {
            attendancePercentage =
                    Math.round((weightedPresent / cumulativeTotal) * 10000.0) / 100.0;
        }

        // For display in the bottom strip we keep the raw presence
        // figure as the "attendance" numerator (rounded down to 2dp).
        double cumulativePresentDisplay =
                Math.round(weightedPresent * 100.0) / 100.0;

        // --------------------------------------------------
        // Courses
        // --------------------------------------------------

        List<Courses> courses =
                entityManager
                        .createQuery(
                                "SELECT c FROM Courses c ORDER BY c.courseName ASC",
                                Courses.class
                        )
                        .getResultList();

        // --------------------------------------------------
        // Students grouped by course (for Mark Attendance modal)
        // --------------------------------------------------

        List<Enrollment> allEnrollments =
                entityManager
                        .createQuery(
                                "SELECT e FROM Enrollment e " +
                                "JOIN FETCH e.student " +
                                "JOIN FETCH e.course " +
                                "ORDER BY e.student.firstName ASC",
                                Enrollment.class
                        )
                        .getResultList();

        Map<Long, List<StudentOptionDTO>> studentsByCourse = new LinkedHashMap<>();

        for (Enrollment e : allEnrollments) {
            if (e.getStudent() == null || e.getCourse() == null) continue;

            Long cid = e.getCourse().getId();

            studentsByCourse
                    .computeIfAbsent(cid, k -> new ArrayList<>())
                    .add(new StudentOptionDTO(
                            e.getStudent().getId(),
                            e.getStudent().getFirstName(),
                            e.getStudent().getLastName()
                    ));
        }

        // --------------------------------------------------
        // Model
        // --------------------------------------------------

        model.addAttribute("attendanceList", attendanceList);
        model.addAttribute("selectedDate", date);
        model.addAttribute("selectedCourseId", courseId);
        model.addAttribute("search", search);

        // Today's stats (top four cards)
        model.addAttribute("presentCount", presentToday);
        model.addAttribute("absentCount",  absentToday);
        model.addAttribute("lateCount",    lateToday);
        model.addAttribute("totalAttendance", totalToday);

        // Cumulative stats (gauge + bottom strip)
        model.addAttribute("cumulativePresent", cumulativePresentDisplay);
        model.addAttribute("cumulativePresentRaw", cumulativePresentRaw);
        model.addAttribute("cumulativeLate",   cumulativeLateRaw);
        model.addAttribute("cumulativeAbsent", cumulativeAbsentRaw);
        model.addAttribute("cumulativeTotal",  cumulativeTotal);
        model.addAttribute("attendancePercentage", attendancePercentage);

        // Extra info for UI (optional)
        model.addAttribute("rangeStart", rangeStart);
        model.addAttribute("rangeEnd",   rangeEnd);
        model.addAttribute("dateFilterApplied", dateFilterApplied);

        model.addAttribute("studentsByCourse", studentsByCourse);
        model.addAttribute("courses", courses);

        return "attendance";
    }

    // =====================================================
    // SAVE
    // =====================================================

    @PostMapping("/save")
    public String saveAttendance(
            @ModelAttribute AttendanceDTO dto,
            RedirectAttributes ra
    ) {
        attendanceService.saveAttendance(dto);
        ra.addFlashAttribute("successMessage", "Attendance marked successfully!");
        return "redirect:/attendance?date=" + dto.getAttendanceDate();
    }

    // =====================================================
    // UPDATE
    // =====================================================

    @PostMapping("/update/{id}")
    public String updateAttendance(
            @PathVariable Long id,
            @ModelAttribute AttendanceDTO dto,
            RedirectAttributes ra
    ) {
        attendanceService.updateAttendance(id, dto);
        ra.addFlashAttribute("successMessage", "Attendance updated successfully!");

        String date = dto.getAttendanceDate() != null
                ? dto.getAttendanceDate().toString()
                : LocalDate.now().toString();

        return "redirect:/attendance?date=" + date;
    }

    // =====================================================
    // DELETE
    // =====================================================

    @PostMapping("/delete/{id}")
    public String deleteAttendance(
            @PathVariable Long id,
            RedirectAttributes ra
    ) {
        attendanceService.deleteAttendance(id);
        ra.addFlashAttribute("successMessage", "Attendance record deleted.");
        return "redirect:/attendance";
    }

    // =====================================================
    // STUDENT ATTENDANCE
    // =====================================================

    @GetMapping("/student/{studentId}")
    @ResponseBody
    public List<Attendance> getStudentAttendance(@PathVariable Long studentId) {
        return attendanceService.getStudentAttendance(studentId);
    }

    @GetMapping("/student/{studentId}/percentage")
    @ResponseBody
    public double getStudentAttendancePercentage(@PathVariable Long studentId) {
        return attendanceService.getStudentAttendancePercentage(studentId);
    }
}