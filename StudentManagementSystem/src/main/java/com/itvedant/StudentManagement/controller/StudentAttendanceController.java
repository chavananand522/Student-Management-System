package com.itvedant.StudentManagement.controller;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.itvedant.StudentManagement.dto.StudentAttendanceDTO;
import com.itvedant.StudentManagement.model.Attendance;
import com.itvedant.StudentManagement.model.Students;
import com.itvedant.StudentManagement.reposatory.StudentRepositiry;
import com.itvedant.StudentManagement.services.StudentAttendanceService;

@Controller
@RequestMapping("/student")
public class StudentAttendanceController {

	private static final Logger log = LoggerFactory.getLogger(StudentAttendanceController.class);

	private final StudentAttendanceService attendanceService;
	private final StudentRepositiry studentRepository;

	public StudentAttendanceController(StudentAttendanceService attendanceService,
			StudentRepositiry studentRepository) {
		this.attendanceService = attendanceService;
		this.studentRepository = studentRepository;
	}

	/**
	 * GET /student/attendance Student's own attendance dashboard.
	 */
	@GetMapping("/attendance")
	public String myAttendance(@RequestParam(required = false) Long courseId,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size,
			Principal principal, Model model) {

		// Resolve logged-in student from email (username)
		String email = principal.getName();
		Students student = studentRepository.findByEmailIgnoreCase(email)
				.orElseThrow(() -> new RuntimeException("Logged-in user is not a registered student: " + email));

		Long studentId = student.getId();
		log.info("Loading attendance dashboard for student id={}", studentId);

		// Fetch attendance data
		Page<Attendance> attendancePage = attendanceService.getStudentAttendance(studentId, courseId, startDate,
				endDate, page, size);

		List<StudentAttendanceDTO> subjectSummary = attendanceService.getSubjectWiseSummary(studentId);

		double overallPercentage = attendanceService.getOverallPercentage(studentId);
		long total = attendanceService.getTotalCount(studentId);
		long present = attendanceService.getPresentCount(studentId);
		long absent = attendanceService.getAbsentCount(studentId);
		long late = attendanceService.getLateCount(studentId);

		// Push to view
		model.addAttribute("student", student);
		model.addAttribute("attendancePage", attendancePage);
		model.addAttribute("subjectSummary", subjectSummary);
		model.addAttribute("overallPercentage", overallPercentage);
		model.addAttribute("totalCount", total);
		model.addAttribute("presentCount", present);
		model.addAttribute("absentCount", absent);
		model.addAttribute("lateCount", late);

		// Keep filter state
		model.addAttribute("selectedCourseId", courseId);
		model.addAttribute("startDate", startDate);
		model.addAttribute("endDate", endDate);

		return "student/student-attendance";
	}
}