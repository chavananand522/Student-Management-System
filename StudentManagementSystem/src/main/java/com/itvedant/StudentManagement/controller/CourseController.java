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

    private static final Logger log =
            LoggerFactory.getLogger(CourseController.class);

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping("")
    public String redirectToList() {

        log.info(
                "GET /course - redirecting to /course/list"
        );

        return "redirect:/course/list";
    }

    @GetMapping("/new")
    public String showCreateCourse(Model model) {

        log.info(
                "GET /course/new - showing create course page"
        );

        model.addAttribute(
                "courseDto",
                new CourseDTO()
        );

        return "add-course";
    }

    @GetMapping("/list")
    public String listCourses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "7") int size,
            Model model) {

        log.info(
                "GET /course/list - showing course list page"
        );

        Page<CourseDTO> courses =
                courseService.getCourses(page, size);

        model.addAttribute(
                "courses",
                courses
        );

        return "courses";
    }

    @PostMapping("/list")
    public String createCourse(
            @Valid
            @ModelAttribute("courseDto")
            CourseDTO courseDTO,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttribute) {

        log.info(
                "POST /course/list - create course request received"
        );

        if (bindingResult.hasErrors()) {
            return "add-course";
        }

        if (courseService.existsByCourseCode(
                courseDTO.getCourseCode())) {

            bindingResult.rejectValue(
                    "courseCode",
                    "duplicate",
                    "Code must be unique"
            );

            return "add-course";
        }

        try {

            courseService.createCourse(courseDTO);

            redirectAttribute.addFlashAttribute(
                    "message",
                    "Course is created successfully"
            );

        } catch (RuntimeException e) {

            log.error(
                    "Error creating course",
                    e
            );

            redirectAttribute.addFlashAttribute(
                    "errorMessage",
                    e.getMessage()
            );
        }

        return "redirect:/course/list";
    }

    @GetMapping("/{id}")
    public String getCourseById(
            @PathVariable Long id,
            Model model) {

        CourseDTO course =
                courseService.getCourseById(id);

        model.addAttribute(
                "course",
                course
        );

        return "view-course";
    }

    @GetMapping("/{id}/edit")
    public String editCourse(
            @PathVariable Long id,
            Model model) {

        CourseDTO courseDto =
                courseService.getCourseById(id);

        model.addAttribute(
                "courseDto",
                courseDto
        );

        return "edit-course";
    }

    @PostMapping("/{id}/update")
    public String updateCourse(
            @PathVariable Long id,
            @Valid
            @ModelAttribute("courseDto")
            CourseDTO courseDTO,
            BindingResult bindingResult,
            RedirectAttributes redirectAttribute) {

        log.info(
                "POST /course/{}/update - update course request received",
                id
        );

        if (bindingResult.hasErrors()) {
            return "edit-course";
        }

        if (courseService.existsByCourseCodeAndIdNot(
                courseDTO.getCourseCode(),
                id
        )) {

            bindingResult.rejectValue(
                    "courseCode",
                    "duplicate",
                    "Code must be unique"
            );

            return "edit-course";
        }

        try {

            courseService.updateCourse(
                    id,
                    courseDTO
            );

            redirectAttribute.addFlashAttribute(
                    "message",
                    "Course is updated successfully"
            );

        } catch (RuntimeException e) {

            log.error(
                    "Error updating course with id: {}",
                    id,
                    e
            );

            redirectAttribute.addFlashAttribute(
                    "errorMessage",
                    e.getMessage()
            );
        }

        return "redirect:/course/list";
    }

    @PostMapping("/delete/{id}")
    public String deleteCourse(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        log.info(
                "POST /course/delete/{} - delete request received",
                id
        );

        try {

            courseService.deleteCourse(id);

            redirectAttributes.addFlashAttribute(
                    "message",
                    "Course deleted successfully"
            );

        } catch (Exception e) {

            log.error(
                    "Error deleting course with id: {}",
                    id,
                    e
            );

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Error deleting course: " + e.getMessage()
            );
        }

        return "redirect:/course/list";
    }
}