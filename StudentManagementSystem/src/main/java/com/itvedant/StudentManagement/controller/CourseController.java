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

import com.itvedant.StudentManagement.dto.CourseDTO;
import com.itvedant.StudentManagement.services.CourseService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/course")
public class CourseController {

	private static final Logger log = LoggerFactory.getLogger(CourseController.class);

	private final CourseService courseService;

	public CourseController(CourseService courseService) {
		this.courseService = courseService;
	}

	@GetMapping("/new")
	public String showCreateCourse(Model model) {
		log.info("Get /course/new - showing create course page.");
		model.addAttribute("courseDto", new CourseDTO());
		return "add-course";
	}

	@GetMapping("/list")
	public String listCourses(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "3") int size,
			Model model, @RequestParam(value = "message", required = false) String message) {
		log.info("Get /course/list - showing course list page.");

		Page<CourseDTO> courses = courseService.getCourses(page, size);
		model.addAttribute("courses", courses);
		model.addAttribute("message", message);

		return "courses";
	}

	// Save Course
	@PostMapping("/list")
	public String createCourse(@Valid @ModelAttribute("courseDto") CourseDTO courseDTO, BindingResult bindingResult,Model model,
			RedirectAttributes redirectAttribute) {

		log.info("Post /course - create course request received.");

		// 1. Check validation errors
		if (bindingResult.hasErrors()) {
			log.error("Post /course - page return due to validation error");
			return "add-course";
		}

		// 2. Check duplicate course code
		if (courseService.existsByCourseCode(courseDTO.getCourseCode())) {
			log.info("Post /course - Code must be unique.");
			bindingResult.rejectValue("courseCode", "duplicate", "Code must be unique");

			return "add-course";
		}

		// 3. Save course
		courseService.createCourse(courseDTO);

		// 4. Success message
		redirectAttribute.addFlashAttribute("message", "Course is created successfully");

		log.info("Post /course - Course is created successfully");

		// 5. Redirect to list
		return "redirect:/course/list";
	}

	@GetMapping("/{id}")
	public String getCourseByid(@PathVariable Long id, Model model) {
		CourseDTO course = courseService.getCourseById(id);
		model.addAttribute("course", course);
		return "view-course";

	}

	@GetMapping("/{id}/edit")
	public String editCourse(@PathVariable Long id, Model model) {
		CourseDTO courseDto = courseService.getCourseById(id);
		model.addAttribute("courseDto", courseDto);
		return "edit-course";
	}

	@PostMapping("/{id}/update")
	public String updateCourse(@PathVariable Long id, @Valid @ModelAttribute("courseDto") CourseDTO courseDTO,
			BindingResult bindingResult, RedirectAttributes redirectAttribute) {
		
		log.info("Post/{id} /update - update course request received.{}",id);

		// 1. Check validation errors
		if (bindingResult.hasErrors()) {
			log.error("Post/{id} /update - page return due to validation error");
			return "add-course";
		}

		// 2. Check duplicate course code
		if (courseService.existsByCourseCodeAndIdNot(courseDTO.getCourseCode(),id)) {
			log.info("Post/{id} /update - Code must be unique.");
			
			bindingResult.rejectValue("courseCode", "duplicate", "Code must be unique");
			return "edit-course";
		}

		// 3. Save course
		courseService.updateCourse(id,courseDTO);

		// 4. Success message
		redirectAttribute.addFlashAttribute("message", "Course is upadated successfully");

		log.info("Post/{id} /update - updated course successfully");

		// 5. Redirect to list
		return "redirect:/course/list";

	}

}
