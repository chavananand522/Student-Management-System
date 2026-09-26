// ============================================================
// FILE:
// src/main/java/com/itvedant/StudentManagement/controller/StudentProfileController.java
// ============================================================

package com.itvedant.StudentManagement.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.itvedant.StudentManagement.model.Users;
import com.itvedant.StudentManagement.reposatory.UserRepository;

@Controller
@RequestMapping("/student/profile")
public class StudentProfileController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public StudentProfileController(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository =
                userRepository;

        this.passwordEncoder =
                passwordEncoder;
    }

    @PostMapping("/password")
    public String changePassword(
            @RequestParam String currentPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        Users user =
                userRepository
                    .findByUserName(
                        authentication.getName())
                    .orElseThrow(() ->
                        new IllegalStateException(
                            "User not found"));

        if (!passwordEncoder.matches(
                currentPassword,
                user.getPassword())) {

            redirectAttributes
                    .addFlashAttribute(
                        "error",
                        "Current password is incorrect.");

            return "redirect:/student/profile";
        }

        if (!newPassword.equals(
                confirmPassword)) {

            redirectAttributes
                    .addFlashAttribute(
                        "error",
                        "New passwords do not match.");

            return "redirect:/student/profile";
        }

        if (newPassword.length() < 6) {

            redirectAttributes
                    .addFlashAttribute(
                        "error",
                        "Password must contain at least 6 characters.");

            return "redirect:/student/profile";
        }

        user.setPassword(
                passwordEncoder.encode(
                        newPassword));

        userRepository.save(user);

        redirectAttributes
                .addFlashAttribute(
                    "message",
                    "Password changed successfully.");

        return "redirect:/student/profile";
    }
}