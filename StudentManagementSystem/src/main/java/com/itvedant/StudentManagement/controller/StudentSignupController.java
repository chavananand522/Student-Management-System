package com.itvedant.StudentManagement.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.itvedant.StudentManagement.dto.StudentSignupDTO;
import com.itvedant.StudentManagement.services.StudentAuthService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/student")
public class StudentSignupController {

    private final StudentAuthService studentAuthService;

    public StudentSignupController(StudentAuthService studentAuthService) {
        this.studentAuthService = studentAuthService;
    }

    // ==============================
    // SHOW SIGNUP PAGE
    // ==============================
    @GetMapping("/signup")
    public String signupPage(Model model) {

        model.addAttribute(
                "studentSignupDTO",
                new StudentSignupDTO()
        );

        return "student/signup";
    }

    // ==============================
    // PROCESS SIGNUP
    // ==============================
    @PostMapping("/signup")
    public String signup(
            @Valid StudentSignupDTO dto,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        // Validation errors
        if (bindingResult.hasErrors()) {
            return "student/signup";
        }

        // Duplicate email
        if (studentAuthService.emailExists(dto.getEmail())) {

            bindingResult.rejectValue(
                    "email",
                    "duplicate",
                    "Email is already registered."
            );

            return "student/signup";
        }

        try {

            studentAuthService.register(dto);

            redirectAttributes.addFlashAttribute(
                    "signupSuccess",
                    "Registration successful! You can now login."
            );

            return "redirect:/login";

        } catch (IllegalArgumentException e) {

            model.addAttribute(
                    "error",
                    e.getMessage()
            );

            return "student/signup";
        }
    }
}