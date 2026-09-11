package com.itvedant.StudentManagement.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.itvedant.StudentManagement.dto.EnrollmentDTO;
import com.itvedant.StudentManagement.dto.EnrollmentSummeryDTO;
import com.itvedant.StudentManagement.services.CourseService;
import com.itvedant.StudentManagement.services.EnrollmentService;
import com.itvedant.StudentManagement.services.StudentService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/enrollments")
public class EnrollmentController {

	private static final Logger log = LoggerFactory.getLogger(EnrollmentController.class);

	private final CourseService courseService;
	private final StudentService studentService;
	private final EnrollmentService enrollmentService;

	public EnrollmentController(CourseService courseService, StudentService studentService,
			EnrollmentService enrollmentService) {
		this.courseService = courseService;
		this.studentService = studentService;
		this.enrollmentService = enrollmentService;
	}

	@GetMapping("/showEnroll")
	public String showEnroll(Model model) {
		log.info("Get /enrollments/showEnroll - showing Enrollment page.");
		model.addAttribute("enrollmentDto", new EnrollmentDTO());
		model.addAttribute("courseList", courseService.getAllCourses());
		model.addAttribute("studentList", studentService.getAllStudents());
		return "enroll-course";
	}

	@GetMapping("/enrollmentList")
	public String enrollmentList(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "3") int size,
			Model model) {
		log.info("GET /enrollmentList - showing enrolled student list page");
		Page<EnrollmentSummeryDTO> students = enrollmentService.getEnrolledStudents(page, size);
		model.addAttribute("students", students);
		return "enrolled-students";
	}

	@PostMapping("/enrollCourse")
	public String enrollCourse(@Valid @ModelAttribute("enrollmentDto") EnrollmentDTO enrollmentDTO,
			BindingResult bindingResult, Model model, RedirectAttributes redirectAttribute) {

		log.info("Post/enrollments/enrollCourse - Enrollment request received.");

		if (bindingResult.hasErrors()) {
			model.addAttribute("courseList", courseService.getAllCourses());
			model.addAttribute("studentList", studentService.getAllStudents());
			return "enroll-course";
		}

		enrollmentService.enrollStudentToCourses(enrollmentDTO);
		redirectAttribute.addFlashAttribute("message", "Enrollment successfully!!");
		log.info("Post/enrollments/enrollCourse - Enrollment successfully.");
		return "redirect:/enrollments/enrollmentList";

	}

	@GetMapping("/getStudentEnrollmentDetails/{id}")
	public String getStudentEnrollmentDetails(@PathVariable Long id, Model model,
			@RequestParam(defaultValue = "enrollments") String source) {
		EnrollmentSummeryDTO enrollmentSummeryDTO = enrollmentService.findEnrolledStudentCourseDetails(id);
		model.addAttribute("enrollmentSummeryDTO", enrollmentSummeryDTO);
		model.addAttribute("source", source);

		return "enrollment-details";
	}

}