package com.itvedant.StudentManagement.controller;


import java.util.List;

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
import org.springframework.web.bind.annotation.ResponseBody;
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

    private static final Logger log =
            LoggerFactory.getLogger(EnrollmentController.class);

    private final CourseService courseService;
    private final StudentService studentService;
    private final EnrollmentService enrollmentService;

    public EnrollmentController(
            CourseService courseService,
            StudentService studentService,
            EnrollmentService enrollmentService) {

        this.courseService = courseService;
        this.studentService = studentService;
        this.enrollmentService = enrollmentService;
    }

    @GetMapping("/showEnroll")
    public String showEnroll(Model model) {

        log.info("GET /enrollments/showEnroll");

        model.addAttribute(
                "enrollmentDto",
                new EnrollmentDTO()
        );

        model.addAttribute(
                "courseList",
                courseService.getAllCourses()
        );

        model.addAttribute(
                "studentList",
                studentService.getAllStudents()
        );

        model.addAttribute(
                "enrolledCourseIds",
                List.of()
        );

        return "enroll-course";
    }

    @GetMapping("/student/{studentId}/courses")
    @ResponseBody
    public List<Long> getStudentEnrolledCourses(
            @PathVariable Long studentId) {

        log.info(
                "GET /enrollments/student/{}/courses",
                studentId
        );

        return enrollmentService
                .findEnrolledCourseIdsByStudent(studentId);
    }

    @GetMapping("/enrollmentList")
    public String enrollmentList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "7") int size,
            Model model) {

        log.info("GET /enrollmentList");

        Page<EnrollmentSummeryDTO> students =
                enrollmentService.getEnrolledStudents(
                        page,
                        size
                );

        model.addAttribute(
                "students",
                students
        );

        return "enrolled-students";
    }

    @PostMapping("/enrollCourse")
    public String enrollCourse(
            @Valid @ModelAttribute("enrollmentDto")
            EnrollmentDTO enrollmentDTO,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttribute) {

        log.info("POST /enrollments/enrollCourse");

        if (bindingResult.hasErrors()) {

            model.addAttribute(
                    "courseList",
                    courseService.getAllCourses()
            );

            model.addAttribute(
                    "studentList",
                    studentService.getAllStudents()
            );

            List<Long> enrolledCourseIds =
                    enrollmentDTO.getStudentId() != null
                            ? enrollmentService.findEnrolledCourseIdsByStudent(
                                    enrollmentDTO.getStudentId())
                            : List.of();

            model.addAttribute(
                    "enrolledCourseIds",
                    enrolledCourseIds
            );

            return "enroll-course";
        }

        try {

            enrollmentService.enrollStudentToCourses(
                    enrollmentDTO
            );

            redirectAttribute.addFlashAttribute(
                    "message",
                    "Enrollment successfully!!"
            );

        } catch (RuntimeException ex) {

            log.warn(
                    "Enrollment failed: {}",
                    ex.getMessage()
            );

            redirectAttribute.addFlashAttribute(
                    "error",
                    ex.getMessage()
            );
        }

        return "redirect:/enrollments/enrollmentList";
    }

    @GetMapping("/getStudentEnrollmentDetails/{id}")
    public String getStudentEnrollmentDetails(
            @PathVariable Long id,
            Model model,
            @RequestParam(
                    defaultValue = "enrollments"
            ) String source) {

        EnrollmentSummeryDTO dto =
                enrollmentService
                        .findEnrolledStudentCourseDetails(id);

        model.addAttribute(
                "enrollmentSummeryDTO",
                dto
        );

        model.addAttribute(
                "source",
                source
        );

        return "enrollment-details";
    }

    @GetMapping("/edit/{studentId}")
    public String showEditForm(
            @PathVariable Long studentId,
            Model model) {

        log.info("GET /enrollments/edit/{}", studentId);

        EnrollmentDTO dto = new EnrollmentDTO();
        dto.setStudentId(studentId);

        List<Long> enrolledCourseIds =
                enrollmentService.findEnrolledCourseIdsByStudent(studentId);

        model.addAttribute("enrollmentDto", dto);
        model.addAttribute("courseList", courseService.getAllCourses());
        model.addAttribute("studentList", studentService.getAllStudents());
        model.addAttribute("enrolledCourseIds", enrolledCourseIds);

        return "edit-enrollment";
    }

    @PostMapping("/edit/{studentId}")
    public String updateStudentCourses(
            @PathVariable Long studentId,
            @RequestParam(value = "courseIds", required = false)
            List<Long> courseIds,
            RedirectAttributes ra) {

        log.info("POST /enrollments/edit/{}", studentId);

        try {

            enrollmentService.updateStudentCourses(
                    studentId,
                    courseIds
            );

            ra.addFlashAttribute(
                    "message",
                    "Courses updated successfully"
            );

        } catch (RuntimeException ex) {

            log.warn(
                    "Course update failed: {}",
                    ex.getMessage()
            );

            ra.addFlashAttribute(
                    "error",
                    ex.getMessage()
            );
        }

        return "redirect:/enrollments/enrollmentList";
    }

    @PostMapping("/delete/{id}")
    public String deleteEnrollment(
            @PathVariable Long id,
            RedirectAttributes ra) {

        log.info(
                "POST /enrollments/delete/{}",
                id
        );

        try {

            enrollmentService.deleteEnrollment(id);

            ra.addFlashAttribute(
                    "message",
                    "Enrollment deleted successfully"
            );

        } catch (RuntimeException ex) {

            ra.addFlashAttribute(
                    "error",
                    ex.getMessage()
            );
        }

        return "redirect:/enrollments/enrollmentList";
    }

    @PostMapping("/deleteByStudent/{studentId}")
    public String deleteByStudent(
            @PathVariable Long studentId,
            RedirectAttributes ra) {

        log.info(
                "POST /enrollments/deleteByStudent/{}",
                studentId
        );

        try {

            enrollmentService
                    .deleteAllEnrollmentsForStudent(
                            studentId
                    );

            ra.addFlashAttribute(
                    "message",
                    "All enrollments removed for the student"
            );

        } catch (RuntimeException ex) {

            ra.addFlashAttribute(
                    "error",
                    ex.getMessage()
            );
        }

        return "redirect:/enrollments/enrollmentList";
    }
}